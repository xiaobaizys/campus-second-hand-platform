package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.ChatMessage;
import java.util.List;

public interface AssistantService {
    /**
     * AI聊天接口
     * @param sessionId 会话ID
     * @param message 用户提问消息
     * @return AI回复内容
     */
    String chat(String sessionId, String message);
    
    /**
     * 获取聊天记录
     * @param sessionId 会话ID
     * @return 聊天记录列表
     */
    List<ChatMessage> getChatHistory(String sessionId);
    
    /**
     * 清空聊天记录
     * @param sessionId 会话ID
     */
    void clearChatHistory(String sessionId);
    
    /**
     * 保存聊天消息
     * @param chatMessage 聊天消息
     */
    void saveChatMessage(ChatMessage chatMessage);
}
