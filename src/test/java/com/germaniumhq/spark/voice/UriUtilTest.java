package com.germaniumhq.spark.voice;

import org.junit.Test;

import static org.junit.Assert.*;

public class UriUtilTest {
    @Test
    public void createUriString() {
        assertEquals("http://wut.com/wut/mut",
                UriUtil.createUriString("http://wut.com/", "wut/mut"));
        assertEquals("http://wut.com/wut/mut",
                UriUtil.createUriString("http://wut.com", "/wut/mut"));
        assertEquals("http://wut.com/wut/mut",
                UriUtil.createUriString("http://wut.com/", "/wut/mut"));
        assertEquals("http://wut.com/wut/mut",
                UriUtil.createUriString("http://wut.com", "wut/mut"));
    }
}