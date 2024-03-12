package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.article.*;
import com.lodny.rwcommon.grpc.follow.FollowGrpc;
import com.lodny.rwcommon.grpc.follow.GrpcFolloweeIdsResponse;
import com.lodny.rwcommon.grpc.follow.GrpcFollowerIdRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcGetUserIdByUsernameRequest;
import com.lodny.rwcommon.grpc.rwuser.GrpcGetUserIdByUsernameResponse;
import com.lodny.rwcommon.grpc.rwuser.RwUserGrpc;
import com.lodny.rwcommon.grpc.tag.GrpcArticleIdsByTagStringRequest;
import com.lodny.rwcommon.grpc.tag.GrpcArticleIdsByTagStringResponse;
import com.lodny.rwcommon.grpc.tag.TagGrpc;
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

    @GrpcClient("tag-grpc")
    private TagGrpc.TagBlockingStub tagStub;

    @GrpcClient("user-grpc")
    private RwUserGrpc.RwUserBlockingStub userStub;

    @GrpcClient("follow-grpc")
    private FollowGrpc.FollowBlockingStub followStub;

    private static PageRequest getPageRequest(final ArticleParam articleParam) {
        int pageSize = articleParam.limit();
        int pageNo = articleParam.offset() / pageSize;

        return PageRequest.of(pageNo, pageSize);
    }

    private PageImpl<ArticleResponse> getArticleResponses(final GrpcGetArticlesResponse response, final PageRequest pageRequest) {
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

    public Page<ArticleResponse> getArticles(final ArticleParam articleParam,
                                             final long loginUserId) {
        GrpcGetArticlesResponse response = articleStub.getArticles(GrpcGetArticlesRequest.newBuilder()
                .setOffset(articleParam.offset())
                .setLimit(articleParam.limit())
                .build());
        log.info("getArticles() : response={}", response);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getArticles() : pageRequest={}", pageRequest);

        return getArticleResponses(response, pageRequest);
    }

    public Page<ArticleResponse> getArticlesByTag(final ArticleParam articleParam,
                                                  final Long loginUserId,
                                                  final String tagString) {
        GrpcArticleIdsByTagStringResponse tagResponse = tagStub.getArticleIdsByTagString(GrpcArticleIdsByTagStringRequest.newBuilder()
                .setTagString(tagString)
                .build());
        List<Long> articleIds = tagResponse.getArticleIdList();
        log.info("getArticlesByTag() : articleIds={}", articleIds);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getArticles() : pageRequest={}", pageRequest);

        GrpcGetArticlesResponse response = articleStub.getArticlesByArticleIds(GrpcArticlesByArticleIdsRequest.newBuilder()
                .setOffset(articleParam.offset())
                .setLimit(articleParam.limit())
                .addAllArticleId(articleIds)
                .build());
        log.info("getArticles() : response={}", response);

        return getArticleResponses(response, pageRequest);
    }

    public Page<ArticleResponse> getArticlesByAuthor(final ArticleParam articleParam,
                                                        final long loginUserId,
                                                        final String author) {
        GrpcGetUserIdByUsernameResponse userResponse = userStub.getUserIdByUsername(GrpcGetUserIdByUsernameRequest.newBuilder()
                .setUsername(author)
                .build());
        long userId = userResponse.getUserId();
        log.info("getArticlesByAuthorIds() : userId={}", userId);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getArticles() : pageRequest={}", pageRequest);

        GrpcGetArticlesResponse response = articleStub.getArticlesByAuthorIds(GrpcArticlesByAuthorIdsRequest.newBuilder()
                .setOffset(articleParam.offset())
                .setLimit(articleParam.limit())
                .addAllAuthorId(List.of(userId))
                .build());
        log.info("getArticlesByAuthorIds() : response={}", response);

        return getArticleResponses(response, pageRequest);
    }

    public Page<ArticleResponse> getFeedArticlesByLoginUser(final ArticleParam articleParam, final long followerId) {
        GrpcFolloweeIdsResponse followResponse = followStub.getFolloweeIdsByFollowerId(GrpcFollowerIdRequest.newBuilder()
                .setFollowerId(followerId)
                .build());
        log.info("getFeedArticlesByLoginUser() : followResponse={}", followResponse);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getArticles() : pageRequest={}", pageRequest);

        GrpcGetArticlesResponse response = articleStub.getArticlesByAuthorIds(GrpcArticlesByAuthorIdsRequest.newBuilder()
                .setOffset(articleParam.offset())
                .setLimit(articleParam.limit())
                .addAllAuthorId(followResponse.getFolloweeIdList())
                .build());
        log.info("getArticlesByAuthorIds() : response={}", response);

        return getArticleResponses(response, pageRequest);
    }
}
