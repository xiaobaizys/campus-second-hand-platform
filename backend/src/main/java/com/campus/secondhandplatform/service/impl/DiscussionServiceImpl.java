package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Discussion;
import com.campus.secondhandplatform.entity.DiscussionLike;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.DiscussionRepository;
import com.campus.secondhandplatform.repository.DiscussionLikeRepository;
import com.campus.secondhandplatform.repository.UserRepository;
import com.campus.secondhandplatform.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private DiscussionLikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Discussion> getDiscussions(Pageable pageable) {
        return discussionRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Discussion> getDiscussions(Pageable pageable, String tag, String keyword) {
        // 简化实现，直接返回所有未删除的讨论
        return discussionRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Discussion> getDiscussionsByTag(String tag, Pageable pageable) {
        return discussionRepository.findByTagsContainingAndIsDeletedFalse(tag, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Discussion> searchDiscussions(String keyword, Pageable pageable) {
        return discussionRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Discussion getDiscussionById(Long id) {
        Optional<Discussion> optionalDiscussion = discussionRepository.findById(id);
        if (optionalDiscussion.isPresent()) {
            Discussion discussion = optionalDiscussion.get();
            if (!discussion.getIsDeleted()) {
                incrementViewCount(id);
                return discussion;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public Discussion createDiscussion(Discussion discussion, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            discussion.setUser(user);
            discussion.setLikeCount(0);
            discussion.setCommentCount(0);
            discussion.setViewCount(0);
            discussion.setIsDeleted(false);
            return discussionRepository.save(discussion);
        }

        return null;
    }

    @Override
    @Transactional
    public Discussion updateDiscussion(Long id, Discussion discussion, Long userId) {
        // 检查权限
        if (!hasPermissionToModifyDiscussion(id, userId)) {
            return null;
        }

        // 获取现有讨论
        Optional<Discussion> commentOpt = discussionRepository.findById(id);
        if (commentOpt.isPresent()) {
            Discussion existingDiscussion = commentOpt.get();
            existingDiscussion.setTitle(discussion.getTitle());
            existingDiscussion.setContent(discussion.getContent());
            existingDiscussion.setImages(discussion.getImages());
            existingDiscussion.setTags(discussion.getTags());
            return discussionRepository.save(existingDiscussion);
        }

        return null;
    }

    @Override
    @Transactional
    public void deleteDiscussion(Long id, Long userId) {
        // 检查权限
        if (!hasPermissionToModifyDiscussion(id, userId)) {
            return;
        }

        // 获取现有讨论
        Optional<Discussion> discussionOpt = discussionRepository.findById(id);
        if (discussionOpt.isPresent()) {
            Discussion discussion = discussionOpt.get();
            discussion.setIsDeleted(true);
            discussionRepository.save(discussion);
        }
    }

    @Override
    @Transactional
    public boolean toggleLike(Long discussionId, Long userId) {
        Optional<Discussion> optionalDiscussion = discussionRepository.findById(discussionId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalDiscussion.isPresent() && optionalUser.isPresent()) {
            Discussion discussion = optionalDiscussion.get();
            User user = optionalUser.get();
            DiscussionLike existingLike = likeRepository.findByUser_IdAndDiscussion_Id(userId, discussionId);
            if (existingLike != null) {
                // 取消点赞
                likeRepository.delete(existingLike);
                discussion.setLikeCount(discussion.getLikeCount() - 1);
                discussionRepository.save(discussion);
                return false;
            } else {
                // 点赞
                DiscussionLike like = new DiscussionLike();
                like.setUser(user);
                like.setDiscussion(discussion);
                likeRepository.save(like);
                discussion.setLikeCount(discussion.getLikeCount() + 1);
                discussionRepository.save(discussion);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLiked(Long discussionId, Long userId) {
        return likeRepository.existsByUser_IdAndDiscussion_Id(userId, discussionId);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long discussionId) {
        Optional<Discussion> optionalDiscussion = discussionRepository.findById(discussionId);
        if (optionalDiscussion.isPresent()) {
            Discussion discussion = optionalDiscussion.get();
            discussion.setViewCount(discussion.getViewCount() + 1);
            discussionRepository.save(discussion);
        }
    }

    @Override
    public Page<Discussion> getDiscussionsByUserId(Long userId, Pageable pageable) {
        return discussionRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
    }

    @Override
    public Page<Discussion> getDiscussionsByMostLiked(Pageable pageable) {
        return discussionRepository.findByIsDeletedFalseOrderByLikeCountDesc(pageable);
    }

    @Override
    public Page<Discussion> getDiscussionsByMostCommented(Pageable pageable) {
        return discussionRepository.findByIsDeletedFalseOrderByCommentCountDesc(pageable);
    }

    @Override
    public Page<Discussion> getDiscussionsByMostViewed(Pageable pageable) {
        return discussionRepository.findByIsDeletedFalseOrderByViewCountDesc(pageable);
    }

    @Override
    public List<String> getHotTags() {
        // 这里简化实现，实际应该根据讨论数量统计热门标签
        List<String> tags = new ArrayList<>();
        tags.add("学习交流");
        tags.add("生活分享");
        tags.add("求助问答");
        tags.add("二手交易");
        tags.add("校园活动");
        return tags;
    }

    /**
     * 检查用户是否有权限修改讨论
     */
    private boolean hasPermissionToModifyDiscussion(Long discussionId, Long userId) {
        Optional<Discussion> discussionOpt = discussionRepository.findById(discussionId);
        if (discussionOpt.isPresent()) {
            Discussion discussion = discussionOpt.get();
            // 讨论作者或管理员可以修改
            return discussion.getUser().getId().equals(userId) ||
                    userRepository.findById(userId).map(user -> "ADMIN".equals(user.getRole())).orElse(false);
        }
        return false;
    }

    @Override
    public Discussion saveDiscussion(Discussion discussion) {
        return discussionRepository.save(discussion);
    }
}