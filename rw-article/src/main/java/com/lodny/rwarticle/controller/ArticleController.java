package com.lodny.rwarticle.controller;

import com.lodny.rwarticle.entity.dto.ArticleParam;
import com.lodny.rwarticle.entity.dto.ArticleResponse;
import com.lodny.rwarticle.entity.dto.RegisterArticleRequest;
import com.lodny.rwarticle.entity.wrapper.WrapArticleResponse;
import com.lodny.rwarticle.entity.wrapper.WrapArticleResponses;
import com.lodny.rwarticle.entity.wrapper.WrapRegisterArticleRequest;
import com.lodny.rwarticle.service.ArticleService;
import com.lodny.rwarticle.service.GrpcClientService;
import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final GrpcClientService grpcClientService;

    @GetMapping("/grpc/test")
    public String testGrpc() {
        log.info("testGrpc() : ");
        return grpcClientService.sendMessage("juice");
    }

    private String getTokenByLoginInfo(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getToken() : "";
    }

    private static long getLoginUserId(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getUserId() : -1L;
    }

    @JwtTokenRequired
    @PostMapping
    public ResponseEntity<?> registerArticle(@RequestBody final WrapRegisterArticleRequest wrapRegisterArticleRequest,
                                             @LoginUser final LoginInfo loginInfo) {
        RegisterArticleRequest registerArticleRequest = wrapRegisterArticleRequest.article();
        log.info("registerArticle() : registerArticleRequest={}", registerArticleRequest);
        log.info("registerArticle() : loginInfo={}", loginInfo);

        ArticleResponse articleResponse = articleService.registerArticle(registerArticleRequest, loginInfo);
        log.info("registerArticle() : articleResponse={}", articleResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapArticleResponse(articleResponse));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getArticleBySlug(@PathVariable final String slug,
                                              @LoginUser final LoginInfo loginInfo) {
        log.info("getArticleBySlug() : slug={}", slug);
        log.info("getArticleBySlug() : loginInfo={}", loginInfo);

        ArticleResponse articleResponse = articleService.getArticleBySlug(slug, getTokenByLoginInfo(loginInfo));
        log.info("getArticleBySlug() : articleResponse={}", articleResponse);

        return ResponseEntity.ok(new WrapArticleResponse(articleResponse));
    }

    @JwtTokenRequired
    @GetMapping("/feed")
    public ResponseEntity<?> getFeedArticle(@ModelAttribute final ArticleParam articleParam,
                                            @LoginUser final LoginInfo loginInfo) {
        log.info("getFeedArticle() : articleParam={}", articleParam);
        log.info("getFeedArticle() : loginInfo={}", loginInfo);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getFeedArticle() : pageRequest={}", pageRequest);

        final Page<ArticleResponse> pageArticles = articleService.getFeedArticles(pageRequest, loginInfo.getToken(), loginInfo.getUserId());
        log.info("getFeedArticle() : pageArticles={}", pageArticles);

        return ResponseEntity.ok(new WrapArticleResponses(pageArticles));
    }

    @GetMapping
    public ResponseEntity<?> getArticles(@ModelAttribute final ArticleParam articleParam,
                                         @LoginUser final LoginInfo loginInfo) {
        log.info("getArticles() : articleParam={}", articleParam);

        PageRequest pageRequest = getPageRequest(articleParam);
        log.info("getArticles() : pageRequest={}", pageRequest);

        final var loginUserId = getLoginUserId(loginInfo);
        log.info("getArticles() : loginUserId={}", loginUserId);

        final var token = getTokenByLoginInfo(loginInfo);

        final Page<ArticleResponse> pageArticles =
                switch (articleParam.type()) {
                    case "tag"       -> articleService.getArticlesByTag(pageRequest, loginUserId, token, articleParam.tag());
                    case "author"    -> articleService.getArticlesByAuthor(pageRequest, loginUserId, token, articleParam.author());
                    case "favorited" -> articleService.getArticlesByFavorited(pageRequest, loginUserId, token, articleParam.favorited());
                    default          -> articleService.getArticles(pageRequest, loginUserId, token);
                };
        log.info("getArticles() : pageArticles={}", pageArticles);

        return ResponseEntity.ok(new WrapArticleResponses(pageArticles));
    }

    private static PageRequest getPageRequest(final ArticleParam articleParam) {
        int pageSize = articleParam.limit();
        int pageNo = articleParam.offset() / pageSize;

        return PageRequest.of(pageNo, pageSize);
    }

    @JwtTokenRequired
    @DeleteMapping("/{slug}")
    public ResponseEntity<Integer> deleteArticleBySlug(@PathVariable final String slug,
                                                       @LoginUser final LoginInfo loginInfo) {
        log.info("deleteArticleBySlug() : slug={}", slug);

        int count = articleService.deleteArticleBySlug(slug, loginInfo.getUserId(), loginInfo.getToken());
        log.info("deleteArticleBySlug() : count={}", count);

        return ResponseEntity.ok(1);
    }

    @GetMapping("/{slug}/id")
    public ResponseEntity<?> getArticleIdBySlug(@PathVariable final String slug) {
        log.info("getArticleIdBySlug() : slug={}", slug);

        Long articleId = articleService.getArticleIdBySlug(slug);
        log.info("getArticleIdBySlug() : articleId={}", articleId);

        return ResponseEntity.ok(articleId);
    }
}
