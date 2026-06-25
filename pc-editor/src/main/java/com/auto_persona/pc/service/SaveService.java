package com.auto_persona.pc.service;

import com.auto_persona.pc.model.SaveData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveService {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public SaveData loadFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        return gson.fromJson(json, SaveData.class);
    }

    public SaveData loadFromResource(String path) throws IOException {
        var stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) throw new FileNotFoundException("Resource not found: " + path);
        String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(json, SaveData.class);
    }

    public String toJson(SaveData data) {
        return gson.toJson(data);
    }

    public void saveToFile(File file, SaveData data) throws IOException {
        Files.writeString(file.toPath(), toJson(data), StandardCharsets.UTF_8);
    }
}
