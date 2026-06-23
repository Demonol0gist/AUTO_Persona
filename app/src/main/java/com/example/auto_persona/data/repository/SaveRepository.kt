package com.example.auto_persona.data.repository

import android.content.Context
import android.net.Uri
import com.example.auto_persona.data.model.SaveData
import kotlinx.serialization.json.Json

class SaveRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        prettyPrint = true
        encodeDefaults = true
    }

    fun loadFromAssets(context: Context, filename: String = "存档.json"): Result<SaveData> {
        return try {
            val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
            val saveData = json.decodeFromString<SaveData>(jsonString)
            Result.success(saveData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parseJson(jsonString: String): Result<SaveData> {
        return try {
            val saveData = json.decodeFromString<SaveData>(jsonString)
            Result.success(saveData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun toPrettyJson(saveData: SaveData): String {
        return json.encodeToString(SaveData.serializer(), saveData)
    }

    fun importFromUri(context: Context, uri: Uri): Result<SaveData> {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: return Result.failure(IllegalStateException("Cannot open file"))
            parseJson(jsonString)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportToUri(context: Context, uri: Uri, saveData: SaveData): Result<Unit> {
        return try {
            val jsonString = toPrettyJson(saveData)
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                it.write(jsonString)
            } ?: return Result.failure(IllegalStateException("Cannot write file"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
