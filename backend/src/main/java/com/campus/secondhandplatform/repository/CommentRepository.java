package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 按商品ID查询评论，排除已删除的评论
    Page<Comment> findByProductIdAndIsDeletedFalse(Long productId, Pageable pageable);

    // 按商品ID查询所有评论，排除已删除的评论
    List<Comment> findByProductIdAndIsDeletedFalse(Long productId);

    // 按讨论ID查询评论，排除已删除的评论
    Page<Comment> findByDiscussionIdAndIsDeletedFalse(Long discussionId, Pageable pageable);

    // 按讨论ID查询所有评论，排除已删除的评论
    List<Comment> findByDiscussionIdAndIsDeletedFalse(Long discussionId);

    // 按用户ID查询评论，排除已删除的评论
    Page<Comment> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    // 按父评论ID查询回复，排除已删除的评论
    List<Comment> findByParentIdAndIsDeletedFalse(Long parentId);

    // 统计商品的评论数量
    long countByProductIdAndIsDeletedFalse(Long productId);

    // 统计商品的平均评分
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.product.id = :productId AND c.isDeleted = false")
    Double calculateAverageRating(@Param("productId") Long productId);

    // 统计商品的评分数量
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.product.id = :productId AND c.rating IS NOT NULL AND c.isDeleted = false")
    long countRatings(@Param("productId") Long productId);
}
