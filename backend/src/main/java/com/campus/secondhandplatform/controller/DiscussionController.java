package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.Discussion;
import java.util.ArrayList;
import com.campus.secondhandplatform.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionController.class);

    @Autowired
    private DiscussionService discussionService;

    // 获取讨论列表
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        logger.info("获取讨论列表请求参数：page={}, size={}, sortBy={}, sortDir={}, tag={}, keyword={}",
                page, size, sortBy, sortDir, tag, keyword);
        try {
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            logger.info("调用discussionService.getDiscussions方法");
            Page<Discussion> discussions = discussionService.getDiscussions(pageable, tag, keyword);
            logger.info("获取到讨论列表，共{}条记录", discussions.getTotalElements());

            // 手动构建响应，避免直接返回Discussion实体导致的序列化问题
            List<Map<String, Object>> discussionList = discussions.getContent().stream().map(discussion -> {
                Map<String, Object> discussionMap = new HashMap<>();
                discussionMap.put("id", discussion.getId());
                discussionMap.put("title", discussion.getTitle());
                discussionMap.put("content", discussion.getContent());
                discussionMap.put("tags", discussion.getTags());
                discussionMap.put("images", discussion.getImages());
                discussionMap.put("likeCount", discussion.getLikeCount());
                discussionMap.put("commentCount", discussion.getCommentCount());
                discussionMap.put("viewCount", discussion.getViewCount());
                discussionMap.put("createdAt", discussion.getCreatedAt());
                discussionMap.put("liked", discussion.getLiked());

                // 简化用户信息
                Map<String, Object> userInfo = new HashMap<>();
                try {
                    // 尝试访问用户信息，如果抛出异常则使用默认值
                    userInfo.put("id", discussion.getUser().getId());
                    userInfo.put("username", discussion.getUser().getUsername());
                    userInfo.put("avatar", discussion.getUser().getAvatarUrl());
                    // 添加校园认证字段，默认false
                    userInfo.put("isCampusVerified", false);
                } catch (Exception e) {
                    // 如果无法获取用户信息，使用默认值
                    logger.error("获取用户信息失败：", e);
                    userInfo.put("id", 1L);
                    userInfo.put("username", "匿名用户");
                    userInfo.put("avatar", "");
                    userInfo.put("isCampusVerified", false);
                }
                discussionMap.put("user", userInfo);

                return discussionMap;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", discussionList);
            response.put("totalElements", discussions.getTotalElements());
            response.put("totalPages", discussions.getTotalPages());
            response.put("currentPage", discussions.getNumber());
            response.put("pageSize", discussions.getSize());

            logger.info("构建响应成功，返回{}条讨论记录", discussionList.size());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("获取讨论列表失败：", e);
            // 返回空列表，避免前端报错
            Map<String, Object> response = new HashMap<>();
            response.put("content", new ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", 0);
            response.put("pageSize", size);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // 测试端点
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("测试端点被调用");
        return new ResponseEntity<>("测试成功", HttpStatus.OK);
    }

    // 获取讨论详情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDiscussionById(@PathVariable Long id, Principal principal) {
        logger.info("获取讨论详情请求，id={}", id);
        try {
            Discussion discussion = discussionService.getDiscussionById(id);
            if (discussion != null) {
                // 如果用户已登录，获取点赞状态
                if (principal != null) {
                    Long userId = Long.parseLong(principal.getName());
                    discussion.setLiked(discussionService.isLiked(id, userId));
                }

                // 手动构建响应，避免直接返回Discussion实体导致的序列化问题
                Map<String, Object> discussionMap = new HashMap<>();
                discussionMap.put("id", discussion.getId());
                discussionMap.put("title", discussion.getTitle());
                discussionMap.put("content", discussion.getContent());
                discussionMap.put("tags", discussion.getTags());
                discussionMap.put("images", discussion.getImages());
                discussionMap.put("likeCount", discussion.getLikeCount());
                discussionMap.put("commentCount", discussion.getCommentCount());
                discussionMap.put("viewCount", discussion.getViewCount());
                discussionMap.put("createdAt", discussion.getCreatedAt());
                discussionMap.put("liked", discussion.getLiked());
                // 添加前端期望的字段，使用默认值
                discussionMap.put("campus", "");
                discussionMap.put("type", "");
                discussionMap.put("videos", new ArrayList<>());
                discussionMap.put("favoriteCount", 0);
                discussionMap.put("favorited", false);

                // 简化用户信息
                Map<String, Object> userInfo = new HashMap<>();
                try {
                    // 尝试访问用户信息，如果抛出异常则使用默认值
                    userInfo.put("id", discussion.getUser().getId());
                    userInfo.put("username", discussion.getUser().getUsername());
                    userInfo.put("avatar", discussion.getUser().getAvatarUrl());
                    // 添加校园认证字段，默认false
                    userInfo.put("isCampusVerified", false);
                } catch (Exception e) {
                    // 如果无法获取用户信息，使用默认值
                    logger.error("获取用户信息失败：", e);
                    userInfo.put("id", 1L);
                    userInfo.put("username", "匿名用户");
                    userInfo.put("avatar", "");
                    userInfo.put("isCampusVerified", false);
                }
                discussionMap.put("user", userInfo);

                logger.info("获取讨论详情成功，id={}", id);
                return new ResponseEntity<>(discussionMap, HttpStatus.OK);
            } else {
                logger.info("讨论不存在，id={}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("获取讨论详情失败，id={}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 创建讨论
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Discussion> createDiscussion(@RequestBody Discussion discussion, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Discussion createdDiscussion = discussionService.createDiscussion(discussion, userId);
        if (createdDiscussion != null) {
            return new ResponseEntity<>(createdDiscussion, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 更新讨论
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Discussion> updateDiscussion(@PathVariable Long id, @RequestBody Discussion discussion,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Discussion updatedDiscussion = discussionService.updateDiscussion(id, discussion, userId);
        if (updatedDiscussion != null) {
            return new ResponseEntity<>(updatedDiscussion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 删除讨论
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDiscussion(@PathVariable Long id, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        discussionService.deleteDiscussion(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 点赞/取消点赞讨论
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long id, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        boolean isLiked = discussionService.toggleLike(id, userId);
        return new ResponseEntity<>(isLiked, HttpStatus.OK);
    }

    // 检查点赞状态
    @GetMapping("/{id}/like-status")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Boolean> checkLikeStatus(@PathVariable Long id, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        boolean isLiked = discussionService.isLiked(id, userId);
        return new ResponseEntity<>(isLiked, HttpStatus.OK);
    }

    // 获取热门标签
    @GetMapping("/hot-tags")
    public ResponseEntity<List<String>> getHotTags() {
        List<String> hotTags = discussionService.getHotTags();
        return new ResponseEntity<>(hotTags, HttpStatus.OK);
    }

    // 按点赞数排序获取讨论列表
    @GetMapping("/most-liked")
    public ResponseEntity<Page<Discussion>> getMostLikedDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Discussion> discussions = discussionService.getDiscussionsByMostLiked(pageable);
        return new ResponseEntity<>(discussions, HttpStatus.OK);
    }

    // 按评论数排序获取讨论列表
    @GetMapping("/most-commented")
    public ResponseEntity<Page<Discussion>> getMostCommentedDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Discussion> discussions = discussionService.getDiscussionsByMostCommented(pageable);
        return new ResponseEntity<>(discussions, HttpStatus.OK);
    }

    // 按浏览量排序获取讨论列表
    @GetMapping("/most-viewed")
    public ResponseEntity<Page<Discussion>> getMostViewedDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Discussion> discussions = discussionService.getDiscussionsByMostViewed(pageable);
        return new ResponseEntity<>(discussions, HttpStatus.OK);
    }

    // 获取当前用户的讨论列表
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<Discussion>> getMyDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Discussion> discussions = discussionService.getDiscussionsByUserId(userId, pageable);
        return new ResponseEntity<>(discussions, HttpStatus.OK);
    }
}
