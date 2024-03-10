package com.lodny.rwarticle.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lodny.rwarticle.entity.Article;
import com.lodny.rwcommon.util.MapToDto.MapToDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponse {
    @JsonIgnore
    private Long id;

    private String slug;
    private String title;
    private String description;
    private String body;
    private Set<String> tagList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean favorited;
    private Long favoritesCount;

    private ProfileResponse author;

    public static ArticleResponse of(final Article article,
                                     final Set<String> tagList,
                                     final ProfileResponse author,
                                     final Long[] favoriteInfo) {
        return ArticleResponse.builder()
                .id(article.getId())
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .tagList(tagList)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .favoritesCount(favoriteInfo[0])
                .favorited(favoriteInfo[1] == 1L)
                .author(author)
                .build();
    }
}
