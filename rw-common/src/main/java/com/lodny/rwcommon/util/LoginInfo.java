package com.lodny.rwcommon.util;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginInfo {
    private final String email;
    private final long userId;
    private final String token;

    @Setter
    private Object userResponse;

    public LoginInfo(final String email, final long userId, final String token) {
       this.email = email;
       this.userId = userId;
       this.token = token;
    }
}
