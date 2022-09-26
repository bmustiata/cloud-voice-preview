package com.germaniumhq.spark.voice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.germaniumhq.spark.Settings;
import com.germaniumhq.spark.voice.rest.azure.AzureVoiceDescriptionVo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

import static com.germaniumhq.spark.voice.UriUtil.createUri;

public class AzureVoiceProvider implements VoiceProvider {
    private Map<VoiceLanguage, List<VoiceCharacter>> voices;

    @Override
    public String getProviderName() {
        return "Azure";
    }

    @Override
    public void refresh() {
        List<VoiceCharacter> ibmVoices = azureVoicesListRestCall();
        this.voices = new LinkedHashMap<>();

        for (VoiceCharacter voiceCharacter: ibmVoices) {
            List<VoiceCharacter> voiceList = this.voices.computeIfAbsent(voiceCharacter.getVoiceLanguage(), (x) -> new ArrayList<>());
            voiceList.add(voiceCharacter);
        }
    }

    private List<VoiceCharacter> azureVoicesListRestCall() {
        // https://germanywestcentral.tts.speech.microsoft.com/cognitiveservices/voices/list
        String azureEndpoint = Settings.INSTANCE.getAzureEndpoint();

        HttpRequest httpRequest = createListVoicesHttpRequest(azureEndpoint);

        try (InputStream inputStream = IbmVoiceProvider.performHttpRequest(azureEndpoint, httpRequest)) {
            return loadVoiceCharactersFromInputStream(azureEndpoint, inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to read " + azureEndpoint, e);
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
        if (voiceCharacter == null) {
            return Collections.singletonList(VoiceSentiment.DEFAULT);
        }

        return new ArrayList<>(voiceCharacter.getSentiments());
    }


    @Override
    public InputStream renderVoice(VoiceCharacter character, VoiceSentiment sentiment, float pitchMultiplier, String text) {
        String xmlContent = createXmlContent(character, sentiment, pitchMultiplier, text);

        URI uri = createUri(Settings.INSTANCE.getAzureEndpoint(), "/v1");
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(xmlContent))
                .uri(uri)
                .header("Ocp-Apim-Subscription-Key", Settings.INSTANCE.getAzureToken())
                .header("Content-Type", "application/ssml+xml")
                .header("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3");

        HttpRequest request = builder.build();

        return IbmVoiceProvider.performHttpRequest(uri.toString(), request);
    }

    private String createXmlContent(VoiceCharacter character, VoiceSentiment sentiment, float pitchMultiplier, String text) {
        StringBuilder result = new StringBuilder();

        result.append("<speak version='1.0' xml:lang='en-UK' xmlns:mstts=\"https://www.w3.org/2001/mstts\" >")
                .append("<voice name='")
                .append(character.getId())
                .append("'>");

        if (sentiment != null && sentiment != VoiceSentiment.DEFAULT) {
            result.append("<mstts:express-as style=\"")
                    .append(sentiment.getId())
                    .append("\">");
        }

        result.append("<![CDATA[")
                .append(text)
                .append("]]>");

        if (sentiment != null && sentiment != VoiceSentiment.DEFAULT) {
            result.append("</mstts:express-as>");
        }

        result.append("</voice></speak>");

        return result.toString();
    }

    public static List<VoiceCharacter> loadVoiceCharactersFromInputStream(String url, InputStream resultInputStream) {
        try {
            List<AzureVoiceDescriptionVo> voices = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(resultInputStream, new TypeReference<>() {});

            List<VoiceCharacter> result = new ArrayList<>();

            for (AzureVoiceDescriptionVo azureVoice: voices) {
               VoiceLanguage voiceLanguage = VoiceLanguage.fromString(azureVoice.getLocale(), azureVoice.getLocaleName());
                VoiceCharacter voiceCharacter = new VoiceCharacter(azureVoice.getShortName(), azureVoice.getDisplayName(), voiceLanguage);
                voiceCharacter.setDescription(azureVoice.getName());

                if (azureVoice.getStyleList() != null) {
                    for (String style: azureVoice.getStyleList()) {
                        voiceCharacter.getSentiments().add(new VoiceSentiment(style, style));
                    }
                }

                result.add(voiceCharacter);
            }

            return result;
        } catch (IOException e) {
            throw new IllegalStateException("unable to read voice information from " + url, e);
        }
    }

    /**
     * Creates the HTTP GET request, using the Auth credentials for Azure.
     * @param azureEndpoint
     * @return
     */
    private static HttpRequest createListVoicesHttpRequest(String azureEndpoint) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(createUri(azureEndpoint, "/voices/list"))
                .header("Ocp-Apim-Subscription-Key", Settings.INSTANCE.getAzureToken())
                .GET()
                .build();

        return httpRequest;
    }

    @Override
    public String toString() {
        return getProviderName();
    }
}
