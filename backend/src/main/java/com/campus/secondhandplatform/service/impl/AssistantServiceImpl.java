package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.service.AssistantService;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = "sk-3466a598aef344938c910b4edcc323bc";
    private static final String BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Override
    public String chat(String message) {
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");

        JSONArray messages = new JSONArray();

        // 系统提示
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是校园二手交易平台的AI助手，你需要回答用户关于校园二手交易平台的相关问题。");
        messages.put(systemMessage);

        // 用户消息
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.put(userMessage);

        requestBody.put("messages", messages);

        // 构建请求
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try {
            // 发送请求
            Response response = client.newCall(request).execute();

            // 解析响应
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);

                if (jsonResponse.has("choices") && jsonResponse.getJSONArray("choices").length() > 0) {
                    JSONObject firstChoice = jsonResponse.getJSONArray("choices").getJSONObject(0);
                    JSONObject assistantMessage = firstChoice.getJSONObject("message");
                    return assistantMessage.getString("content");
                }
            }

            return "抱歉，我暂时无法回答这个问题，请稍后再试。";
        } catch (IOException e) {
            e.printStackTrace();
            return "抱歉，服务器出现问题，请稍后再试。";
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，解析AI回复失败，请稍后再试。";
        }
    }
}
