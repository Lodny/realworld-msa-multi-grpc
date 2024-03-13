package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.ArticleParam;
import com.lodny.rwproxy.entity.dto.ArticleResponse;
import com.lodny.rwproxy.entity.dto.RegisterArticleRequest;
import com.lodny.rwproxy.entity.wrapper.WrapArticleResponse;
import com.lodny.rwproxy.entity.wrapper.WrapArticleResponses;
import com.lodny.rwproxy.entity.wrapper.WrapRegisterArticleRequest;
import com.lodny.rwproxy.service.ArticleGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleGrpcClient articleGrpcClient;

    private String getTokenByLoginInfo(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getToken() : "";
    }

    private long getLoginUserId(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getUserId() : -1L;
    }


    @JwtTokenRequired
    @PostMapping
    public ResponseEntity<?> registerArticle(@RequestBody final WrapRegisterArticleRequest wrapRegisterArticleRequest,
                                             @LoginUser final LoginInfo loginInfo) {
        RegisterArticleRequest registerArticleRequest = wrapRegisterArticleRequest.article();
        log.info("registerArticle() : registerArticleRequest={}", registerArticleRequest);
        log.info("registerArticle() : loginInfo={}", loginInfo);

        ArticleResponse articleResponse = articleGrpcClient.registerArticle(registerArticleRequest, getLoginUserId(loginInfo));
        log.info("registerArticle() : articleResponse={}", articleResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapArticleResponse(articleResponse));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getArticleBySlug(@PathVariable final String slug,
                                              @LoginUser final LoginInfo loginInfo) {
        log.info("getArticleBySlug() : slug={}", slug);
        log.info("getArticleBySlug() : loginInfo={}", loginInfo);

        ArticleResponse articleResponse = articleGrpcClient.getArticleBySlug(slug, getLoginUserId(loginInfo));
        log.info("getArticleBySlug() : articleResponse={}", articleResponse);

        return ResponseEntity.ok(new WrapArticleResponse(articleResponse));
    }

    @JwtTokenRequired
    @GetMapping("/feed")
    public ResponseEntity<?> getFeedArticle(@ModelAttribute final ArticleParam articleParam,
                                            @LoginUser final LoginInfo loginInfo) {
        log.info("getFeedArticle() : articleParam={}", articleParam);
        log.info("getFeedArticle() : loginInfo={}", loginInfo);

        final var loginUserId = getLoginUserId(loginInfo);
        log.info("getArticles() : loginUserId={}", loginUserId);

        final Page<ArticleResponse> pageArticles = articleGrpcClient.getFeedArticlesByLoginUser(articleParam, loginUserId);
        log.info("getFeedArticle() : pageArticles={}", pageArticles);

        return ResponseEntity.ok(new WrapArticleResponses(pageArticles));
    }

    @GetMapping
    public ResponseEntity<?> getArticles(@ModelAttribute final ArticleParam articleParam,
                                         @LoginUser final LoginInfo loginInfo) {
        log.info("getArticles() : articleParam={}", articleParam);

        final var loginUserId = getLoginUserId(loginInfo);
        log.info("getArticles() : loginUserId={}", loginUserId);

        final Page<ArticleResponse> pageArticles =
                switch (articleParam.type()) {
                    case "tag"       -> articleGrpcClient.getArticlesByTag(articleParam, loginUserId, articleParam.tag());
                    case "author"    -> articleGrpcClient.getArticlesByAuthor(articleParam, loginUserId, articleParam.author());
                    case "favorited" -> articleGrpcClient.getArticlesByFavorited(articleParam, loginUserId, articleParam.favorited());
                    default          -> articleGrpcClient.getArticles(articleParam, loginUserId);
                };
        log.info("getArticles() : pageArticles={}", pageArticles);

        return ResponseEntity.ok(new WrapArticleResponses(pageArticles));
    }

    @JwtTokenRequired
    @DeleteMapping("/{slug}")
    public ResponseEntity<Integer> deleteArticleBySlug(@PathVariable final String slug,
                                                       @LoginUser final LoginInfo loginInfo) {
        log.info("deleteArticleBySlug() : slug={}", slug);
        articleGrpcClient.deleteArticleBySlug(slug, loginInfo.getUserId());

        return ResponseEntity.ok(1);
    }
}
