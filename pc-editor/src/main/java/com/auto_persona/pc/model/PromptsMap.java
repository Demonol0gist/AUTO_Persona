package com.auto_persona.pc.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class PromptsMap {
    @SerializedName("sister-null") public PromptData sisterNull;
    @SerializedName("sister-verylow") public PromptData sisterVerylow;
    @SerializedName("sister-low") public PromptData sisterLow;
    @SerializedName("sister-medium") public PromptData sisterMedium;
    @SerializedName("sister-high") public PromptData sisterHigh;
    @SerializedName("sister-dilei") public PromptData sisterDilei;
    @SerializedName("sister-kindergarten") public PromptData sisterKindergarten;
    @SerializedName("sister-tutor") public PromptData sisterTutor;
    @SerializedName("sister-kemonomimi") public PromptData sisterKemonomimi;
    @SerializedName("sister-kemonomimi-cat") public PromptData sisterKemonomimiCat;

    public static class PromptData {
        public String spec = "chara_card_v2";
        @SerializedName("spec_version") public String specVersion = "2.0";
        public PromptInnerData data = new PromptInnerData();
    }

    public static class PromptInnerData {
        public String name = "Yuki";
        public String description = "";
        public String personality = "";
        public String scenario = "";
        @SerializedName("creator_notes") public String creatorNotes = "";
        @SerializedName("first_mes") public String firstMes;
        public List<String> tags = new ArrayList<>();
    }
}
