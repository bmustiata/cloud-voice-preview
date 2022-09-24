package com.germaniumhq.spark.voice;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IbmVoiceParserTest {
    @Test
    public void verifyIfLanguageGetsExtractedCorrectly() {
        assertEquals("fr-FR", IbmVoiceProvider.getVoiceLanguage("fr-FR_NicolasV3Voice"));
        assertEquals("Nicolas (V3)", IbmVoiceProvider.getVoiceName("fr-FR_NicolasV3Voice"));
        assertEquals("en-GB", IbmVoiceProvider.getVoiceLanguage("en-GB_KateVoice"));
        assertEquals("Kate", IbmVoiceProvider.getVoiceName("en-GB_KateVoice"));
    }

    @Test
    public void verifyIfJsonLoadingWorks() throws FileNotFoundException {
        List<VoiceCharacter> characterList = IbmVoiceProvider.loadVoiceCharactersFromInputStream(
                "http://localhost/...",
                new FileInputStream("src/test/resources/ibm-voice-list.json"));

        Assert.assertNotNull(characterList);
        Assert.assertEquals(43, characterList.size());

        VoiceCharacter character = TestUtils.findCharacterById(characterList, "fr-FR_NicolasV3Voice");
        Assert.assertEquals("fr-FR_NicolasV3Voice", character.getId());
        Assert.assertEquals("Nicolas (V3)", character.getName());
        Assert.assertEquals("Nicolas: French (français) male voice. Dnn technology.", character.getDescription());
        Assert.assertEquals("fr-FR", character.getVoiceLanguage().getId());
        Assert.assertEquals("fr-FR", character.getVoiceLanguage().getName());
    }
}
