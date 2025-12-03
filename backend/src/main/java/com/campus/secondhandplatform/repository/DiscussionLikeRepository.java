package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.DiscussionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionLikeRepository extends JpaRepository<DiscussionLike, Long> {

    // 根据用户ID和讨论ID查找点赞记录
    DiscussionLike findByUser_IdAndDiscussion_Id(Long userId, Long discussionId);

    // 删除点赞记录
    void deleteByUser_IdAndDiscussion_Id(Long userId, Long discussionId);

    // 统计讨论点赞数
    long countByDiscussion_Id(Long discussionId);

    // 检查用户是否已点赞
    boolean existsByUser_IdAndDiscussion_Id(Long userId, Long discussionId);
}
