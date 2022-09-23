package com.germaniumhq.spark.voice;

import java.util.Objects;

public class VoiceLanguage {
    private final String id;
    private final String name;

    public VoiceLanguage(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static VoiceLanguage fromString(String language) {
        return new VoiceLanguage(language, language);
    }

    public static VoiceLanguage fromString(String id, String language) {
        return new VoiceLanguage(id, language);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceLanguage that = (VoiceLanguage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
