package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.profile.GrpcProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileByUsernameRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
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
public class ProfileGrpcService extends ProfileGrpc.ProfileImplBase {

    private final UserRepository userRepository;
    private final FollowGrpcClient followGrpcClient;

    private GrpcProfileResponse getGrpcProfileResponse(final long followeeId, final long followerId, final RealWorldUser foundUser) {
        Boolean following = followGrpcClient.isFollowing(followeeId, followerId);

        return GrpcProfileResponse.newBuilder()
                .setUsername(foundUser.getUsername())
                .setBio(Optional.ofNullable(foundUser.getBio()).orElse(""))
                .setImage(Optional.ofNullable(foundUser.getImage()).orElse(""))
                .setFollowing(following)
                .build();
    }

    @Override
    public void getProfileByUserId(final GrpcProfileByUserIdRequest request,
                                   final StreamObserver<GrpcProfileResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfileByUserId() : foundUser={}", foundUser);

        GrpcProfileResponse response = getGrpcProfileResponse(request.getUserId(), request.getFollowerId(), foundUser);
        log.info("getProfileByUserId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProfileByUsername(final GrpcProfileByUsernameRequest request,
                                     final StreamObserver<GrpcProfileResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfileByUsername() : foundUser={}", foundUser);

        GrpcProfileResponse response = getGrpcProfileResponse(foundUser.getId(), request.getFollowerId(), foundUser);
        log.info("getProfileByUsername() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
