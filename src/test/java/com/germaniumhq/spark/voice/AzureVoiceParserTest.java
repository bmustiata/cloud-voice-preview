package com.germaniumhq.spark.voice;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static com.germaniumhq.spark.voice.TestUtils.findCharacterById;

public class AzureVoiceParserTest {
    @Test
    public void verifyIfJsonParsingWorks() throws FileNotFoundException {
        List<VoiceCharacter> result = AzureVoiceProvider.loadVoiceCharactersFromInputStream(
                "http://localhost/...",
                new FileInputStream("src/test/resources/azure-voice-list.json"));

        Assert.assertNotNull(result);
        Assert.assertEquals(352, result.size());

        VoiceCharacter character = findCharacterById(result, "en-US-JennyNeural");

        Assert.assertEquals("en-US-JennyNeural", character.getId());
        Assert.assertEquals("Jenny", character.getName());
        Assert.assertEquals("Microsoft Server Speech Text to Speech Voice (en-US, JennyNeural)", character.getDescription());
        Assert.assertEquals("en-US", character.getVoiceLanguage().getId());
        Assert.assertEquals("English (United States)", character.getVoiceLanguage().getName());
        Assert.assertEquals(15, character.getSentiments().size());
    }
}
