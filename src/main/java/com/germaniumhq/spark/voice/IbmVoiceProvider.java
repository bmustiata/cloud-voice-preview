package com.germaniumhq.spark.voice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.germaniumhq.spark.Settings;
import com.germaniumhq.spark.voice.rest.ibm.IbmVoiceDescriptionVo;
import com.germaniumhq.spark.voice.rest.ibm.IbmVoicesListRequestVo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IbmVoiceProvider implements VoiceProvider {
    static Pattern IBM_VOICE_RE = Pattern.compile("^(\\w+-\\w+)_(.+?)(V\\d+)?Voice$");

    private Map<VoiceLanguage, List<VoiceCharacter>> voices;

    @Override
    public String getProviderName() {
        return "IBM";
    }

    @Override
    public void refresh() {
        List<VoiceCharacter> ibmVoices = ibmVoicesListRestCall();
        this.voices = new LinkedHashMap<>();

        for (VoiceCharacter voiceCharacter: ibmVoices) {
            List<VoiceCharacter> voiceList = this.voices.computeIfAbsent(voiceCharacter.getVoiceLanguage(), (x) -> new ArrayList<>());
            voiceList.add(voiceCharacter);
        }
    }

    @Override
    public List<VoiceLanguage> getAvailableLanguages() {
        return new ArrayList<>(this.voices.keySet());
    }

    @Override
    public List<VoiceCharacter> getAvailableCharacters(VoiceLanguage language) {
        return this.voices.get(language);
    }


    @Override
    public List<VoiceSentiment> getAvailableSentiments(VoiceCharacter voiceCharacter) {
        return Collections.singletonList(VoiceSentiment.DEFAULT);
    }

    @Override
    public InputStream renderVoice(VoiceCharacter character, VoiceSentiment sentiment, float pitchMultiplier, String text) {
        String url = buildRenderVoiceUrl(character);

        Map<String, String> data = new HashMap<>();
        data.put("text", text);

        try {
            String json = new ObjectMapper().writeValueAsString(data);
            return performRestCall(url, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return this.getProviderName();
    }

    static String getVoiceName(String ibmVoice) {
        Matcher matcher = IBM_VOICE_RE.matcher(ibmVoice);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                    "unable to parse `%s` as a language identifier",
                    ibmVoice
            ));
        }

        // we have a match including a version specifier
        if (matcher.group(3) != null) {
            return String.format("%s (%s)", matcher.group(2), matcher.group(3));
        }

        return matcher.group(2);
    }

    static String getVoiceLanguage(String ibmVoice) {
        Matcher matcher = IBM_VOICE_RE.matcher(ibmVoice);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                    "unable to parse `%s` as a language identifier",
                    ibmVoice
            ));
        }

        return matcher.group(1);
    }

    private List<VoiceCharacter> ibmVoicesListRestCall() {
        String url = buildVoiceListingUrl();
        InputStream resultInputStream = performRestCall(url, null);

        return loadVoiceCharactersFromInputStream(url, resultInputStream);
    }

    /**
     * Parses the JSON and creates the VoiceCharacter objects we need with their
     * assigned VoiceLanguage.
     */
    static List<VoiceCharacter> loadVoiceCharactersFromInputStream(String url, InputStream resultInputStream) {
        try {
            IbmVoicesListRequestVo voices = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(resultInputStream, IbmVoicesListRequestVo.class);

            List<VoiceCharacter> result = new ArrayList<>();

            for (IbmVoiceDescriptionVo ibmVoice: voices.getVoices()) {
                String name = getVoiceName(ibmVoice.getName());
                VoiceLanguage voiceLanguage = VoiceLanguage.fromString(ibmVoice.getLanguage());
                VoiceCharacter voiceCharacter = new VoiceCharacter(ibmVoice.getName(), name, voiceLanguage);
                voiceCharacter.setDescription(ibmVoice.getDescription());

                result.add(voiceCharacter);
            }

            return result;
        } catch (IOException e) {
            throw new IllegalStateException("unable to read voice information from " + url, e);
        }
    }

    private InputStream performRestCall(String url, String text) {
        // https://api.eu-de.text-to-speech.watson.cloud.ibm.com/instances/0ea5f5c2-916c-49bc-8635-97d1fabe5e8b/
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", getBasicAuthenticationHeader("apikey", Settings.INSTANCE.getIbmToken()));

        if (text != null) {
            builder = builder.POST(HttpRequest.BodyPublishers.ofString(text))
                    .header("Content-Type", "application/json")
                    .header("Accept", "audio/mp3");
        } else {
            builder = builder.GET();
        }

        HttpRequest request = builder.build();

        try {
            HttpResponse<InputStream> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() / 100 != 2) {
                throw new IllegalArgumentException(String.format(
                        "unable ot read voices list from %s, return code: %d",
                        url, response.statusCode()
                ));
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("unable to read available voices from url");
        }
    }

    private static String buildVoiceListingUrl() {
        String ibmEndpoint = ḡetIbmEndpointFromSettings();

        return String.format("%sv1/voices", ibmEndpoint);
    }

    private static String buildRenderVoiceUrl(VoiceCharacter character) {
        String ibmEndpoint = ḡetIbmEndpointFromSettings();

        return String.format(
                "%sv1/synthesize?voice=%s",
                ibmEndpoint,
                URLEncoder.encode(character.getId(), StandardCharsets.UTF_8));
    }

    private static String ḡetIbmEndpointFromSettings() {
        String ibmEndpoint = Settings.INSTANCE.getIbmEndpoint();

        if (ibmEndpoint == null || ibmEndpoint.isBlank()) {
            throw new IllegalArgumentException("IBM provider not configured");
        }

        ibmEndpoint = ibmEndpoint.strip();

        if (!ibmEndpoint.endsWith("/")) {
            ibmEndpoint += "/";
        }
        return ibmEndpoint;
    }

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

}
