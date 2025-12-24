package com.campus.secondhandplatform.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 角色：system（系统）、user（用户）、assistant（助手）
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息时间戳
     */
    private Date timestamp;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 默认构造函数
     */
    public ChatMessage() {
        this.timestamp = new Date();
    }
    
    /**
     * 构造函数
     * @param role 角色
     * @param content 内容
     */
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = new Date();
    }
    
    /**
     * 构造函数
     * @param role 角色
     * @param content 内容
     * @param sessionId 会话ID
     */
    public ChatMessage(String role, String content, String sessionId) {
        this.role = role;
        this.content = content;
        this.sessionId = sessionId;
        this.timestamp = new Date();
    }
}