package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Comment;
import com.campus.secondhandplatform.entity.Discussion;
import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.CommentRepository;
import com.campus.secondhandplatform.service.CommentService;
import com.campus.secondhandplatform.service.DiscussionService;
import com.campus.secondhandplatform.service.ProductService;
import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProductService productService;
    private final UserService userService;
    private final DiscussionService discussionService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
            ProductService productService,
            UserService userService,
            DiscussionService discussionService) {
        this.commentRepository = commentRepository;
        this.productService = productService;
        this.userService = userService;
        this.discussionService = discussionService;
    }

    @Override
    public Comment createComment(Comment comment, Long productId, Long userId) {
        // 获取商品和用户
        Optional<Product> productOpt = productService.getProductById(productId);
        Optional<User> userOpt = userService.getUserById(userId);

        if (productOpt.isPresent() && userOpt.isPresent()) {
            comment.setProduct(productOpt.get());
            comment.setUser(userOpt.get());
            comment.setIsDeleted(false);
            return commentRepository.save(comment);
        }

        return null;
    }

    @Override
    public Comment createComment(Long productId, Long userId, String content, Integer rating) {
        // 获取商品和用户
        Optional<Product> productOpt = productService.getProductById(productId);
        Optional<User> userOpt = userService.getUserById(userId);

        if (productOpt.isPresent() && userOpt.isPresent()) {
            Comment comment = new Comment();
            comment.setProduct(productOpt.get());
            comment.setUser(userOpt.get());
            comment.setContent(content);
            comment.setRating(rating);
            comment.setIsDeleted(false);
            return commentRepository.save(comment);
        }

        return null;
    }

    @Override
    public Comment createCommentForDiscussion(Long discussionId, Long userId, String content) {
        // 获取讨论和用户
        Optional<Discussion> discussionOpt = Optional.ofNullable(discussionService.getDiscussionById(discussionId));
        Optional<User> userOpt = userService.getUserById(userId);

        if (discussionOpt.isPresent() && userOpt.isPresent()) {
            Discussion discussion = discussionOpt.get();
            User user = userOpt.get();
            
            // 创建评论
            Comment comment = new Comment();
            comment.setDiscussion(discussion);
            comment.setUser(user);
            comment.setContent(content);
            comment.setIsDeleted(false);
            Comment savedComment = commentRepository.save(comment);
            
            // 更新讨论的评论计数
            discussion.setCommentCount(discussion.getCommentCount() + 1);
            discussionService.saveDiscussion(discussion);
            
            return savedComment;
        }

        return null;
    }

    @Override
    public Page<Comment> getCommentsByDiscussionId(Long discussionId, Pageable pageable) {
        // 使用fetch join加载用户信息，避免LazyInitializationException
        return commentRepository.findByDiscussionIdAndIsDeletedFalse(discussionId, pageable);
    }

    @Override
    public Comment updateComment(Long id, Comment commentDetails, Long userId) {
        // 检查权限
        if (!hasPermissionToModifyComment(id, userId)) {
            return null;
        }

        // 获取现有评论
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setContent(commentDetails.getContent());
            comment.setRating(commentDetails.getRating());
            return commentRepository.save(comment);
        }

        return null;
    }

    @Override
    public boolean deleteComment(Long id, Long userId) {
        // 检查权限
        if (!hasPermissionToModifyComment(id, userId)) {
            return false;
        }

        // 获取现有评论
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setIsDeleted(true);
            commentRepository.save(comment);
            return true;
        }

        return false;
    }

    @Override
    public Comment getCommentById(Long id) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        return commentOpt.filter(comment -> !comment.getIsDeleted()).orElse(null);
    }

    @Override
    public Page<Comment> getCommentsByProductId(Long productId, Pageable pageable) {
        // 使用fetch join加载用户信息，避免LazyInitializationException
        return commentRepository.findByProductIdAndIsDeletedFalse(productId, pageable);
    }

    @Override
    public Page<Comment> getCommentsByUserId(Long userId, Pageable pageable) {
        return commentRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
    }

    @Override
    public List<Comment> getCommentReplies(Long commentId) {
        return commentRepository.findByParentIdAndIsDeletedFalse(commentId);
    }

    @Override
    public long countCommentsByProductId(Long productId) {
        return commentRepository.countByProductIdAndIsDeletedFalse(productId);
    }

    @Override
    public Double calculateAverageRating(Long productId) {
        return commentRepository.calculateAverageRating(productId);
    }

    @Override
    public long countRatingsByProductId(Long productId) {
        return commentRepository.countRatings(productId);
    }

    @Override
    public boolean hasPermissionToModifyComment(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        return commentOpt.filter(comment -> comment.getUser().getId().equals(userId)).isPresent();
    }
}
