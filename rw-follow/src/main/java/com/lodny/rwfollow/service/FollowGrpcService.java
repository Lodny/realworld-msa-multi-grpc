package com.lodny.rwfollow.service;

import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.follow.*;
import com.lodny.rwfollow.entity.Follow;
import com.lodny.rwfollow.entity.FollowId;
import com.lodny.rwfollow.repository.FollowRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FollowGrpcService extends FollowGrpc.FollowImplBase {

    private final FollowRepository followRepository;

    @Override
    public void isFollowing(final GrpcIsFollowingRequest request, final StreamObserver<GrpcFollowingResponse> responseObserver) {
        Follow following = followRepository.findById(new FollowId(request.getFolloweeId(), request.getFollowerId()));
        log.info("isFollowing() : following={}", following);

        GrpcFollowingResponse response = GrpcFollowingResponse.newBuilder()
                .setFollowing(following != null)
                .build();
        log.info("isFollowing() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void follow(final GrpcFollowRequest request,
                       final StreamObserver<Common.Empty> responseObserver) {
        Follow follow = new Follow(new FollowId(request.getFolloweeId(), request.getFollowerId()));
        log.info("follow() : follow={}", follow);

        followRepository.save(follow);

        responseObserver.onNext(Common.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void unfollow(final GrpcFollowRequest request, final StreamObserver<Common.Empty> responseObserver) {
        FollowId followId = new FollowId(request.getFolloweeId(), request.getFollowerId());
        log.info("unfollow() : followId={}", followId);

        followRepository.deleteById(followId);

        responseObserver.onNext(Common.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getFolloweeIdsByFollowerId(final GrpcFollowerIdRequest request,
                                           final StreamObserver<GrpcFolloweeIdsResponse> responseObserver) {
        long followerId = request.getFollowerId();
        log.info("getFolloweeIdsByFollowerId() : followerId={}", followerId);

        List<Long> followees = followRepository.findAllByIdFollowerId(followerId).stream()
                .map(follow -> follow.getId().getFolloweeId())
                .toList();
        log.info("getFolloweeIdsByFollowerId() : followees={}", followees);

        GrpcFolloweeIdsResponse response = GrpcFolloweeIdsResponse.newBuilder()
                .addAllFolloweeId(followees)
                .build();
        log.info("getFolloweeIdsByFollowerId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
