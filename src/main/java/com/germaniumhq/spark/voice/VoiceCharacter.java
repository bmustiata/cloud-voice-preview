package com.germaniumhq.spark.voice;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class VoiceCharacter {
    private String id; // technical id used in other calls
    private String name; // display name

    private String description; // eventual description of the character

    private VoiceLanguage voiceLanguage; // voice language of the character

    private Set<VoiceSentiment> sentiments = new LinkedHashSet<>();

    public VoiceCharacter(String id, String name, VoiceLanguage voiceLanguage) {
        this.id = id;
        this.name = name;
        this.voiceLanguage = voiceLanguage;
        this.sentiments.add(VoiceSentiment.DEFAULT);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VoiceLanguage getVoiceLanguage() {
        return voiceLanguage;
    }

    public void setVoiceLanguage(VoiceLanguage voiceLanguage) {
        this.voiceLanguage = voiceLanguage;
    }

    public Set<VoiceSentiment> getSentiments() {
        return sentiments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceCharacter that = (VoiceCharacter) o;
        return id.equals(that.id);
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
