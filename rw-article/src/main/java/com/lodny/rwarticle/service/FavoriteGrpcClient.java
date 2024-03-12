package com.lodny.rwarticle.service;

import com.lodny.rwcommon.grpc.favorite.FavoriteGrpc;
import com.lodny.rwcommon.grpc.favorite.GrpcFavoriteRequest;
import com.lodny.rwcommon.grpc.favorite.GrpcGetFavoriteInfoResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FavoriteGrpcClient {

    @GrpcClient("favorite-grpc")
    private FavoriteGrpc.FavoriteBlockingStub favoriteStub;

    public Long[] getFavoriteInfoByArticleId(final Long articleId, final Long loginUserId) {
        log.info("getFavoriteInfoByArticleId() : articleId={}", articleId);

        GrpcGetFavoriteInfoResponse response = favoriteStub.getFavoriteInfo(GrpcFavoriteRequest.newBuilder()
                .setArticleId(articleId)
                .setUserId(loginUserId)
                .build());
        log.info("getFavoriteInfoByArticleId() : response={}", response);

        return new Long[]{response.getFavoritesCount(), response.getFavorited()};
    }

//
//    @GrpcClient("profile-grpc")
//    private ProfileGrpc.ProfileBlockingStub profileStub;
//
//    @GrpcClient("user-grpc")
//    private RwUserGrpc.RwUserBlockingStub userStub;
//
//
//    private long getFavoriteeeId(final String username) {
//        GrpcGetUserIdByUsernameResponse userIdByUsername = userStub.getUserIdByUsername(GrpcGetUserIdByUsernameRequest.newBuilder()
//                .setUsername(username)
//                .build());
//
//        return userIdByUsername.getUserId();
//    }
//
//    private ProfileResponse getProfileResponse(final long favoriteerId, final long favoriteeeId) {
//        GrpcProfileResponse grpcProfile = profileStub.getProfileByUserId(GrpcProfileByUserIdRequest.newBuilder()
//                .setUserId(favoriteeeId)
//                .setFavoriteerId(favoriteerId)
//                .build());
//
//        return new ProfileResponse(
//                grpcProfile.getUsername(),
//                grpcProfile.getBio(),
//                grpcProfile.getImage(),
//                grpcProfile.getFavoriteing());
//    }
//
//    public ProfileResponse favorite(final String username, final long favoriteerId) {
//        log.info("favorite() : favoriteerId={}", favoriteerId);
//
//        long favoriteeeId = getFavoriteeeId(username);
//        log.info("favorite() : favoriteeeId={}", favoriteeeId);
//
//        Common.Empty empty = favoriteStub.favorite(GrpcFavoriteRequest.newBuilder()
//                .setFavoriteeeId(favoriteeeId)
//                .setFavoriteerId(favoriteerId)
//                .build());
//        log.info("favorite() : empty={}", empty);
//
//        return getProfileResponse(favoriteerId, favoriteeeId);
//    }
//
//    public ProfileResponse unfavorite(final String username, final long favoriteerId) {
//        log.info("unfavorite() : favoriteerId={}", favoriteerId);
//
//        long favoriteeeId = getFavoriteeeId(username);
//        log.info("unfavorite() : favoriteeeId={}", favoriteeeId);
//
//        Common.Empty empty = favoriteStub.unfavorite(GrpcFavoriteRequest.newBuilder()
//                .setFavoriteeeId(favoriteeeId)
//                .setFavoriteerId(favoriteerId)
//                .build());
//        log.info("unfavorite() : empty={}", empty);
//
//        return getProfileResponse(favoriteerId, favoriteeeId);
//    }
}
