package com.lodny.rwproxy.entity.dto;

public record ProfileResponse(
    String username,
    String bio,
    String image,
    Boolean following) {
}
