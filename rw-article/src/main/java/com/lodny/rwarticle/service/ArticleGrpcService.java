package com.lodny.rwarticle.service;

import com.lodny.rwarticle.entity.Article;
import com.lodny.rwarticle.repository.ArticleRepository;
import com.lodny.rwcommon.grpc.article.*;
import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.util.CommonGrpcUtil;
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

    private GrpcArticleResponse getGrpcGetArticleResponse(final Article article) {
        return GrpcArticleResponse.newBuilder()
                .setId(article.getId())
                .setSlug(article.getSlug())
                .setTitle(article.getTitle())
                .setDescription(article.getDescription())
                .setBody(article.getBody())
                .setCreatedAt(CommonGrpcUtil.toGrpcTimestamp(article.getCreatedAt()))
                .setUpdatedAt(CommonGrpcUtil.toGrpcTimestamp(article.getUpdatedAt()))
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

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void registerArticle(final GrpcRegisterArticleRequest request, final StreamObserver<GrpcArticleResponse> responseObserver) {
        Article article = Article.of(request);
        Article savedArticle = articleRepository.save(article);

        GrpcArticleResponse response = getGrpcGetArticleResponse(savedArticle);
        log.info("registerArticle() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
    }

    @Override
    public void getArticleBySlug(final Common.GrpcSlugRequest request,
                                 final StreamObserver<GrpcArticleResponse> responseObserver) {
        Article foundArticle = articleRepository.findBySlug(request.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleBySlug() : foundArticle={}", foundArticle);

        GrpcArticleResponse response = getGrpcGetArticleResponse(foundArticle);
        log.info("getArticleBySlug() : response={}", response);

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
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

        CommonGrpcUtil.completeResponseObserver(responseObserver, response);
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

    @Override
    public void deleteArticleBySlug(final GrpcSlugAuthorIdRequest request, final StreamObserver<Common.Empty> responseObserver) {
        Article foundArticle = articleRepository.findBySlug(request.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("deleteArticleBySlug() : foundArticle={}", foundArticle);

        if (! foundArticle.getAuthorId().equals(request.getAuthorId()))
            throw new IllegalArgumentException("authorId is wrong");

        articleRepository.delete(foundArticle);
        CommonGrpcUtil.completeResponseObserver(responseObserver, Common.Empty.getDefaultInstance());
    }
}
