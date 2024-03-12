package com.lodny.rwarticle.service;

import com.lodny.rwarticle.entity.dto.ProfileResponse;
import com.lodny.rwcommon.grpc.profile.GrpcProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfileGrpcClient {

    @GrpcClient("profile-grpc")
    private ProfileGrpc.ProfileBlockingStub profileStub;

    public ProfileResponse getProfileByUserId(final Long userId, final Long loginUserId) {
        GrpcProfileResponse profile = profileStub.getProfileByUserId(GrpcProfileByUserIdRequest.newBuilder()
                .setUserId(userId)
                .setFollowerId(loginUserId)
                .build());

        ProfileResponse profileResponse = new ProfileResponse(
                profile.getUsername(),
                profile.getBio(),
                profile.getImage(),
                profile.getFollowing());

        return profileResponse;
    }
}
