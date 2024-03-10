package com.lodny.rwcomment.entity.dto;

import com.lodny.rwcomment.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String body,
        ProfileResponse author
) {
    public static CommentResponse of(final Comment comment, final ProfileResponse author) {
        return new CommentResponse(comment.getId(),
                comment.getCreatedAt(),
                comment.getCreatedAt(),
                comment.getBody(),
                author);
    }
}
