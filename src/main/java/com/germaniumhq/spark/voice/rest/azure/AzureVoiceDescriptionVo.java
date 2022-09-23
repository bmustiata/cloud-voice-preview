package com.germaniumhq.spark.voice.rest.azure;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 *  {
 * 		"Name": "Microsoft Server Speech Text to Speech Voice (en-US, SaraNeural)",
 * 		"DisplayName": "Sara",
 * 		"LocalName": "Sara",
 * 		"ShortName": "en-US-SaraNeural",
 * 		"Gender": "Female",
 * 		"Locale": "en-US",
 * 		"LocaleName": "English (United States)",
 * 		"StyleList": [
 * 			"angry",
 * 			"cheerful",
 * 			"excited",
 * 			"friendly",
 * 			"hopeful",
 * 			"sad",
 * 			"shouting",
 * 			"terrified",
 * 			"unfriendly",
 * 			"whispering"
 * 		],
 * 		"SampleRateHertz": "24000",
 * 		"VoiceType": "Neural",
 * 		"Status": "GA",
 * 		"WordsPerMinute": "157"
 *    },
 */
public class AzureVoiceDescriptionVo {
    @JsonProperty("Name")
    private String name; // description in our case

    @JsonProperty("DisplayName")
    private String displayName; // name

    @JsonProperty("ShortName")
    private String shortName; // id

    @JsonProperty("Locale")
    private String locale;

    @JsonProperty("LocaleName")
    private String localeName;

    @JsonProperty("StyleList")
    private List<String> styleList; // sentiments

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public List<String> getStyleList() {
        return styleList;
    }

    public void setStyleList(List<String> styleList) {
        this.styleList = styleList;
    }
}
