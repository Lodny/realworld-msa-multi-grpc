package com.lodny.rwarticle.service;

import com.lodny.rwarticle.entity.Article;
import com.lodny.rwarticle.repository.ArticleRepository;
import com.lodny.rwcommon.grpc.article.*;
import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.util.GrpcTimeUtil;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ArticleGrpcService extends ArticleGrpc.ArticleImplBase {

    private final ArticleRepository articleRepository;

    private PageRequest getPageRequest(final int offset, final int limit) {
        return PageRequest.of(offset / limit, limit);
    }

    private GrpcGetArticleResponse getGrpcGetArticleResponse(final Article article) {
        return GrpcGetArticleResponse.newBuilder()
                .setId(article.getId())
                .setSlug(article.getSlug())
                .setTitle(article.getTitle())
                .setDescription(article.getDescription())
                .setBody(article.getBody())
                .setCreatedAt(GrpcTimeUtil.toGrpcTimestamp(article.getCreatedAt()))
                .setUpdatedAt(GrpcTimeUtil.toGrpcTimestamp(article.getUpdatedAt()))
                .setAuthorId(article.getAuthorId())
                .build();
    }

    private void convertGrpcResponse(final StreamObserver<GrpcGetArticlesResponse> responseObserver, final Page<Article> articlePage) {
        GrpcGetArticlesResponse response = GrpcGetArticlesResponse.newBuilder()
                .addAllArticle(articlePage.getContent().stream()
                        .map(this::getGrpcGetArticleResponse)
                        .toList())
                .setCurrentPage(articlePage.getNumber())
                .setTotalPages(articlePage.getTotalPages())
                .setTotalElements(articlePage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArticleBySlug(final Common.GrpcSlugRequest request,
                                 final StreamObserver<GrpcGetArticleResponse> responseObserver) {
        Article foundArticle = articleRepository.findBySlug(request.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleBySlug() : foundArticle={}", foundArticle);

        GrpcGetArticleResponse response = getGrpcGetArticleResponse(foundArticle);
        log.info("getArticleBySlug() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArticleIdBySlug(final Common.GrpcSlugRequest request,
                                   final StreamObserver<Common.GrpcIdResponse> responseObserver) {
        Article foundArticle = articleRepository.findBySlug(request.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleIdBySlug() : foundArticle={}", foundArticle);

        Common.GrpcIdResponse response = Common.GrpcIdResponse.newBuilder()
                .setId(foundArticle.getId())
                .build();
        log.info("getArticleIdBySlug() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArticles(final Common.GrpcPageRequest request,
                            final StreamObserver<GrpcGetArticlesResponse> responseObserver) {
        PageRequest pageRequest = getPageRequest(request.getOffset(), request.getLimit());
        log.info("getArticles() : pageRequest={}", pageRequest);

        Page<Article> articlePage = articleRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        convertGrpcResponse(responseObserver, articlePage);
    }

    @Override
    public void getArticlesByArticleIds(final GrpcArticlesByArticleIdsRequest request, final StreamObserver<GrpcGetArticlesResponse> responseObserver) {
        PageRequest pageRequest = getPageRequest(request.getOffset(), request.getLimit());
        log.info("getArticles() : pageRequest={}", pageRequest);

        Page<Article> articlePage = articleRepository.findAllByIdInOrderByCreatedAtDesc(request.getIdList(), pageRequest);
        convertGrpcResponse(responseObserver, articlePage);
    }

    @Override
    public void getArticlesByAuthorIds(final GrpcArticlesByAuthorIdsRequest request, final StreamObserver<GrpcGetArticlesResponse> responseObserver) {
        PageRequest pageRequest = getPageRequest(request.getOffset(), request.getLimit());
        log.info("getArticles() : pageRequest={}", pageRequest);

        Page<Article> articlePage = articleRepository.findAllByAuthorIdInOrderByCreatedAtDesc(request.getAuthorIdList(), pageRequest);
        convertGrpcResponse(responseObserver, articlePage);
    }
}
