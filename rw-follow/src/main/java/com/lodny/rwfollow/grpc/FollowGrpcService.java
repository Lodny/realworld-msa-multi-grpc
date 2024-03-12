package com.lodny.rwfollow.grpc;

import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.GrpcIsFollowingRequest;
import com.lodny.rwcommon.grpc.follow.GrpcIsFollowingResponse;
import com.lodny.rwfollow.entity.Follow;
import com.lodny.rwfollow.entity.FollowId;
import com.lodny.rwfollow.repository.FollowRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FollowGrpcService extends FollowGrpc.FollowImplBase {

    private final FollowRepository followRepository;

    @Override
    public void isFollowing(final GrpcIsFollowingRequest request, final StreamObserver<GrpcIsFollowingResponse> responseObserver) {
        Follow following = followRepository.findById(new FollowId(request.getFolloweeId(), request.getFollowerId()));
        log.info("isFollowing() : following={}", following);

        GrpcIsFollowingResponse response = GrpcIsFollowingResponse.newBuilder()
                .setFollowing(following != null)
                .build();
        log.info("isFollowing() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
