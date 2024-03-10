package com.lodny.rwfavorite.entity.dto;

public record ProfileResponse(
    String username,
    String bio,
    String image,
    Boolean following) {
}
