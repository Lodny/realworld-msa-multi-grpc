package com.lodny.rwcomment.grpc;

import com.lodny.rwcomment.entity.Comment;
import com.lodny.rwcomment.repository.CommentRepository;
import com.lodny.rwcommon.grpc.comment.*;
import com.lodny.rwcommon.grpc.common.Common;
import com.lodny.rwcommon.util.GrpcTimeUtil;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CommentGrpcService extends CommentGrpc.CommentImplBase {

    private final CommentRepository commentRepository;

    @Override
    public void registerComment(final GrpcRegisterCommentRequest request, final StreamObserver<GrpcCommentResponse> responseObserver) {
        Comment comment = Comment.of(request.getBody(), request.getArticleId(), request.getAuthorId());
        log.info("registerComment() : comment={}", comment);

        Comment savedComment = commentRepository.save(comment);
        log.info("registerComment() : savedComment={}", savedComment);

        GrpcCommentResponse response = getRegisterCommentResponse(savedComment);
        log.info("registerComment() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private GrpcCommentResponse getRegisterCommentResponse(final Comment comment) {
        return GrpcCommentResponse.newBuilder()
                .setId(comment.getId())
                .setCreatedAt(GrpcTimeUtil.toGrpcTimestamp(comment.getCreatedAt()))
                .setUpdatedAt(GrpcTimeUtil.toGrpcTimestamp(comment.getUpdatedAt()))
                .setBody(comment.getBody())
                .setAuthorId(comment.getAuthorId())
                .build();
    }

    @Override
    public void getCommentsByArticleId(final GrpcGetCommentsByArticleIdRequest request,
                                       final StreamObserver<GrpcGetCommentsByArticleIdResponse> responseObserver) {
        List<Comment> comments = commentRepository.findAllByArticleIdOrderByCreatedAtDesc(request.getArticleId());
        log.info("getComments() : comments={}", comments);

        GrpcGetCommentsByArticleIdResponse response = GrpcGetCommentsByArticleIdResponse.newBuilder()
                .addAllComments(comments.stream()
                    .map(this::getRegisterCommentResponse)
                    .toList())
                .build();
        log.info("getCommentsByArticleId() : response={}", response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCommentById(final GrpcDeleteCommentByIdRequest request, final StreamObserver<Common.Empty> responseObserver) {
        long commentId = request.getId();
        log.info("deleteCommentById() : commentId={}", commentId);

        commentRepository.deleteById(commentId);

        responseObserver.onNext(Common.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
