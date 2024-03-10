package com.lodny.rwcomment.entity.dto;

public record ProfileResponse(
    String username,
    String bio,
    String image,
    Boolean following) {
}
