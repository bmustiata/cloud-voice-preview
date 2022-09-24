package com.germaniumhq.spark.voice;

import java.util.List;

public class TestUtils {
    public static VoiceCharacter findCharacterById(List<VoiceCharacter> characterList, String chararcterId) {
        for (VoiceCharacter voiceCharacter: characterList) {
            if (chararcterId.equals(voiceCharacter.getId())) {
                return voiceCharacter;
            }
        }

        throw new IllegalArgumentException(String.format(
                "unable to find character %s in %s",
                chararcterId,
                characterList
        ));
    }
}
