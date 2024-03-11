package com.lodny.rwproxy.entity.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String body,
        ProfileResponse author
) { }
