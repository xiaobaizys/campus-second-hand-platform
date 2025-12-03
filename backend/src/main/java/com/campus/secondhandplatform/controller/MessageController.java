package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.Message;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.service.MessageService;
import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(originPatterns = { "http://localhost:*" }, maxAge = 3600)
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("用户未认证");
        }

        String name = authentication.getName();

        // 尝试先将name作为用户ID处理
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            // 如果不是数字，说明是用户名，需要根据用户名查找用户ID
            User user = userService.getUserByUsername(name)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + name));
            return user.getId();
        }
    }

    /**
     * 获取当前用户的所有消息
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserMessages() {
        Long userId = getCurrentUserId();
        List<Message> messages = messageService.getUserMessages(userId);
        long unreadCount = messageService.getUnreadMessageCount(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("data", messages);
        response.put("unreadCount", unreadCount);
        response.put("total", messages.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户的未读消息
     */
    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUnreadMessages() {
        Long userId = getCurrentUserId();
        List<Message> unreadMessages = messageService.getUserUnreadMessages(userId);
        long unreadCount = messageService.getUnreadMessageCount(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("data", unreadMessages);
        response.put("unreadCount", unreadCount);
        response.put("total", unreadMessages.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 发送消息
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> messageData) {
        Long senderId = getCurrentUserId();
        Long receiverId = Long.parseLong(messageData.get("receiverId").toString());
        String content = messageData.get("content").toString();

        try {
            Message message = messageService.sendMessage(senderId, receiverId, content);
            Map<String, Object> response = new HashMap<>();
            response.put("data", message);
            response.put("message", "消息发送成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> markMessageAsRead(@PathVariable Long id) {
        boolean success = messageService.markAsRead(id);

        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "消息已标记为已读");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "消息不存在");
            error.put("success", false);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> markAllMessagesAsRead() {
        Long userId = getCurrentUserId();
        messageService.markAllAsRead(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "所有消息已标记为已读");
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        boolean success = messageService.deleteMessage(id);

        if (success) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "消息已删除");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "消息不存在");
            error.put("success", false);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 清空所有消息
     */
    @DeleteMapping("/clear-all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> clearAllMessages() {
        Long userId = getCurrentUserId();
        messageService.clearAllMessages(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "所有消息已清空");
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}