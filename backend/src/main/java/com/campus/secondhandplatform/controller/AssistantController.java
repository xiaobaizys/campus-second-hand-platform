package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.ChatMessage;
import com.campus.secondhandplatform.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(originPatterns = { "http://localhost:*" })
public class AssistantController {

    private final AssistantService assistantService;

    @Autowired
    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    /**
     * AI聊天接口
     * @param requestBody 包含用户消息和会话ID的请求体
     * @return AI回复和完整聊天记录
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> requestBody) {
        // 获取会话ID，如果没有则生成一个新的
        String sessionId = requestBody.getOrDefault("sessionId", UUID.randomUUID().toString());
        String message = requestBody.get("message");
        
        // 调用AI服务获取回复
        String response = assistantService.chat(sessionId, message);
        
        // 获取完整聊天记录
        List<ChatMessage> chatHistory = assistantService.getChatHistory(sessionId);
        
        // 构建响应
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("sessionId", sessionId);
        responseBody.put("content", response);
        responseBody.put("chatHistory", chatHistory);
        
        return ResponseEntity.ok(responseBody);
    }
    
    /**
     * 获取聊天记录
     * @param sessionId 会话ID
     * @return 聊天记录
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(@RequestParam String sessionId) {
        List<ChatMessage> chatHistory = assistantService.getChatHistory(sessionId);
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("sessionId", sessionId);
        responseBody.put("chatHistory", chatHistory);
        
        return ResponseEntity.ok(responseBody);
    }
    
    /**
     * 清空聊天记录
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, String>> clearChatHistory(@RequestParam String sessionId) {
        assistantService.clearChatHistory(sessionId);
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "聊天记录已清空");
        
        return ResponseEntity.ok(responseBody);
    }
}
