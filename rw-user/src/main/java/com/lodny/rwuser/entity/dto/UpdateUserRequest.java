package com.lodny.rwuser.entity.dto;

public record UpdateUserRequest(String username, String email, String password, String image, String bio) { }
