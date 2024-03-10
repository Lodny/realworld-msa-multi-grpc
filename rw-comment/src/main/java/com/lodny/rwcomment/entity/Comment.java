package com.lodny.rwcomment.entity;

import com.lodny.rwcomment.entity.dto.RegisterCommentRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String body;
    private Long articleId;
    private Long authorId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Comment of(final RegisterCommentRequest registerCommentRequest,
                             final Long articleId,
                             final Long authorId) {
        Assert.hasText(registerCommentRequest.body(), "body 는 필수 입니다.");
        Assert.notNull(articleId, "articleId 는 필수입니다.");
        Assert.notNull(authorId, "articleId 는 필수입니다.");

        return Comment.builder()
                .body(registerCommentRequest.body())
                .articleId(articleId)
                .authorId(authorId)
                .build();
    }
}
