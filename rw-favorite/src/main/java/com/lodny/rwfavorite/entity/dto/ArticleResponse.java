package com.lodny.rwfavorite.entity.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleResponse(
    String slug,
    String title,
    String description,
    String body,
    Set<String> tagList,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean favorited,
    Long favoritesCount,
    ProfileResponse author) {
}
