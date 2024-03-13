package com.lodny.rwproxy.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lodny.rwcommon.grpc.rwuser.GrpcLoginResponse;
import com.lodny.rwcommon.util.ImageUtil;
import lombok.Builder;

@Builder
public record UserResponse(
    String email,
    String token,
    String username,
    String bio,
    String image
    ) {

    public UserResponse {
        image = ImageUtil.nullToDefaultImage(image);
    }

    public static UserResponse of(final GrpcLoginResponse grpcUser, final String token) {
        return new UserResponse(
                grpcUser.getEmail(),
                token,
                grpcUser.getUsername(),
                grpcUser.getBio(),
                grpcUser.getImage());
    }
}
