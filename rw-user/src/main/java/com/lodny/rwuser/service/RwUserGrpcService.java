package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.rwuser.*;
import com.lodny.rwcommon.util.CommonGrpcUtil;
import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RwUserGrpcService extends RwUserGrpc.RwUserImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUserIdByUsername(final Common.GrpcUsernameRequest request,
                                    final StreamObserver<Common.GrpcIdResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Common.GrpcIdResponse response = Common.GrpcIdResponse.newBuilder()
                .setId(foundUser.getId())
                .build();
        log.info("getUserIdByUsername() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void login(final GrpcLoginRequest request,
                      final StreamObserver<GrpcLoginResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("email or password is invalid"));
        log.info("login() : foundUser={}", foundUser);

        if (! foundUser.getPassword().equals(request.getPassword()))
            throw new IllegalArgumentException("email or password is invalid");

        GrpcLoginResponse response = GrpcLoginResponse.newBuilder()
                .setEmail(foundUser.getEmail())
                .setUsername(foundUser.getUsername())
                .setBio(Optional.ofNullable(foundUser.getBio()).orElse(""))
                .setImage(Optional.ofNullable(foundUser.getImage()).orElse(""))
                .setId(foundUser.getId())
                .build();
        log.info("login() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void registerUser(final GrpcRegisterUserRequest request,
                             final StreamObserver<GrpcLoginResponse> responseObserver) {
        RealWorldUser newUser = RealWorldUser.of(request);
        log.info("registerUser() : newUser={}", newUser);

        RealWorldUser savedUser = userRepository.save(newUser);
        log.info("registerUser() : savedUser={}", savedUser);

        GrpcLoginResponse response = GrpcLoginResponse.newBuilder()
                .setEmail(savedUser.getEmail())
                .setUsername(savedUser.getUsername())
                .setId(savedUser.getId())
                .build();
        log.info("registerUser() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void updateUser(final GrpcUpdateUserRequest request, final StreamObserver<GrpcLoginResponse> responseObserver) {
        log.info("updateUser() : 2={}", 2);
    }
}
