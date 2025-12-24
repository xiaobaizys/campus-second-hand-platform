package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.ChatMessage;
import com.campus.secondhandplatform.service.AssistantService;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = "sk-3466a598aef344938c910b4edcc323bc";
    private static final String BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String CHAT_HISTORY_KEY_PREFIX = "chat:history:";
    private static final long CHAT_HISTORY_EXPIRE_TIME = 24 * 60 * 60; // 24小时过期

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String chat(String sessionId, String message) {
        System.out.println("=== 开始处理AI聊天请求 ===");
        System.out.println("会话ID: " + sessionId);
        System.out.println("用户消息: " + message);
        
        // 获取历史聊天记录
        String key = CHAT_HISTORY_KEY_PREFIX + sessionId;
        List<ChatMessage> chatHistory = getChatHistory(sessionId);
        System.out.println("当前历史记录数量: " + chatHistory.size());
        
        // 添加用户消息到历史记录
        ChatMessage userMessage = new ChatMessage("user", message, sessionId);
        chatHistory.add(userMessage);
        
        // 直接保存完整聊天记录，避免多次Redis操作
        redisTemplate.opsForValue().set(key, chatHistory, CHAT_HISTORY_EXPIRE_TIME, TimeUnit.SECONDS);
        System.out.println("用户消息已保存到Redis");
        
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");

        JSONArray messages = new JSONArray();

        // 系统提示
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是校园二手交易平台的AI助手，你需要回答用户关于校园二手交易平台的相关问题。");
        messages.put(systemMessage);

        // 添加历史聊天记录到请求，只保留最近10轮对话，避免请求过长
        int startIndex = Math.max(0, chatHistory.size() - 20); // 保留最近10轮对话（20条消息）
        System.out.println("发送给AI的历史记录起始索引: " + startIndex);
        for (int i = startIndex; i < chatHistory.size(); i++) {
            ChatMessage chatMsg = chatHistory.get(i);
            JSONObject msg = new JSONObject();
            msg.put("role", chatMsg.getRole());
            msg.put("content", chatMsg.getContent());
            messages.put(msg);
        }

        requestBody.put("messages", messages);
        System.out.println("请求体: " + requestBody.toString());

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
            System.out.println("发送请求到AI服务...");
            Response response = client.newCall(request).execute();
            System.out.println("AI服务响应状态: " + response.code());

            // 解析响应
            if (response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("AI服务响应内容: " + responseBody);
                
                if (response.isSuccessful()) {
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.has("choices") && jsonResponse.getJSONArray("choices").length() > 0) {
                        JSONObject firstChoice = jsonResponse.getJSONArray("choices").getJSONObject(0);
                        JSONObject assistantMessageJson = firstChoice.getJSONObject("message");
                        String assistantResponse = assistantMessageJson.getString("content");
                        
                        System.out.println("AI回复: " + assistantResponse);
                        
                        // 添加AI回复到历史记录
                        ChatMessage assistantMessage = new ChatMessage("assistant", assistantResponse, sessionId);
                        chatHistory.add(assistantMessage);
                        
                        // 直接保存完整聊天记录
                        redisTemplate.opsForValue().set(key, chatHistory, CHAT_HISTORY_EXPIRE_TIME, TimeUnit.SECONDS);
                        System.out.println("AI回复已保存到Redis");
                        
                        return assistantResponse;
                    }
                }
            }

            return "抱歉，我暂时无法回答这个问题，请稍后再试。";
        } catch (IOException e) {
            System.out.println("IO异常: " + e.getMessage());
            e.printStackTrace();
            return "抱歉，服务器出现问题，请稍后再试。";
        } catch (Exception e) {
            System.out.println("异常: " + e.getMessage());
            e.printStackTrace();
            return "抱歉，解析AI回复失败，请稍后再试。";
        }
    }

    @Override
    public List<ChatMessage> getChatHistory(String sessionId) {
        String key = CHAT_HISTORY_KEY_PREFIX + sessionId;
        try {
            // 安全获取聊天记录，避免类型转换异常
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && value instanceof List<?>) {
                // 确保列表中的元素都是ChatMessage类型
                List<?> rawList = (List<?>) value;
                List<ChatMessage> chatHistory = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof ChatMessage) {
                        chatHistory.add((ChatMessage) item);
                    }
                }
                System.out.println("成功获取历史记录，数量: " + chatHistory.size());
                return chatHistory;
            }
        } catch (Exception e) {
            System.out.println("获取聊天记录异常: " + e.getMessage());
            e.printStackTrace();
        }
        // 如果获取失败或类型转换失败，返回空列表
        return new ArrayList<>();
    }

    @Override
    public void clearChatHistory(String sessionId) {
        String key = CHAT_HISTORY_KEY_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    @Override
    public void saveChatMessage(ChatMessage chatMessage) {
        // 这个方法现在被chat方法中的直接保存逻辑替代，保留是为了兼容接口
        String sessionId = chatMessage.getSessionId();
        String key = CHAT_HISTORY_KEY_PREFIX + sessionId;
        
        List<ChatMessage> chatHistory = getChatHistory(sessionId);
        
        // 检查是否已经存在相同的消息，避免重复添加
        boolean exists = chatHistory.stream().anyMatch(msg -> 
            msg.getTimestamp().equals(chatMessage.getTimestamp()) && 
            msg.getRole().equals(chatMessage.getRole()) && 
            msg.getContent().equals(chatMessage.getContent())
        );
        
        if (!exists) {
            chatHistory.add(chatMessage);
            redisTemplate.opsForValue().set(key, chatHistory, CHAT_HISTORY_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }
}
