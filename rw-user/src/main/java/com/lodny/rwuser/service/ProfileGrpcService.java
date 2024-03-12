package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.profile.GrpcProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.entity.dto.ProfileResponse;
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

    @Override
    public void getProfileByUserId(final GrpcProfileByUserIdRequest request,
                                   final StreamObserver<GrpcProfileResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfileByUserId() : foundUser={}", foundUser);

        Boolean following = followGrpcClient.isFollowing(request.getUserId(), request.getFollowerId());

        GrpcProfileResponse response = GrpcProfileResponse.newBuilder()
                .setUsername(foundUser.getUsername())
                .setBio(Optional.ofNullable(foundUser.getBio()).orElse(""))
                .setImage(Optional.ofNullable(foundUser.getImage()).orElse(""))
                .setFollowing(following)
                .build();
        log.info("getProfileByUserId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public ProfileResponse getProfile(final String username, final Long loginUserId) {
        RealWorldUser foundUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfile() : foundUser={}", foundUser);

        Boolean following = followGrpcClient.isFollowing(foundUser.getId(), loginUserId);
        log.info("getProfile() : following={}", following);

        return ProfileResponse.of(foundUser, following);
    }
}
