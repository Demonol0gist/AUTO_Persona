package com.auto_persona.pc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {
    public String version = "1.0.0";
    public long timestamp;
    public PlayerInfo playerInfo = new PlayerInfo();
    public String gender = "brother";
    public CharacterStats characterStats = new CharacterStats();
    public CharacterSystemData characterSystemData = new CharacterSystemData();
    public PlayerProgress playerProgress = new PlayerProgress();
    public QuestSystem questSystem = new QuestSystem();
    public GlobalLevelSystem globalLevelSystem = new GlobalLevelSystem();
    public TimeSystem timeSystem = new TimeSystem();
    public Inventory inventory = new Inventory();
    public GameSettings settings = new GameSettings();
    public DateHistory dateHistory = new DateHistory();
    public TravelSystem travelSystem = new TravelSystem();
    public ShopSystem shopSystem = new ShopSystem();
    public String currentOutfit;
    public int kemonomimiReminderCount;

    // ---- Sub-classes ----

    public static class PlayerInfo {
        public String name = "";
        public String birthday = "";
        public String identity = "";
        public boolean hasCollected;
    }

    public static class CharacterStats {
        public int affection;
        public int trust;
    }

    public static class CharacterSystemData {
        public CharacterStats stats = new CharacterStats();
        public CharacterInfo character = new CharacterInfo();
        public boolean diaryLeakTriggered;
        public boolean yandereEventTriggered;
        public boolean giftPreferenceEventTriggered;
        public boolean affection30EventTriggered;
        public boolean affection70EventTriggered;
        public int affectionCap = 1000;
        public int trustMin;
        public int trustMax = 100;
        public int kemonomimiShyness;
        public boolean kemonomimiCatForm;
        public String currentPersonaPackId;
        public boolean diaryLeakRecovered;
        public boolean yandereRecovered;
        public boolean outfit19Unlocked;
    }

    public static class CharacterInfo {
        public String name = "YUKI";
        public String personality = "";
        public List<String> favoriteGifts = new ArrayList<>();
        public List<String> specialEvents = new ArrayList<>();
        public String currentMood = "normal";
    }

    public static class PlayerProgress {
        public int coins;
    }

    public static class QuestSystem {
        public int unlockedLevel;
    }

    public static class GlobalLevelSystem {
        public int globalLevel = 1;
        public int globalExp;
    }

    public static class TimeSystem {
        public int currentDay = 1;
        public int dailyActions = 99999;
        public int actionsUsed;
        public List<String> specialEvents = new ArrayList<>();
        public String gameStartDate = "";
    }

    public static class Inventory {
        public List<InventoryItem> items = new ArrayList<>();
        public Equipment equipment = new Equipment();
    }

    public static class InventoryItem {
        public String id = "";
        public String name = "";
        public int quantity;
        public String type = "";
    }

    public static class Equipment {
        public String weapon;
        public String armor;
        public String accessory;
    }

    public static class GameSettings {
        public double musicVolume = 0.7;
        public double soundVolume = 0.8;
        public boolean autoSave = true;
    }

    public static class DateHistory {
        public int custom, cafe, park, restaurant, beach, nightmarket, nightpath;
    }

    public static class TravelSystem {
        public boolean hasShownNightDialogue;
        public Map<String, String> travelHistory = new HashMap<>();
        public List<String> unlockedDestinations = new ArrayList<>();
        public List<String> currentSpots = new ArrayList<>();
        public String travelState, currentDestination, destinationCityName, currentCity;
        public boolean isTravelMode;
        public String travelStartTime, travelEndTime;
    }

    public static class ShopSystem {
        public OutfitPurchases purchases = new OutfitPurchases();
        public GiftInventory giftInventory = new GiftInventory();
        public Map<String, Integer> travelInventory = new HashMap<>();
        public String currentTab = "travel";
    }

    public static class OutfitPurchases {
        public boolean outfit1, outfit2, outfit3, outfit4, outfit5,
               outfit6, outfit7, outfit8, outfit9, outfit10,
               outfit11, outfit12, outfit13, outfit14, outfit15,
               outfit16, outfit17, outfit18, outfit19, outfit20;
    }

    public static class GiftInventory {
        public int gift1, gift2, gift3, gift4, gift5, gift6,
                   gift7, gift8, gift9, gift10, gift11, gift12;
    }
}
