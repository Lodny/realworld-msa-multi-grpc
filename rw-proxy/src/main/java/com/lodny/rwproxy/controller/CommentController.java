package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.grpc.comment.RegisterCommentResponse;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.CommentResponse;
import com.lodny.rwproxy.entity.dto.RegisterCommentRequest;
import com.lodny.rwproxy.entity.wrapper.WrapCommentResponses;
import com.lodny.rwproxy.entity.wrapper.WrapRegisterCommentRequest;
import com.lodny.rwproxy.service.CommentGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/{slug}")
public class CommentController {
    private final CommentGrpcClient commentGrpcClient;

    private String getTokenByLoginInfo(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getToken() : "";
    }

    private Long getLoginIdByLoginInfo(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getUserId() : -1L;
    }

    @JwtTokenRequired
    @PostMapping("/comments")
    public ResponseEntity<?> registerComment(@PathVariable final String slug,
                                             @RequestBody final WrapRegisterCommentRequest wrapRegisterCommentRequest,
                                             @LoginUser final LoginInfo loginInfo) {
        RegisterCommentRequest registerCommentRequest = wrapRegisterCommentRequest.comment();
        log.info("registerComment() : slug={}", slug);
        log.info("registerComment() : registerCommentRequest={}", registerCommentRequest);
        log.info("registerComment() : loginInfo={}", loginInfo);

        Long articleId = commentGrpcClient.getArticleIdBySlug(slug);
        log.info("registerComment() : articleId={}", articleId);

        RegisterCommentResponse response = commentGrpcClient.registerComment(articleId, registerCommentRequest.body(), loginInfo.getUserId());
        log.info("registerComment() : response={}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapCommentResponse(response));
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getComments(@PathVariable final String slug,
                                         @LoginUser final LoginInfo loginInfo) {
        log.info("getComments() : slug={}", slug);
        log.info("getComments() : loginInfo={}", loginInfo);

        Long articleId = commentGrpcClient.getArticleIdBySlug(slug);
        log.info("registerComment() : articleId={}", articleId);

        List<CommentResponse> comments = commentGrpcClient.getCommentsByArticleId(articleId, getLoginIdByLoginInfo(loginInfo));
        log.info("getComments() : comments={}", comments);

        return ResponseEntity.ok(new WrapCommentResponses(comments));
    }

//    @JwtTokenRequired
//    @DeleteMapping("/comments/{id}")
//    public ResponseEntity<?> deleteComment(@PathVariable final String slug,
//                                           @PathVariable final Long id,
//                                           @LoginUser final LoginInfo loginInfo) {
//        log.info("deleteComment() : slug={}", slug);
//        log.info("deleteComment() : comment id={}", id);
//        log.info("deleteComment() : loginInfo={}", loginInfo);
//
//        commentService.deleteComment(slug, id, loginInfo.getUserId());
//
//        return ResponseEntity.ok(id);
//    }
}
