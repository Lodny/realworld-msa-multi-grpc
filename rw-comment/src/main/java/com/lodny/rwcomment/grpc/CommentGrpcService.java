package com.lodny.rwcomment.grpc;

import com.lodny.rwcomment.entity.Comment;
import com.lodny.rwcomment.repository.CommentRepository;
import com.lodny.rwcommon.grpc.comment.CommentGrpc;
import com.lodny.rwcommon.grpc.comment.GetCommentsByArticleIdRequest;
import com.lodny.rwcommon.grpc.comment.GetCommentsByArticleIdResponse;
import com.lodny.rwcommon.grpc.comment.RegisterCommentResponse;
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
    public void getCommentsByArticleId(final GetCommentsByArticleIdRequest request,
                                       final StreamObserver<GetCommentsByArticleIdResponse> responseObserver) {
        List<Comment> comments = commentRepository.findAllByArticleIdOrderByCreatedAtDesc(request.getArticleId());
        log.info("getComments() : comments={}", comments);

        GetCommentsByArticleIdResponse response = GetCommentsByArticleIdResponse.newBuilder()
                .addAllComments(comments.stream()
                    .map(comment -> RegisterCommentResponse.newBuilder()
                        .setId(comment.getId())
                        .setCreatedAt(GrpcTimeUtil.toGrpcTimestamp(comment.getCreatedAt()))
                        .setUpdatedAt(GrpcTimeUtil.toGrpcTimestamp(comment.getUpdatedAt()))
                        .setBody(comment.getBody())
                        .setAuthorId(comment.getAuthorId())
                        .build())
                    .toList())
                .build();

        log.info("getCommentsByArticleId() : response={}", response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
