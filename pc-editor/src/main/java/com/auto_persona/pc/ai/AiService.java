package com.auto_persona.pc.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AiService {

    private static OkHttpClient createClient() {
        try {
            TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
            };
            SSLContext sslCtx = SSLContext.getInstance("TLSv1.2");
            sslCtx.init(null, trustAll, new SecureRandom());
            return new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(sslCtx.getSocketFactory(), (X509TrustManager) trustAll[0])
                    .hostnameVerifier((host, session) -> true)
                    .build();
        } catch (Exception e) {
            return new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
    }

    private final OkHttpClient client = createClient();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public String generatePersona(AiConfig config, String userInput) throws IOException {
        String templateExample = """
                {
                  "name": "YUKI",
                  "personality": "温柔内向，逐渐熟络",
                  "scenario": "你是用户的妹妹，与用户同居",
                  "tags": ["女生", "妹妹", "日常", "中文角色"]
                }""";

        String systemPrompt = "你是一个二次元角色人设生成器。根据用户描述，生成一个完整的角色人设JSON。\n\n" +
            "你必须严格按以下JSON格式输出（只输出JSON，不要任何额外文字）：\n" +
            "{\n" +
            "  \"name\": \"角色名（英文大写）\",\n" +
            "  \"description\": \"[姓名:XX][年龄:XX][性别:X][身份:XX][外貌:XX][性格:XX][喜好:XX][称呼:XX][对话:XX]\",\n" +
            "  \"personality\": \"核心性格描述，50字以内\",\n" +
            "  \"scenario\": \"场景设定：你是用户的XX，与用户的关系和互动模式\",\n" +
            "  \"creator_notes\": \"输出格式规则：[每次发言结尾加<好感变化:+X>,X∈[-5,5]][限制:≤100字]严格遵守输出格式：仅输出角色台词和标签\",\n" +
            "  \"first_mes\": \"角色的开场白第一句话（可选，可为空）\",\n" +
            "  \"tags\": [\"标签1\", \"标签2\", \"标签3\"]\n" +
            "}\n\n" +
            "参考模板示例：\n" + templateExample +
            "\n\n用户要求：" + userInput;

        String body = "{\"model\":\"" + config.model + "\",\"messages\":[" +
            "{\"role\":\"system\",\"content\":" + escapeJson(systemPrompt) + "}," +
            "{\"role\":\"user\",\"content\":" + escapeJson(userInput) + "}" +
            "],\"temperature\":0.8,\"max_tokens\":2048}";

        Request request = new Request.Builder()
                .url(config.baseUrl + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + config.apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body, JSON))
                .build();

        try (var response = client.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("API错误 " + response.code() + ": " + respBody);
            }
            return extractJsonFromResponse(respBody);
        }
    }

    private String extractJsonFromResponse(String body) {
        try {
            // Properly parse the API response JSON
            Gson gson = new Gson();
            JsonObject resp = gson.fromJson(body, JsonObject.class);
            JsonArray choices = resp.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) return extractJsonBraces(body);
            JsonObject msg = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            if (msg == null) return extractJsonBraces(body);
            String content = msg.get("content").getAsString();
            if (content == null) return extractJsonBraces(body);
            return extractJsonBraces(content);
        } catch (Exception e) {
            return extractJsonBraces(body);
        }
    }

    private String extractJsonBraces(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        // Try removing markdown code block markers
        String cleaned = text.replace("```json", "").replace("```", "").trim();
        start = cleaned.indexOf('{');
        end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) return cleaned.substring(start, end + 1);
        return text;
    }

    private String escapeJson(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
