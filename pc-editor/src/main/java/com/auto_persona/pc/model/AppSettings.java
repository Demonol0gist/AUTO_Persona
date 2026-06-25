package com.auto_persona.pc.model;

import java.util.Map;

public class AppSettings {
    public int bgmVolume = 70;
    public int voiceVolume = 80;
    public boolean isMuted;
    public Map<String, String> longTermMemory = Map.of();
    public boolean ttsEnabled = true;
    public boolean presetRepliesEnabled = true;
}
