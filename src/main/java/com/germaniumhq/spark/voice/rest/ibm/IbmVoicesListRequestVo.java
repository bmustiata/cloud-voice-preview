package com.germaniumhq.spark.voice.rest.ibm;

import java.util.List;

public class IbmVoicesListRequestVo {
    private List<IbmVoiceDescriptionVo> voices;

    public List<IbmVoiceDescriptionVo> getVoices() {
        return voices;
    }

    public void setVoices(List<IbmVoiceDescriptionVo> voices) {
        this.voices = voices;
    }
}
