package com.germaniumhq.spark.voice;

import com.germaniumhq.spark.SoundMediaPlayer;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SoundMediaPlayerTest {
    @Test
    public void testStartStop() throws FileNotFoundException, InterruptedException {

        File file = new File("C:\\game\\Mati\\Assets\\Partition(e533ffb2-d350-4db5-b280-25f5cfa8c9f4)\\Each_Character_Speaks_fabian_0005(30DC).wav");
        new SoundMediaPlayer().play(new FileInputStream(file));
        Thread.sleep(5000);
    }
}
