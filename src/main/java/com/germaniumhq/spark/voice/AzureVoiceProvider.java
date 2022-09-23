package com.germaniumhq.spark.voice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.germaniumhq.spark.voice.rest.azure.AzureVoiceDescriptionVo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AzureVoiceProvider implements VoiceProvider {
    @Override
    public String getProviderName() {
        return "Azure'";
    }

    @Override
    public void refresh() {

    }

    @Override
    public List<VoiceLanguage> getAvailableLanguages() {
        return null;
    }

    @Override
    public List<VoiceCharacter> getAvailableCharacters(VoiceLanguage language) {
        return null;
    }

    @Override
    public List<VoiceSentiment> getAvailableSentiments(VoiceCharacter voiceCharacter) {
        return null;
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

                result.add(voiceCharacter);
            }

            return result;
        } catch (IOException e) {
            throw new IllegalStateException("unable to read voice information from " + url, e);
        }
    }
}
