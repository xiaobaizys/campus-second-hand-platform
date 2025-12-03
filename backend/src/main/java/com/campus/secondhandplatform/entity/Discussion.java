package com.campus.secondhandplatform.entity;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "discussions")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // 临时字段，用于前端展示用户信息
    @Transient
    private Map<String, Object> userInfo;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ElementCollection
    @CollectionTable(name = "discussion_images", joinColumns = @JoinColumn(name = "discussion_id"))
    @Column(name = "image_url")
    private List<String> images;

    @ElementCollection
    @CollectionTable(name = "discussion_tags", joinColumns = @JoinColumn(name = "discussion_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "like_count", columnDefinition = "int default 0")
    private Integer likeCount = 0;

    @Column(name = "comment_count", columnDefinition = "int default 0")
    private Integer commentCount = 0;

    @Column(name = "view_count", columnDefinition = "int default 0")
    private Integer viewCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    // 临时字段，用于前端展示，不存储到数据库
    @Transient
    private Boolean liked = false;

    // 临时字段，用于前端展示，不存储到数据库
    @Transient
    private List<Comment> comments;
}
