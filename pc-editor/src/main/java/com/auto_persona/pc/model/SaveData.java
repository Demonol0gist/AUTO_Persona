package com.auto_persona.pc.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SaveData {
    public String version = "1.0.0";
    public long timestamp;
    public String exportDate = "";
    public int slotId = 1;
    public GameContainer data = new GameContainer();

    public static class GameContainer {
        public GameData gameData = new GameData();
        public List<DiaryEntry> diary = List.of();
        public AppSettings settings = new AppSettings();
        public PromptsMap prompts = new PromptsMap();
    }
}
