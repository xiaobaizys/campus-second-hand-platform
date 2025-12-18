package com.campus.secondhandplatform.service;

public interface AssistantService {
    /**
     * AI聊天接口
     * @param message 用户提问消息
     * @return AI回复内容
     */
    String chat(String message);
}
