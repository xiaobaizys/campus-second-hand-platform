package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.Comment;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.service.CommentService;
import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("用户未认证");
        }

        String name = authentication.getName();

        // 尝试先将name作为用户ID处理
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            // 如果不是数字，说明是用户名，需要根据用户名查找用户ID
            User user = userService.getUserByUsername(name)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            return user.getId();
        }
    }

    /**
     * 获取商品评论
     */
    @GetMapping("/products/{productId}/comments")
    public ResponseEntity<Map<String, Object>> getCommentsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> comments = commentService.getCommentsByProductId(productId, pageable);

        return buildCommentsResponse(comments);
    }

    /**
     * 获取讨论评论
     */
    @GetMapping("/discussions/{discussionId}/comments")
    public ResponseEntity<Map<String, Object>> getCommentsByDiscussionId(
            @PathVariable Long discussionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> comments = commentService.getCommentsByDiscussionId(discussionId, pageable);

        return buildCommentsResponse(comments);
    }

    /**
     * 构建评论响应
     */
    private ResponseEntity<Map<String, Object>> buildCommentsResponse(Page<Comment> comments) {
        // 手动构建响应，只返回前端需要的基本字段，不包含用户详情，避免LazyInitializationException
        List<Map<String, Object>> commentList = comments.getContent().stream().map(comment -> {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("content", comment.getContent());
            commentMap.put("createdAt", comment.getCreatedAt());
            commentMap.put("updatedAt", comment.getUpdatedAt());

            // 获取真实的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            try {
                userInfo.put("id", comment.getUser().getId());
                userInfo.put("username", comment.getUser().getUsername());
                userInfo.put("avatar", comment.getUser().getAvatarUrl());
            } catch (Exception e) {
                // 如果无法获取用户信息，使用默认值
                userInfo.put("id", 1L);
                userInfo.put("username", "匿名用户");
                userInfo.put("avatar", "");
            }
            commentMap.put("user", userInfo);

            return commentMap;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", commentList);
        response.put("totalElements", comments.getTotalElements());
        response.put("totalPages", comments.getTotalPages());
        response.put("currentPage", comments.getNumber());
        response.put("pageSize", comments.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 添加评论
     */
    @PostMapping("/comments")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody Map<String, Object> commentData) {
        try {
            // 记录请求数据
            System.out.println("收到评论请求: " + commentData);

            Long userId = getCurrentUserId();
            System.out.println("当前用户ID: " + userId);

            // 检查content字段是否存在
            if (!commentData.containsKey("content")) {
                System.out.println("请求缺少content字段");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "评论内容不能为空");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String content = commentData.get("content").toString();
            System.out.println("评论内容: " + content);

            Comment createdComment;
            if (commentData.containsKey("productId")) {
                // 商品评论
                Long productId = Long.parseLong(commentData.get("productId").toString());
                System.out.println("商品ID: " + productId);
                Integer rating = commentData.containsKey("rating")
                        ? Integer.parseInt(commentData.get("rating").toString())
                        : null;
                createdComment = commentService.createComment(productId, userId, content, rating);
            } else if (commentData.containsKey("discussionId")) {
                // 讨论评论
                Long discussionId = Long.parseLong(commentData.get("discussionId").toString());
                System.out.println("讨论ID: " + discussionId);
                createdComment = commentService.createCommentForDiscussion(discussionId, userId, content);
            } else {
                System.out.println("请求缺少productId或discussionId字段");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "缺少必要的商品ID或讨论ID");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 检查评论是否创建成功
            if (createdComment == null) {
                System.out.println("评论创建失败，createdComment为null");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "评论创建失败");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 构建响应，只返回前端需要的基本字段
            Map<String, Object> response = new HashMap<>();
            response.put("id", createdComment.getId());
            response.put("content", createdComment.getContent());
            response.put("createdAt", createdComment.getCreatedAt());

            // 获取当前用户信息
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("avatar", user.getAvatar());
            response.put("user", userInfo);

            System.out.println("评论创建成功: " + createdComment.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("评论创建失败，错误信息: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 更新评论
     */
    @PutMapping("/comments/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment commentDetails) {
        try {
            Long userId = getCurrentUserId();
            Comment updatedComment = commentService.updateComment(id, commentDetails, userId);
            if (updatedComment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean deleted = commentService.deleteComment(id, userId);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "评论删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户评论
     */
    @GetMapping("/user/comments")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Long userId = getCurrentUserId();
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> comments = commentService.getCommentsByUserId(userId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", comments.getContent());
        response.put("totalElements", comments.getTotalElements());
        response.put("totalPages", comments.getTotalPages());
        response.put("currentPage", comments.getNumber());
        response.put("pageSize", comments.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<Comment>> getCommentReplies(@PathVariable Long commentId) {
        List<Comment> replies = commentService.getCommentReplies(commentId);
        return ResponseEntity.ok(replies);
    }

    /**
     * 获取商品的评论统计信息
     */
    @GetMapping("/products/{productId}/comments/stats")
    public ResponseEntity<Map<String, Object>> getCommentStats(@PathVariable Long productId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalComments", commentService.countCommentsByProductId(productId));
        stats.put("averageRating", commentService.calculateAverageRating(productId));
        stats.put("totalRatings", commentService.countRatingsByProductId(productId));
        return ResponseEntity.ok(stats);
    }
}
