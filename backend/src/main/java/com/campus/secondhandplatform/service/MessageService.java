package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Message;

import java.util.List;

public interface MessageService {

    /**
     * 获取用户收到的所有消息
     * 
     * @param userId 用户ID
     * @return 消息列表
     */
    List<Message> getUserMessages(Long userId);

    /**
     * 获取用户收到的未读消息
     * 
     * @param userId 用户ID
     * @return 未读消息列表
     */
    List<Message> getUserUnreadMessages(Long userId);

    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    long getUnreadMessageCount(Long userId);

    /**
     * 发送消息
     * 
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param content    消息内容
     * @return 发送的消息
     */
    Message sendMessage(Long senderId, Long receiverId, String content);

    /**
     * 标记消息为已读
     * 
     * @param messageId 消息ID
     * @return 是否已读
     */
    boolean markAsRead(Long messageId);

    /**
     * 标记所有消息为已读
     * 
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 删除消息
     * 
     * @param messageId 消息ID
     * @return 是否删除成功
     */
    boolean deleteMessage(Long messageId);

    /**
     * 清空用户所有消息
     * 
     * @param userId 用户ID
     */
    void clearAllMessages(Long userId);
}