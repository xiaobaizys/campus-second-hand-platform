package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscussionService {

    // 获取讨论列表
    Page<Discussion> getDiscussions(Pageable pageable);

    // 获取讨论列表，支持标签和关键词筛选
    Page<Discussion> getDiscussions(Pageable pageable, String tag, String keyword);

    // 根据标签获取讨论列表
    Page<Discussion> getDiscussionsByTag(String tag, Pageable pageable);

    // 根据关键词搜索讨论
    Page<Discussion> searchDiscussions(String keyword, Pageable pageable);

    // 获取讨论详情
    Discussion getDiscussionById(Long id);

    // 创建讨论
    Discussion createDiscussion(Discussion discussion, Long userId);

    // 更新讨论
    Discussion updateDiscussion(Long id, Discussion discussion, Long userId);

    // 删除讨论（软删除）
    void deleteDiscussion(Long id, Long userId);

    // 点赞/取消点赞讨论
    boolean toggleLike(Long discussionId, Long userId);

    // 获取讨论点赞状态
    boolean isLiked(Long discussionId, Long userId);

    // 增加讨论浏览量
    void incrementViewCount(Long discussionId);

    // 根据用户ID获取讨论列表
    Page<Discussion> getDiscussionsByUserId(Long userId, Pageable pageable);

    // 按点赞数排序获取讨论列表
    Page<Discussion> getDiscussionsByMostLiked(Pageable pageable);

    // 按评论数排序获取讨论列表
    Page<Discussion> getDiscussionsByMostCommented(Pageable pageable);

    // 按浏览量排序获取讨论列表
    Page<Discussion> getDiscussionsByMostViewed(Pageable pageable);

    // 获取热门标签
    List<String> getHotTags();
    
    // 保存讨论
    Discussion saveDiscussion(Discussion discussion);
}
