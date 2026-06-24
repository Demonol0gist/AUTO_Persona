package com.example.auto_persona.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auto_persona.data.ai.AiConfig
import com.example.auto_persona.data.ai.AiService
import com.example.auto_persona.data.model.PromptData
import com.example.auto_persona.data.model.PromptInnerData
import com.example.auto_persona.data.repository.SaveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class AiViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("ai_config", 0)
    private val aiService = AiService()
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AiConfig> = _config.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedPersona = MutableStateFlow<PromptData?>(null)
    val generatedPersona: StateFlow<PromptData?> = _generatedPersona.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun loadConfig(): AiConfig {
        return AiConfig(
            apiKey = prefs.getString("api_key", "") ?: "",
            baseUrl = prefs.getString("base_url", "https://api.deepseek.com") ?: "https://api.deepseek.com",
            model = prefs.getString("model", "deepseek-v4-pro") ?: "deepseek-v4-pro"
        )
    }

    fun updateApiKey(key: String) {
        _config.value = _config.value.copy(apiKey = key)
        prefs.edit().putString("api_key", key).apply()
    }

    fun updateBaseUrl(url: String) {
        _config.value = _config.value.copy(baseUrl = url)
        prefs.edit().putString("base_url", url).apply()
    }

    fun updateModel(model: String) {
        _config.value = _config.value.copy(model = model)
        prefs.edit().putString("model", model).apply()
    }

    fun generatePersona(userInput: String) {
        val cfg = _config.value
        if (cfg.apiKey.isBlank()) {
            _error.value = "请先配置 API Key"
            return
        }
        if (userInput.isBlank()) {
            _error.value = "请输入角色描述"
            return
        }

        _isGenerating.value = true
        _error.value = null
        _generatedPersona.value = null

        viewModelScope.launch {
            try {
                val templateExample = """
{
  "name": "YUKI",
  "personality": "温柔内向，逐渐熟络",
  "scenario": "你是用户的妹妹，与用户同居",
  "tags": ["女生", "妹妹", "日常", "中文角色"]
}"""

                val result = aiService.generatePersona(cfg, userInput, templateExample)
                result.fold(
                    onSuccess = { jsonStr ->
                        val parsed = parsePersonaJson(jsonStr)
                        _generatedPersona.value = parsed
                    },
                    onFailure = {
                        _error.value = it.message ?: "生成失败"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "未知错误"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun parsePersonaJson(jsonStr: String): PromptData? {
        return try {
            val elem = json.parseToJsonElement(jsonStr)
            val inner = json.decodeFromJsonElement<PromptInnerData>(elem)
            PromptData(data = inner)
        } catch (e: Exception) {
            null
        }
    }

    fun clearResult() {
        _generatedPersona.value = null
        _error.value = null
    }
}
