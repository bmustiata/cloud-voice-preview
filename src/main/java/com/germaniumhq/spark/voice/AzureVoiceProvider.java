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

        HttpRequest httpRequest = createHttpRequest(azureEndpoint);

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
        return new ArrayList<>(voiceCharacter.getSentiments());
    }


    @Override
    public InputStream renderVoice(VoiceCharacter character, VoiceSentiment sentiment, float pitchMultiplier, String text) {
        return null;
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
    private static HttpRequest createHttpRequest(String azureEndpoint) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(azureEndpoint))
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
