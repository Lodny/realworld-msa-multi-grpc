package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.article.ArticleGrpc;
import com.lodny.rwcommon.grpc.article.GrpcArticleResponse;
import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.grpc.favorite.FavoriteGrpc;
import com.lodny.rwcommon.grpc.favorite.GrpcFavoriteRequest;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FavoriteGrpcClient {

    @GrpcClient("favorite-grpc")
    private FavoriteGrpc.FavoriteBlockingStub favoriteStub;

    @GrpcClient("article-grpc")
    private ArticleGrpc.ArticleBlockingStub articleStub;

    @GrpcClient("profile-grpc")
    private ProfileGrpc.ProfileBlockingStub profileStub;

    public GrpcArticleResponse favorite(final String slug, final long loginUserId) {
        GrpcArticleResponse articleResponse = articleStub.getArticleBySlug(Common.GrpcSlugRequest.newBuilder()
                .setSlug(slug)
                .build());
        log.info("favorite() : articleResponse={}", articleResponse);

        favoriteStub.favorite(GrpcFavoriteRequest.newBuilder()
                .setArticleId(articleResponse.getId())
                .setUserId(loginUserId)
                .build());

        return articleStub.getArticleBySlug(Common.GrpcSlugRequest.newBuilder()
                .setSlug(slug)
                .build());
    }

    public GrpcArticleResponse unfavorite(final String slug, final long loginUserId) {
        GrpcArticleResponse articleResponse = articleStub.getArticleBySlug(Common.GrpcSlugRequest.newBuilder()
                .setSlug(slug)
                .build());
        log.info("unfavorite() : articleResponse={}", articleResponse);

        favoriteStub.unfavorite(GrpcFavoriteRequest.newBuilder()
                .setArticleId(articleResponse.getId())
                .setUserId(loginUserId)
                .build());

        return articleStub.getArticleBySlug(Common.GrpcSlugRequest.newBuilder()
                .setSlug(slug)
                .build());
    }
}
