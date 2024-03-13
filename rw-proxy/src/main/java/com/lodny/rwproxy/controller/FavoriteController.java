package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.grpc.article.GrpcGetArticleResponse;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.ArticleResponse;
import com.lodny.rwproxy.entity.wrapper.WrapArticleResponse;
import com.lodny.rwproxy.service.ArticleGrpcClient;
import com.lodny.rwproxy.service.FavoriteGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteController {

    private final FavoriteGrpcClient favoriteGrpcClient;
    private final ArticleGrpcClient articleGrpcClient;

    private String getToken(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getToken() : "";
    }

    private long getLoginUserId(final LoginInfo loginInfo) {
        return loginInfo == null ? -1L : loginInfo.getUserId();
    }

    @JwtTokenRequired
    @PostMapping("/articles/{slug}/favorite")
    public ResponseEntity<?> favorite(@PathVariable final String slug,
                                      @LoginUser final LoginInfo loginInfo) {
        log.info("favorite() : slug={}", slug);
        log.info("favorite() : loginInfo={}", loginInfo);

        GrpcGetArticleResponse grpcArticleResponse = favoriteGrpcClient.favorite(slug, getLoginUserId(loginInfo));
        log.info("favorite() : grpcArticleResponse={}", grpcArticleResponse);

        ArticleResponse articleResponse = articleGrpcClient.getArticleResponse(grpcArticleResponse, getLoginUserId(loginInfo));
        log.info("favorite() : articleResponse={}", articleResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapArticleResponse(articleResponse));
    }

    @JwtTokenRequired
    @DeleteMapping("/articles/{slug}/favorite")
    public ResponseEntity<?> unfavorite(@PathVariable final String slug,
                                        @LoginUser final LoginInfo loginInfo) {
        log.info("unfavorite() : slug={}", slug);
        log.info("unfavorite() : loginInfo={}", loginInfo);

        GrpcGetArticleResponse grpcArticleResponse = favoriteGrpcClient.unfavorite(slug, getLoginUserId(loginInfo));
        log.info("unfavorite() : grpcArticleResponse={}", grpcArticleResponse);

        ArticleResponse articleResponse = articleGrpcClient.getArticleResponse(grpcArticleResponse, getLoginUserId(loginInfo));
        log.info("unfavorite() : articleResponse={}", articleResponse);

        return ResponseEntity.ok(new WrapArticleResponse(articleResponse));
    }
}
