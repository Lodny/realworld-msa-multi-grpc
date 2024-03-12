package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.profile.GrpcProfileByUsernameRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwcommon.grpc.rwuser.GrpcLoginRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcLoginResponse;
import com.lodny.rwcommon.grpc.rwuser.RwUserGrpc;
import com.lodny.rwcommon.util.JwtUtil;
import com.lodny.rwproxy.entity.dto.LoginRequest;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import com.lodny.rwproxy.entity.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGrpcClient {

    private final JwtUtil jwtUtil;

    @GrpcClient("user-grpc")
    private RwUserGrpc.RwUserBlockingStub userStub;

    public UserResponse login(final LoginRequest loginRequest) {
        GrpcLoginResponse response = userStub.login(GrpcLoginRequest.newBuilder()
                .setEmail(loginRequest.email())
                .setPassword(loginRequest.password())
                .build());
        log.info("login() : response={}", response);

//        String token = jwtUtil.createToken(response.getEmail(), response.getId());
        String token = jwtUtil.createToken(response.getEmail(), -1L);
        log.info("login() : token={}", token);

        return new UserResponse(
                response.getEmail(),
                token,
                response.getUsername(),
                response.getBio(),
                response.getImage());
    }
}
