package com.lodny.rwfavorite.service;

import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.favorite.*;
import com.lodny.rwfavorite.entity.Favorite;
import com.lodny.rwfavorite.entity.FavoriteId;
import com.lodny.rwfavorite.repository.FavoriteRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FavoriteGrpcService extends FavoriteGrpc.FavoriteImplBase {

    private final FavoriteRepository favoriteRepository;

    @Override
    public void favorite(final GrpcFavoriteRequest request, final StreamObserver<Common.Empty> responseObserver) {
        log.info("favorite() : 1={}", 1);
    }

    @Override
    public void unfavorite(final GrpcFavoriteRequest request, final StreamObserver<Common.Empty> responseObserver) {
        log.info("unfavorite() : 1={}", 1);
    }

    @Override
    public void getFavoriteInfo(final GrpcFavoriteRequest request,
                                final StreamObserver<GrpcGetFavoriteInfoResponse> responseObserver) {
        long articleId = request.getArticleId();
        Long favoritesCount = favoriteRepository.countByIdArticleId(articleId);

        FavoriteId favoriteId = new FavoriteId(articleId, request.getUserId());
        log.info("getFavoriteInfo() : favoriteId={}", favoriteId);

        Favorite favorite = favoriteRepository.findById(favoriteId);
        log.info("getFavoriteInfo() : favorite={}", favorite);

        GrpcGetFavoriteInfoResponse response = GrpcGetFavoriteInfoResponse.newBuilder()
                .setFavoritesCount(favoritesCount)
                .setFavorited(favorite != null ? 1L : 0L)
                .build();
        log.info("getFavoriteInfo() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getFavoriteArticleIdsByUserId(final GrpcUserIdRequest request, final StreamObserver<GrpcGetFavoriteArticleIdsResponse> responseObserver) {
        log.info("getFavoriteArticleIdsByUserId() : 1={}", 1);
    }
}
