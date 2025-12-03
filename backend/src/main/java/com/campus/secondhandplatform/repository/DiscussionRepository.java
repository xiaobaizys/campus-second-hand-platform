package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    // 获取讨论列表，排除已删除的讨论
    Page<Discussion> findByIsDeletedFalse(Pageable pageable);

    // 根据标签获取讨论列表
    @Query("SELECT d FROM Discussion d WHERE d.isDeleted = false AND :tag MEMBER OF d.tags")
    Page<Discussion> findByTagsContainingAndIsDeletedFalse(@Param("tag") String tag, Pageable pageable);

    // 根据关键词搜索讨论
    @Query("SELECT d FROM Discussion d WHERE d.isDeleted = false AND (:keyword IS NULL OR d.title LIKE CONCAT('%', :keyword, '%') OR d.content LIKE CONCAT('%', :keyword, '%'))")
    Page<Discussion> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 根据用户ID获取讨论列表
    Page<Discussion> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    // 按点赞数排序
    Page<Discussion> findByIsDeletedFalseOrderByLikeCountDesc(Pageable pageable);

    // 按评论数排序
    Page<Discussion> findByIsDeletedFalseOrderByCommentCountDesc(Pageable pageable);

    // 按浏览量排序
    Page<Discussion> findByIsDeletedFalseOrderByViewCountDesc(Pageable pageable);
}
