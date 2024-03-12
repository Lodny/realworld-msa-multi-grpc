package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.GrpcIsFollowingRequest;
import com.lodny.rwcommon.grpc.follow.GrpcIsFollowingResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FollowGrpcClient {

    @GrpcClient("follow-grpc")
    private FollowGrpc.FollowBlockingStub followStub;

    public Boolean isFollowing(final Long followeeId, final Long followerId) {
        GrpcIsFollowingResponse response = followStub.isFollowing(GrpcIsFollowingRequest.newBuilder()
                .setFolloweeId(followeeId)
                .setFollowerId(followerId)
                .build());
        log.info("isFollowing() : response={}", response);

        return response.getFollowing();
    }
}
