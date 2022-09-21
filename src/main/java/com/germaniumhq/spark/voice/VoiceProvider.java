package com.germaniumhq.spark.voice;

import java.io.InputStream;
import java.util.List;

public interface VoiceProvider {
    /**
     * The provider name to display in the combo box.
     */
    String getProviderName();

    /**
     * Initialize the voice provider. This will get called whenever the user
     * switches the provider in the UI.
     */
    void refresh();

    /**
     * Obtains a list of languages that offer voices.
     */
    List<VoiceLanguage> getAvailableLanguages();

    /**
     * Gets the available characters for the given language.
     */
    List<VoiceCharacter> getAvailableCharacters(VoiceLanguage language);

    /**
     * Obtains a list of available sentiments for a user. At least a single entry
     * with DEFAULT must be returned, regardless of the provider.
     */
    List<VoiceSentiment> getAvailableSentiments(VoiceCharacter voiceCharacter);

    /**
     * Renders the given voice into something that can be played.
    */
    InputStream renderVoice(VoiceCharacter character, VoiceSentiment sentiment, float pitchMultiplier, String text);
}
