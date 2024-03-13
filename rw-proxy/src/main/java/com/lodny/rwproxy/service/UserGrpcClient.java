package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.rwuser.GrpcLoginRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcLoginResponse;
import com.lodny.rwcommon.grpc.rwuser.GrpcRegisterUserRequest;
import com.lodny.rwcommon.grpc.rwuser.RwUserGrpc;
import com.lodny.rwcommon.util.JwtUtil;
import com.lodny.rwproxy.entity.dto.LoginRequest;
import com.lodny.rwproxy.entity.dto.RegisterUserRequest;
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
        GrpcLoginResponse grpcUser = userStub.login(GrpcLoginRequest.newBuilder()
                .setEmail(loginRequest.email())
                .setPassword(loginRequest.password())
                .build());
        log.info("login() : grpcUser={}", grpcUser);

        String token = jwtUtil.createToken(grpcUser.getEmail(), grpcUser.getId());
        log.info("login() : token={}", token);

        return UserResponse.of(grpcUser, token);
    }

    public UserResponse registerUser(final RegisterUserRequest registerUserRequest) {
        GrpcLoginResponse grpcUser = userStub.registerUser(GrpcRegisterUserRequest.newBuilder()
                .setUsername(registerUserRequest.username())
                .setEmail(registerUserRequest.email())
                .setPassword(registerUserRequest.password())
                .build());
        log.info("registerUser() : grpcUser={}", grpcUser);

        String token = jwtUtil.createToken(grpcUser.getEmail(), grpcUser.getId());
        log.info("login() : token={}", token);

        return UserResponse.of(grpcUser, token);
    }
}
