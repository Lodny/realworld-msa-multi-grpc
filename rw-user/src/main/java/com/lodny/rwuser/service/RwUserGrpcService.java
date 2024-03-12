package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.rwuser.*;
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
//    private final FollowGrpcClient followGrpcClient;

    @Override
    public void getUserIdByUsername(final GrpcGetUserIdByUsernameRequest request,
                                    final StreamObserver<GrpcGetUserIdByUsernameResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        GrpcGetUserIdByUsernameResponse response = GrpcGetUserIdByUsernameResponse.newBuilder()
                .setUserId(foundUser.getId())
                .build();
        log.info("getUserIdByUsername() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
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
                .build();
        log.info("login() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
