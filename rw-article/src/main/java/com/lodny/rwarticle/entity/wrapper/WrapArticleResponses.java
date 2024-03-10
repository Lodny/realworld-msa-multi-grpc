package com.lodny.rwarticle.entity.wrapper;

import com.lodny.rwarticle.entity.dto.ArticleResponse;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class WrapArticleResponses {
    private List<ArticleResponse> articles;
    private int articlesCount;

    //    private Boolean empty;
    private Boolean first;
    private Boolean last;
    private int number;
    //    private int numberOfElements;
    private Long totalElements;
    private int totalPages;

    public WrapArticleResponses(final Page<ArticleResponse> pageArticles) {
        this.articles = pageArticles.getContent();
        this.articlesCount = this.articles.size();

        this.first = pageArticles.isFirst();
        this.last = pageArticles.isLast();
        this.number = pageArticles.getNumber();
        this.totalElements = pageArticles.getTotalElements();
        this.totalPages = pageArticles.getTotalPages();
    }
}
