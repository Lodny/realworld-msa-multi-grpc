package com.lodny.rwuser.service;

import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.IsFollowingRequest;
import com.lodny.rwcommon.grpc.follow.IsFollowingResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FollowGrpcClient {

    @GrpcClient("follow-grpc")
    private FollowGrpc.FollowBlockingStub followStub;

    public Boolean isFollowing(final Long followeeId, final Long followerId) {
        IsFollowingResponse response = followStub.isFollowing(IsFollowingRequest.newBuilder()
                .setFolloweeId(followeeId)
                .setFollowerId(followerId)
                .build());
        log.info("isFollowing() : response={}", response);

        return response.getFollowing();
    }
}
