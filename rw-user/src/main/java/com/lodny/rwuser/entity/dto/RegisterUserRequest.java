package com.lodny.rwuser.entity.dto;

import org.springframework.util.Assert;

public record RegisterUserRequest(String username, String email, String password) {

    public RegisterUserRequest {
        Assert.hasText(username, "username 은 필수입니다.");
        Assert.hasText(email, "email 은 필수입니다.");
        Assert.hasText(password, "password 은 필수입니다.");
    }
}
