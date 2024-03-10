package com.lodny.rwfavorite.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwfavorite.entity.wrapper.WrapArticleResponse;
import com.lodny.rwfavorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavoriteController {

    private final FavoriteService favoriteService;

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

        WrapArticleResponse wrapArticleResponse = favoriteService.favorite(slug, getLoginUserId(loginInfo), getToken(loginInfo));
        log.info("favorite() : wrapArticleResponse.article()={}", wrapArticleResponse.article());

        return ResponseEntity.status(HttpStatus.CREATED).body(wrapArticleResponse);
    }

    @JwtTokenRequired
    @DeleteMapping("/articles/{slug}/favorite")
    public ResponseEntity<?> unfavorite(@PathVariable final String slug,
                                        @LoginUser final LoginInfo loginInfo) {
        log.info("unfavorite() : slug={}", slug);
        log.info("unfavorite() : loginInfo={}", loginInfo);

        WrapArticleResponse wrapArticleResponse = favoriteService.unfavorite(slug, getLoginUserId(loginInfo), getToken(loginInfo));
        log.info("unfavorite() : wrapArticleResponse.article()={}", wrapArticleResponse.article());

        return ResponseEntity.ok(wrapArticleResponse);
    }

    @GetMapping("/articles/{articleId}/favorite-info")
    public ResponseEntity<?> favoriteInfo(@PathVariable final Long articleId,
                                          @LoginUser final LoginInfo loginInfo) {
        log.info("favoriteInfo() : articleId={}", articleId);
        log.info("favoriteInfo() : loginInfo={}", loginInfo);

        Long[] favoriteInfo = favoriteService.favoriteInfo(articleId, getLoginUserId(loginInfo));
        log.info("favoriteInfo() : favoriteInfo={}", favoriteInfo);

        return ResponseEntity.ok(favoriteInfo);
    }

    @GetMapping("/favorite/{userId}/article-ids")
    public ResponseEntity<?> getArticleIdsByUserid(@PathVariable final Long userId,
                                                   @LoginUser final LoginInfo loginInfo) {
        log.info("getArticleIdsByUserid() : userId={}", userId);
        log.info("favoriteInfo() : loginInfo={}", loginInfo);

        List<Long> articleIds = favoriteService.getArticleIdsByUserid(userId);
        log.info("getArticleIdsByUserid() : articleIds={}", articleIds);

        return ResponseEntity.ok(articleIds);
    }
}
