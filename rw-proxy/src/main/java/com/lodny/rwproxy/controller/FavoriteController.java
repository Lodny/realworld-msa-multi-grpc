package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.service.FavoriteGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteController {

    private final FavoriteGrpcClient favoriteGrpcClient;

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

//        WrapArticleResponse wrapArticleResponse = favoriteService.favorite(slug, getLoginUserId(loginInfo), getToken(loginInfo));
//        log.info("favorite() : wrapArticleResponse.article()={}", wrapArticleResponse.article());

//        return ResponseEntity.status(HttpStatus.CREATED).body(wrapArticleResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

//    @JwtTokenRequired
//    @DeleteMapping("/articles/{slug}/favorite")
//    public ResponseEntity<?> unfavorite(@PathVariable final String slug,
//                                        @LoginUser final LoginInfo loginInfo) {
//        log.info("unfavorite() : slug={}", slug);
//        log.info("unfavorite() : loginInfo={}", loginInfo);
//
//        WrapArticleResponse wrapArticleResponse = favoriteService.unfavorite(slug, getLoginUserId(loginInfo), getToken(loginInfo));
//        log.info("unfavorite() : wrapArticleResponse.article()={}", wrapArticleResponse.article());
//
//        return ResponseEntity.ok(wrapArticleResponse);
//    }

}
