package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
     * @param requestBody 包含用户消息的请求体
     * @return AI回复
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> requestBody) {
        String message = requestBody.get("message");
        
        // 调用AI服务获取回复
        String response = assistantService.chat(message);
        
        // 构建响应
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("content", response);
        
        return ResponseEntity.ok(responseBody);
    }
}
