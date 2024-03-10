package com.lodny.rwarticle.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lodny.rwarticle.entity.Article;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleResponseRecord(
        @JsonIgnore Long id,
        String slug,
        String title,
        String description,
        String body,
        Set<String> tagList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean favorited,
        Long favoritesCount,
        ProfileResponse author
) {
    public static ArticleResponseRecord of(final Article article,
                                     final ProfileResponse author,
                                     final Boolean favorited,
                                     final Long favoritesCount) {
        // todo : BeanUtils.copyProperites
        return new ArticleResponseRecord(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getBody(),
//                article.getTagList(),
                null,
                article.getCreatedAt(),
                article.getUpdatedAt(),
                favorited,
                favoritesCount,
                author);
    }
}
