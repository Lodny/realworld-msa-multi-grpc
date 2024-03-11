package com.lodny.rwarticle.grpc;

import com.lodny.rwarticle.entity.Article;
import com.lodny.rwarticle.repository.ArticleRepository;
import com.lodny.rwcommon.grpc.article.ArticleGrpc;
import com.lodny.rwcommon.grpc.article.GetArticleIdBySlugRequest;
import com.lodny.rwcommon.grpc.article.GetArticleIdBySlugResponse;
import com.lodny.rwcommon.grpc.tag.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ArticleGrpcService extends ArticleGrpc.ArticleImplBase {

    private final ArticleRepository articleRepository;

    @Override
    public void getArticleIdBySlug(final GetArticleIdBySlugRequest request, final StreamObserver<GetArticleIdBySlugResponse> responseObserver) {
        Article foundArticle = articleRepository.findBySlug(request.getSlug())
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleIdBySlug() : foundArticle={}", foundArticle);

        GetArticleIdBySlugResponse response = GetArticleIdBySlugResponse.newBuilder()
                .setArticleId(foundArticle.getId())
                .build();
        log.info("getArticleIdBySlug() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    //    @Override
//    public void registerTags(final RegisterTagsRequest request, final StreamObserver<Empty> responseObserver) {
//        long articleId = request.getArticleId();
//        log.info("registerTags() : articleId={}", articleId);
//
//        articleRepository.saveAll(
//                request.getTagsList().stream()
//                        .map(tag -> new Tag(articleId, tag))
//                        .toList());
//
//        responseObserver.onNext(Empty.getDefaultInstance());
//        responseObserver.onCompleted();
//    }
}
