package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.article.ArticleGrpc;
import com.lodny.rwcommon.grpc.article.GetArticleIdBySlugRequest;
import com.lodny.rwcommon.grpc.article.GetArticleIdBySlugResponse;
import com.lodny.rwcommon.grpc.comment.*;
import com.lodny.rwcommon.grpc.profile.GetProfileByUserIdRequest;
import com.lodny.rwcommon.grpc.profile.GetProfileByUserIdResponse;
import com.lodny.rwcommon.grpc.profile.ProfileGrpc;
import com.lodny.rwcommon.util.GrpcTimeUtil;
import com.lodny.rwproxy.entity.dto.CommentResponse;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentGrpcClient {

    @GrpcClient("comment-grpc")
    private CommentGrpc.CommentBlockingStub commentStub;

    @GrpcClient("article-grpc")
    private ArticleGrpc.ArticleBlockingStub articleStub;

    @GrpcClient("profile-grpc")
    private ProfileGrpc.ProfileBlockingStub profileStub;


    public CommentResponse registerComment(final Long articleId, final String body, final long authorId) {
        RegisterCommentResponse comment = commentStub.registerComment(RegisterCommentRequest.newBuilder()
                .setArticleId(articleId)
                .setAuthorId(authorId)
                .setBody(body)
                .build());
        log.info("registerComment() : comment={}", comment);

        return getCommentResponse(authorId, comment);
    }

    private CommentResponse getCommentResponse(final long authorId, final RegisterCommentResponse comment) {
        GetProfileByUserIdResponse profile = profileStub.getProfileByUserId(GetProfileByUserIdRequest.newBuilder()
                .setUserId(comment.getAuthorId())
                .setFollowerId(authorId)
                .build());

        ProfileResponse profileResponse = new ProfileResponse(
                profile.getUsername(),
                profile.getBio(),
                profile.getImage(),
                profile.getFollowing());

        return new CommentResponse(
                comment.getId(),
                GrpcTimeUtil.toLocalDateTime(comment.getCreatedAt()),
                GrpcTimeUtil.toLocalDateTime(comment.getUpdatedAt()),
                comment.getBody(),
                profileResponse);
    }

    public Long getArticleIdBySlug(final String slug) {
        GetArticleIdBySlugResponse response = articleStub.getArticleIdBySlug(
                GetArticleIdBySlugRequest.newBuilder()
                        .setSlug(slug)
                        .build());
        log.info("getArticleIdBySlug() : response={}", response);

        return response.getArticleId();
    }

    public List<CommentResponse> getCommentsByArticleId(final Long articleId, final Long followerId) {
        GetCommentsByArticleIdResponse comments = commentStub.getCommentsByArticleId(
                GetCommentsByArticleIdRequest.newBuilder()
                        .setArticleId(articleId)
                        .build());
        log.info("getCommentsByArticleId() : comments={}", comments);

        return comments.getCommentsList().stream()
                .map(comment -> getCommentResponse(followerId, comment))
                .toList();
    }
}
