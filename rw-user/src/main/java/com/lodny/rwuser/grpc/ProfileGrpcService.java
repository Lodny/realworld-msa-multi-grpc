package com.lodny.rwuser.grpc;

import com.lodny.rwcommon.grpc.profile.GetProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GetProfileByUserIdResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.repository.UserRepository;
import com.lodny.rwuser.service.FollowGrpcClient;
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
    public void getProfileByUserId(final GetProfileByUserIdRequest request,
                                   final StreamObserver<GetProfileByUserIdResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfileByUserId() : foundUser={}", foundUser);

        Boolean following = followGrpcClient.isFollowing(request.getUserId(), request.getFollowerId());

        GetProfileByUserIdResponse response = GetProfileByUserIdResponse.newBuilder()
                .setUsername(foundUser.getUsername())
                .setBio(Optional.ofNullable(foundUser.getBio()).orElse(""))
                .setImage(Optional.ofNullable(foundUser.getImage()).orElse(""))
                .setFollowing(following)
                .build();
        log.info("getProfileByUserId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
