package com.germaniumhq.spark;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundMediaPlayer implements LineListener {
    private boolean playing;
    private SourceDataLine audioClip;

    public synchronized void play(InputStream inputStream) {
        try {
            new Player(inputStream).play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void play(byte[] data) {
        try {
            new Player(new ByteArrayInputStream(data)).play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void stop() {
        if (audioClip == null) {
            return;
        }

        audioClip.stop();
        audioClip = null;
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public synchronized void update(LineEvent event) {
        if (LineEvent.Type.START == event.getType()) {
            this.playing = true;
        } else if (LineEvent.Type.STOP == event.getType()) {
            this.audioClip.close();
            this.audioClip = null;
            this.playing = false;
        }
    }
}
