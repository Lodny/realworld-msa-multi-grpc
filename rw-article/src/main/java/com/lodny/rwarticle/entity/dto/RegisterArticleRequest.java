package com.lodny.rwarticle.entity.dto;

import org.springframework.util.Assert;

import java.util.Set;

public record RegisterArticleRequest(String title, String description, String body, Set<String> tagList) {

    public RegisterArticleRequest {
        Assert.hasText(title, "title 은 필수입니다.");
        Assert.hasText(description, "description 은 필수입니다.");
        Assert.hasText(body, "body 는 필수입니다.");
    }
}
