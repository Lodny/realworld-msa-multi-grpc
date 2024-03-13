package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.GrpcFollowingResponse;
import com.lodny.rwcommon.grpc.follow.GrpcIsFollowingRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileByUsernameRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProfileGrpcService extends ProfileGrpc.ProfileImplBase {

    private final UserRepository userRepository;

    @GrpcClient("follow-grpc")
    private FollowGrpc.FollowBlockingStub followStub;

    public Boolean isFollowing(final Long followeeId, final Long followerId) {
        GrpcFollowingResponse response = followStub.isFollowing(GrpcIsFollowingRequest.newBuilder()
                .setFolloweeId(followeeId)
                .setFollowerId(followerId)
                .build());
        log.info("isFollowing() : response={}", response);

        return response.getFollowing();
    }

    private GrpcProfileResponse getGrpcProfileResponse(final long followeeId, final long followerId, final RealWorldUser foundUser) {
        Boolean following = isFollowing(followeeId, followerId);
        log.info("getGrpcProfileResponse() : following={}", following);

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
