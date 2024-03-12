package com.lodny.rwarticle.service;

import com.lodny.rwarticle.entity.Article;
import com.lodny.rwarticle.repository.ArticleRepository;
import com.lodny.rwcommon.grpc.article.*;
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

    private void convertGrpcResponse(final StreamObserver<GrpcGetArticlesResponse> responseObserver, final Page<Article> articlePage) {
        GrpcGetArticlesResponse response = GrpcGetArticlesResponse.newBuilder()
                .addAllArticle(articlePage.getContent().stream()
                        .map(article -> GrpcGetArticleResponse.newBuilder()
                                .setSlug(article.getSlug())
                                .setTitle(article.getTitle())
                                .setDescription(article.getDescription())
                                .setBody(article.getBody())
                                .setCreatedAt(GrpcTimeUtil.toGrpcTimestamp(article.getCreatedAt()))
                                .setUpdatedAt(GrpcTimeUtil.toGrpcTimestamp(article.getUpdatedAt()))
                                .setAuthorId(article.getAuthorId())
                                .build())
                        .toList())
                .setCurrentPage(articlePage.getNumber())
                .setTotalPages(articlePage.getTotalPages())
                .setTotalElements(articlePage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArticleIdBySlug(final GrpcArticleSlugRequest request, final StreamObserver<GrpcGetArticleIdBySlugResponse> responseObserver) {
        Article foundArticle = articleRepository.findBySlug(request.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleIdBySlug() : foundArticle={}", foundArticle);

        GrpcGetArticleIdBySlugResponse response = GrpcGetArticleIdBySlugResponse.newBuilder()
                .setArticleId(foundArticle.getId())
                .build();
        log.info("getArticleIdBySlug() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getArticles(final GrpcGetArticlesRequest request,
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

        Page<Article> articlePage = articleRepository.findAllByIdInOrderByCreatedAtDesc(request.getArticleIdList(), pageRequest);
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
