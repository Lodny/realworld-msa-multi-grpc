package com.lodny.rwuser.grpc;

import com.lodny.rwcommon.grpc.profile.GrpcProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GrpcProfileResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwcommon.grpc.rwuser.GrpcGetUserIdByUsernameRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcGetUserIdByUsernameResponse;
import com.lodny.rwcommon.grpc.rwuser.RwUserGrpc;
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
public class RwUserGrpcService extends RwUserGrpc.RwUserImplBase {

    private final UserRepository userRepository;
//    private final FollowGrpcClient followGrpcClient;

    @Override
    public void getUserIdByUsername(final GrpcGetUserIdByUsernameRequest request,
                                    final StreamObserver<GrpcGetUserIdByUsernameResponse> responseObserver) {
        RealWorldUser foundUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        GrpcGetUserIdByUsernameResponse response = GrpcGetUserIdByUsernameResponse.newBuilder()
                .setUserId(foundUser.getId())
                .build();
        log.info("getUserIdByUsername() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
