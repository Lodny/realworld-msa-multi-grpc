package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.profile.GrpcProfileByUsernameRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfileGrpcClient {

    @GrpcClient("profile-grpc")
    private ProfileGrpc.ProfileBlockingStub profileStub;

    public ProfileResponse getProfileByUsername(final String username, final Long loginUserId) {
        GrpcProfileResponse profile = profileStub.getProfileByUsername(GrpcProfileByUsernameRequest.newBuilder()
                .setUsername(username)
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
