package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.ArticleParam;
import com.lodny.rwproxy.entity.dto.ArticleResponse;
import com.lodny.rwproxy.entity.wrapper.WrapArticleResponses;
import com.lodny.rwproxy.service.ArticleGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleGrpcClient articleGrpcClient;

    private String getTokenByLoginInfo(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getToken() : "";
    }

    private static long getLoginUserId(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getUserId() : -1L;
    }

//    @JwtTokenRequired
//    @PostMapping
//    public ResponseEntity<?> registerArticle(@RequestBody final WrapRegisterArticleRequest wrapRegisterArticleRequest,
//                                             @LoginUser final LoginInfo loginInfo) {
//        RegisterArticleRequest registerArticleRequest = wrapRegisterArticleRequest.article();
//        log.info("registerArticle() : registerArticleRequest={}", registerArticleRequest);
//        log.info("registerArticle() : loginInfo={}", loginInfo);
//
//        ArticleResponse articleResponse = articleService.registerArticle(registerArticleRequest, loginInfo);
//        log.info("registerArticle() : articleResponse={}", articleResponse);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapArticleResponse(articleResponse));
//    }
//
//    @GetMapping("/{slug}")
//    public ResponseEntity<?> getArticleBySlug(@PathVariable final String slug,
//                                              @LoginUser final LoginInfo loginInfo) {
//        log.info("getArticleBySlug() : slug={}", slug);
//        log.info("getArticleBySlug() : loginInfo={}", loginInfo);
//
//        ArticleResponse articleResponse = articleService.getArticleBySlug(slug, getTokenByLoginInfo(loginInfo), getLoginUserId(loginInfo));
//        log.info("getArticleBySlug() : articleResponse={}", articleResponse);
//
//        return ResponseEntity.ok(new WrapArticleResponse(articleResponse));
//    }
//
//    @JwtTokenRequired
//    @GetMapping("/feed")
//    public ResponseEntity<?> getFeedArticle(@ModelAttribute final ArticleParam articleParam,
//                                            @LoginUser final LoginInfo loginInfo) {
//        log.info("getFeedArticle() : articleParam={}", articleParam);
//        log.info("getFeedArticle() : loginInfo={}", loginInfo);
//
//        PageRequest pageRequest = getPageRequest(articleParam);
//        log.info("getFeedArticle() : pageRequest={}", pageRequest);
//
//        final Page<ArticleResponse> pageArticles = articleService.getFeedArticles(pageRequest, loginInfo.getToken(), loginInfo.getUserId());
//        log.info("getFeedArticle() : pageArticles={}", pageArticles);
//
//        return ResponseEntity.ok(new WrapArticleResponses(pageArticles));
//    }

    @GetMapping
    public ResponseEntity<?> getArticles(@ModelAttribute final ArticleParam articleParam,
                                         @LoginUser final LoginInfo loginInfo) {
        log.info("getArticles() : articleParam={}", articleParam);

//        PageRequest pageRequest = getPageRequest(articleParam);
//        log.info("getArticles() : pageRequest={}", pageRequest);

        final var loginUserId = getLoginUserId(loginInfo);
        log.info("getArticles() : loginUserId={}", loginUserId);

        final var token = getTokenByLoginInfo(loginInfo);

        final Page<ArticleResponse> pageArticles =
                switch (articleParam.type()) {
//                    case "tag"       -> articleService.getArticlesByTag(pageRequest, loginUserId, token, articleParam.tag());
//                    case "author"    -> articleService.getArticlesByAuthor(pageRequest, loginUserId, token, articleParam.author());
//                    case "favorited" -> articleService.getArticlesByFavorited(pageRequest, loginUserId, token, articleParam.favorited());
                    default          -> articleGrpcClient.getArticles(articleParam, loginUserId, token);
//                    default          -> articleService.getArticles(pageRequest, loginUserId, token);
                };
        log.info("getArticles() : pageArticles={}", pageArticles);

        return ResponseEntity.ok(new WrapArticleResponses(pageArticles));
    }

//    @JwtTokenRequired
//    @DeleteMapping("/{slug}")
//    public ResponseEntity<Integer> deleteArticleBySlug(@PathVariable final String slug,
//                                                       @LoginUser final LoginInfo loginInfo) {
//        log.info("deleteArticleBySlug() : slug={}", slug);
//
//        int count = articleService.deleteArticleBySlug(slug, loginInfo.getUserId(), loginInfo.getToken());
//        log.info("deleteArticleBySlug() : count={}", count);
//
//        return ResponseEntity.ok(1);
//    }
//
//    @GetMapping("/{slug}/id")
//    public ResponseEntity<?> getArticleIdBySlug(@PathVariable final String slug) {
//        log.info("getArticleIdBySlug() : slug={}", slug);
//
//        Long articleId = articleService.getArticleIdBySlug(slug);
//        log.info("getArticleIdBySlug() : articleId={}", articleId);
//
//        return ResponseEntity.ok(articleId);
//    }
}
