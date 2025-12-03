package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Message;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.MessageRepository;
import com.campus.secondhandplatform.repository.UserRepository;
import com.campus.secondhandplatform.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getUserMessages(Long userId) {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getUserUnreadMessages(Long userId) {
        return messageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        // Get sender and receiver
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("发送者不存在: " + senderId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("接收者不存在: " + receiverId));

        // Create and save message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setIsRead(false);

        return messageRepository.save(message);
    }

    @Override
    public boolean markAsRead(Long messageId) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    message.setIsRead(true);
                    messageRepository.save(message);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unreadMessages.forEach(message -> message.setIsRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    public boolean deleteMessage(Long messageId) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    messageRepository.delete(message);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void clearAllMessages(Long userId) {
        List<Message> messages = messageRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        messageRepository.deleteAll(messages);
    }
}