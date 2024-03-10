package com.lodny.rwarticle.entity;

import com.lodny.rwarticle.entity.dto.RegisterArticleRequest;
import com.lodny.rwcommon.util.SlugUtil;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@ToString
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String slug;

    private String title;
    private String description;
    private String body;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "article_tag")
//    @Column(name = "tag")
//    private Set<String> tagList;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Long authorId;

    public static Article of(final RegisterArticleRequest request, final Long authorId) {
        return Article.builder()
                .slug(SlugUtil.createSlug(request.title()))
                .title(request.title())
                .description(request.description())
                .body(request.body())
//                .tagList(request.tagList())
                .authorId(authorId)
                .build();
    }
}
