package com.lodny.rwuser.entity.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public record LoginRequest(String email, String password) {
    public LoginRequest {
        Assert.hasText(email, "email 은 필수입니다.");
        Assert.hasText(password, "password 은 필수입니다.");
    }
}
