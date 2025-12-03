package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    /**
     * 创建评论
     */
    Comment createComment(Comment comment, Long productId, Long userId);
    
    /**
     * 创建评论（直接传递参数）
     */
    Comment createComment(Long productId, Long userId, String content, Integer rating);
    
    /**
     * 创建讨论评论
     */
    Comment createCommentForDiscussion(Long discussionId, Long userId, String content);
    
    /**
     * 获取讨论评论列表（分页）
     */
    Page<Comment> getCommentsByDiscussionId(Long discussionId, Pageable pageable);

    /**
     * 更新评论
     */
    Comment updateComment(Long id, Comment commentDetails, Long userId);

    /**
     * 删除评论（软删除）
     */
    boolean deleteComment(Long id, Long userId);

    /**
     * 获取评论详情
     */
    Comment getCommentById(Long id);

    /**
     * 获取商品评论列表（分页）
     */
    Page<Comment> getCommentsByProductId(Long productId, Pageable pageable);

    /**
     * 获取用户评论列表（分页）
     */
    Page<Comment> getCommentsByUserId(Long userId, Pageable pageable);

    /**
     * 获取评论的回复列表
     */
    List<Comment> getCommentReplies(Long commentId);

    /**
     * 获取商品的评论数量
     */
    long countCommentsByProductId(Long productId);

    /**
     * 获取商品的平均评分
     */
    Double calculateAverageRating(Long productId);

    /**
     * 获取商品的评分数量
     */
    long countRatingsByProductId(Long productId);

    /**
     * 检查用户是否有权限修改评论
     */
    boolean hasPermissionToModifyComment(Long commentId, Long userId);
}
