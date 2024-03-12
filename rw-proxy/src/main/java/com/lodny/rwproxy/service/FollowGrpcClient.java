package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.GrpcFollowRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwcommon.grpc.rwuser.GrpcGetUserIdByUsernameRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcGetUserIdByUsernameResponse;
import com.lodny.rwcommon.grpc.rwuser.RwUserGrpc;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FollowGrpcClient {

    @GrpcClient("follow-grpc")
    private FollowGrpc.FollowBlockingStub followStub;

    @GrpcClient("profile-grpc")
    private ProfileGrpc.ProfileBlockingStub profileStub;

    @GrpcClient("user-grpc")
    private RwUserGrpc.RwUserBlockingStub userStub;


    private long getFolloweeId(final String username) {
        GrpcGetUserIdByUsernameResponse userResponse = userStub.getUserIdByUsername(GrpcGetUserIdByUsernameRequest.newBuilder()
                .setUsername(username)
                .build());

        return userResponse.getId();
    }

    private ProfileResponse getProfileResponse(final long followerId, final long followeeId) {
        GrpcProfileResponse grpcProfile = profileStub.getProfileByUserId(GrpcProfileByUserIdRequest.newBuilder()
                .setUserId(followeeId)
                .setFollowerId(followerId)
                .build());

        return new ProfileResponse(
                grpcProfile.getUsername(),
                grpcProfile.getBio(),
                grpcProfile.getImage(),
                grpcProfile.getFollowing());
    }

    public ProfileResponse follow(final String username, final long followerId) {
        log.info("follow() : followerId={}", followerId);

        long followeeId = getFolloweeId(username);
        log.info("follow() : followeeId={}", followeeId);

        Common.Empty empty = followStub.follow(GrpcFollowRequest.newBuilder()
                .setFolloweeId(followeeId)
                .setFollowerId(followerId)
                .build());
        log.info("follow() : empty={}", empty);

        return getProfileResponse(followerId, followeeId);
    }

    public ProfileResponse unfollow(final String username, final long followerId) {
        log.info("unfollow() : followerId={}", followerId);

        long followeeId = getFolloweeId(username);
        log.info("unfollow() : followeeId={}", followeeId);

        Common.Empty empty = followStub.unfollow(GrpcFollowRequest.newBuilder()
                .setFolloweeId(followeeId)
                .setFollowerId(followerId)
                .build());
        log.info("unfollow() : empty={}", empty);

        return getProfileResponse(followerId, followeeId);
    }
}
