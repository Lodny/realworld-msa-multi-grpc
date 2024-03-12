package com.lodny.rwarticle.service;

import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.GrpcFolloweeIdsResponse;
import com.lodny.rwcommon.grpc.follow.GrpcFollowerIdRequest;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FollowGrpcClient {

    @GrpcClient("follow-grpc")
    private FollowGrpc.FollowBlockingStub followStub;

    public List<Long> getFolloweeIdsByFollowerId(final Long followerId) {
        log.info("getFolloweeIdsByFollowerId() : followerId={}", followerId);

        GrpcFolloweeIdsResponse response = followStub.getFolloweeIdsByFollowerId(GrpcFollowerIdRequest.newBuilder()
                .setFollowerId(followerId)
                .build());
        log.info("getFolloweeIdsByFollowerId() : response={}", response);

        return response.getFolloweeIdList();
    }

//
//    @GrpcClient("profile-grpc")
//    private ProfileGrpc.ProfileBlockingStub profileStub;
//
//    @GrpcClient("user-grpc")
//    private RwUserGrpc.RwUserBlockingStub userStub;
//
//
//    private long getFolloweeId(final String username) {
//        GrpcIdResponse userIdByUsername = userStub.getUserIdByUsername(GrpcUsernameRequest.newBuilder()
//                .setUsername(username)
//                .build());
//
//        return userIdByUsername.getUserId();
//    }
//
//    private ProfileResponse getProfileResponse(final long followerId, final long followeeId) {
//        GrpcProfileResponse grpcProfile = profileStub.getProfileByUserId(GrpcProfileByUserIdRequest.newBuilder()
//                .setUserId(followeeId)
//                .setFollowerId(followerId)
//                .build());
//
//        return new ProfileResponse(
//                grpcProfile.getUsername(),
//                grpcProfile.getBio(),
//                grpcProfile.getImage(),
//                grpcProfile.getFollowing());
//    }
//
//    public ProfileResponse follow(final String username, final long followerId) {
//        log.info("follow() : followerId={}", followerId);
//
//        long followeeId = getFolloweeId(username);
//        log.info("follow() : followeeId={}", followeeId);
//
//        Common.Empty empty = followStub.follow(GrpcFollowRequest.newBuilder()
//                .setFolloweeId(followeeId)
//                .setFollowerId(followerId)
//                .build());
//        log.info("follow() : empty={}", empty);
//
//        return getProfileResponse(followerId, followeeId);
//    }
//
//    public ProfileResponse unfollow(final String username, final long followerId) {
//        log.info("unfollow() : followerId={}", followerId);
//
//        long followeeId = getFolloweeId(username);
//        log.info("unfollow() : followeeId={}", followeeId);
//
//        Common.Empty empty = followStub.unfollow(GrpcFollowRequest.newBuilder()
//                .setFolloweeId(followeeId)
//                .setFollowerId(followerId)
//                .build());
//        log.info("unfollow() : empty={}", empty);
//
//        return getProfileResponse(followerId, followeeId);
//    }
}
