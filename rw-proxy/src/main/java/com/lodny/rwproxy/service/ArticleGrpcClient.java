package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.article.ArticleGrpc;
import com.lodny.rwcommon.grpc.article.GrpcGetArticlesRequest;
import com.lodny.rwcommon.grpc.article.GrpcGetArticlesResponse;
import com.lodny.rwcommon.util.GrpcTimeUtil;
import com.lodny.rwproxy.entity.dto.ArticleParam;
import com.lodny.rwproxy.entity.dto.ArticleResponse;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleGrpcClient {

    @GrpcClient("article-grpc")
    private ArticleGrpc.ArticleBlockingStub articleStub;

    private static PageRequest getPageRequest(final ArticleParam articleParam) {
        int pageSize = articleParam.limit();
        int pageNo = articleParam.offset() / pageSize;

        return PageRequest.of(pageNo, pageSize);
    }

    public Page<ArticleResponse> getArticles(final ArticleParam articleParam, final long loginUserId, final String token) {
        GrpcGetArticlesResponse response = articleStub.getArticles(GrpcGetArticlesRequest.newBuilder()
                .setType("default")
                .setValue("")
                .setOffset(articleParam.offset())
                .setLimit(articleParam.limit())
                .build());
        log.info("getArticles() : response={}", response);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getArticles() : pageRequest={}", pageRequest);

        List<ArticleResponse> articleResponses = response.getArticleList().stream()
                .map(grpcArticle -> {
                    //todo::
                    Set<String> tagList = Set.of("Java", "React");
                    Boolean favorited = false;
                    Long favoritesCount = 10L;
                    ProfileResponse profile = new ProfileResponse("juice", "", "", false);

                    return new ArticleResponse(
                            grpcArticle.getSlug(),
                            grpcArticle.getTitle(),
                            grpcArticle.getDescription(),
                            grpcArticle.getBody(),
                            tagList,
                            GrpcTimeUtil.toLocalDateTime(grpcArticle.getCreatedAt()),
                            GrpcTimeUtil.toLocalDateTime(grpcArticle.getUpdatedAt()),
                            favorited,
                            favoritesCount,
                            profile);
                })
                .toList();
        log.info("getArticles() : articleResponses={}", articleResponses);

        return new PageImpl<>(articleResponses, pageRequest, response.getTotalElements());
    }
}
