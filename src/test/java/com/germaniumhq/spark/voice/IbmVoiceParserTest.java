package com.germaniumhq.spark.voice;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IbmVoiceParserTest {
    @Test
    public void verifyIfLanguageGetsExtractedCorrectly() {
        assertEquals("fr-FR", IbmVoiceProvider.getVoiceLanguage("fr-FR_NicolasV3Voice"));
        assertEquals("Nicolas (V3)", IbmVoiceProvider.getVoiceName("fr-FR_NicolasV3Voice"));
        assertEquals("en-GB", IbmVoiceProvider.getVoiceLanguage("en-GB_KateVoice"));
        assertEquals("Kate", IbmVoiceProvider.getVoiceName("en-GB_KateVoice"));
    }
}
