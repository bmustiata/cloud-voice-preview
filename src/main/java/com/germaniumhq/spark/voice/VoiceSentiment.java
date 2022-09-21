package com.germaniumhq.spark.voice;

import java.util.Objects;

/**
 * A sentiment that can be attached to a voice.
 */
public class VoiceSentiment {

    // when DEFAULT it's set we don't send any sentiment (when possible)
    public static final VoiceSentiment DEFAULT = new VoiceSentiment("__default__", "default");

    // internal ID used for the REST calls
    private String id;

    // display name in the UI
    private String name;

    public VoiceSentiment(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceSentiment that = (VoiceSentiment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getName();
    }
}
