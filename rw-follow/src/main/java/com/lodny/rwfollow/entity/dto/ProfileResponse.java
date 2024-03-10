package com.lodny.rwfollow.entity.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
public class ProfileResponse {
    private String username;
    private String bio;
    private String image;

    @Setter
    private Boolean following;
}
