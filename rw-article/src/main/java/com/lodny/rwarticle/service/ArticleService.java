package com.lodny.rwarticle.service;

import com.lodny.rwarticle.entity.Article;
import com.lodny.rwarticle.entity.dto.ArticleResponse;
import com.lodny.rwarticle.entity.dto.ProfileResponse;
import com.lodny.rwarticle.entity.dto.RegisterArticleRequest;
import com.lodny.rwarticle.repository.ArticleRepository;
import com.lodny.rwcommon.properties.JwtProperty;
import com.lodny.rwcommon.util.LoginInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final RestTemplate restTemplate;
    private final JwtProperty jwtProperty;

    private final TagGrpcClient tagGrpcClient;
    private final ProfileGrpcClient profileGrpcClient;
    private final FollowGrpcClient followGrpcClient;
    private final FavoriteGrpcClient favoriteGrpcClient;


    private HttpHeaders getHttpHeadersByToken(final String token) {
        log.info("getHttpHeadersByToken() : token={}", token);

        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(token)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", jwtProperty.getTokenTitle() + token);
        }

        return headers;
    }

    @Transactional
    public ArticleResponse registerArticle(final RegisterArticleRequest registerArticleRequest,
                                           final LoginInfo loginInfo) {
        Article article = Article.of(registerArticleRequest, loginInfo.getUserId());
        log.info("registerArticle() : article={}", article);

        Article savedArticle = articleRepository.save(article);
        log.info("registerArticle() : savedArticle={}", savedArticle);

        tagGrpcClient.registerTags(registerArticleRequest.tagList(), savedArticle.getId(), loginInfo.getToken());

        ProfileResponse profileResponse = profileGrpcClient.getProfileByUserId(article.getAuthorId(), loginInfo.getUserId());
        log.info("registerArticle() : profileResponse={}", profileResponse);

        return ArticleResponse.of(
                savedArticle,
                registerArticleRequest.tagList(),
                profileResponse,
                new Long[]{0L, 0L});
    }

    public Page<ArticleResponse> getArticles(final PageRequest pageRequest,
                                             final Long loginUserId,
                                             final String token) {
        Page<Article> articlePage = articleRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        log.info("getArticles() : articlePage={}", articlePage);

        return getArticleResponses(articlePage, loginUserId, token);
    }

    public Page<ArticleResponse> getArticlesByTag(final PageRequest pageRequest,
                                                  final Long loginUserId,
                                                  final String token,
                                                  final String tag) {
        List<Long> articleIds = tagGrpcClient.getArticleIdsByTagString(tag);
        log.info("getArticlesByTag() : articleIds={}", articleIds);
        //todo::orderby

        Page<Article> articlePage = articleRepository.findByIdInOrderByCreatedAtDesc(articleIds, pageRequest);
        log.info("getArticlesByTag() : articlePage={}", articlePage);

        return getArticleResponses(articlePage, loginUserId, token);
    }

    public Page<ArticleResponse> getArticlesByAuthor(final PageRequest pageRequest,
                                                     final Long loginUserId,
                                                     final String token,
                                                     final String author) {
        log.info("getArticlesByAuthor() : author={}", author);

        final Long authorId = getUserIdByUsernameWithRestTemplate(author, token);
        log.info("getArticlesByAuthor() : authorId={}", authorId);

        Page<Article> articlePage = articleRepository.findAllByAuthorIdOrderByCreatedAtDesc(authorId, pageRequest);
        log.info("getArticlesByAuthor() : articlePage={}", articlePage);

        return getArticleResponses(articlePage, loginUserId, token);
    }

    private Long getUserIdByUsernameWithRestTemplate(final String username, final String token) {
        ResponseEntity<Long> response = restTemplate.exchange(
                "http://localhost:8080/api/users/" + username + "/id",
                HttpMethod.GET,
                new HttpEntity<String>(getHttpHeadersByToken(token)),
                Long.class);

        return response.getBody();
    }

    public Page<ArticleResponse> getArticlesByFavorited(final PageRequest pageRequest,
                                                        final Long loginUserId,
                                                        final String token,
                                                        final String favoriteUser) {
        log.info("getArticlesByFavorited() : favoriteUser={}", favoriteUser);

        final Long favoriteUserId = getUserIdByUsernameWithRestTemplate(favoriteUser, token);
        log.info("getArticlesByFavorited() : favoriteUserId={}", favoriteUserId);

        List<Long> articleIds = favoriteGrpcClient.getFavoriteArticleIdsByUserId(favoriteUserId);
        log.info("getArticlesByFavorited() : articleIds={}", articleIds);

        Page<Article> articlePage = articleRepository.findAllByIdInOrderByCreatedAtDesc(articleIds, pageRequest);
        log.info("getArticlesByFavorited() : articlePage={}", articlePage);

        return getArticleResponses(articlePage, loginUserId, token);
    }

    public Page<ArticleResponse> getFeedArticles(final PageRequest pageRequest, final String token, final Long loginUserId) {
        log.info("getFeedArticles() : loginUserId={}", loginUserId);

        List<Long> followeeIds = followGrpcClient.getFolloweeIdsByFollowerId(loginUserId);
        log.info("getFeedArticles() : followeeIds={}", followeeIds);
        //todo::orderby

        Page<Article> articlePage = articleRepository.findByAuthorIdInOrderByCreatedAtDesc(followeeIds, pageRequest);
        log.info("getArticlesByTag() : articlePage={}", articlePage);

        return getArticleResponses(articlePage, loginUserId, token);
    }

    private Page<ArticleResponse> getArticleResponses(final Page<Article> articlePage, final Long loginUserId, final String token) {
        List<ArticleResponse> articleResponses = articlePage.getContent().stream()
                .map(article -> {
                    Set<String> tags = tagGrpcClient.getTagStringsByArticleId(article.getId());
                    ProfileResponse profileResponse = profileGrpcClient.getProfileByUserId(article.getAuthorId(), loginUserId);
                    Long[] favoriteInfo = favoriteGrpcClient.getFavoriteInfoByArticleId(article.getId(), loginUserId);
                    return ArticleResponse.of(article, tags, profileResponse, favoriteInfo);
                })
                .toList();

        log.info("getArticleResponses() : articleResponses={}", articleResponses);

        return new PageImpl<>(articleResponses, articlePage.getPageable(), articlePage.getTotalElements());
    }

    public Long getArticleIdBySlug(final String slug) {
        Article foundArticle = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleIdBySlug() : foundArticle={}", foundArticle);

        return foundArticle.getId();
    }

    @Transactional
    public ArticleResponse getArticleBySlug(final String slug, final String token, final long loginUserId) {
        log.info("getArticleBySlug() : token={}", token);
        Article foundArticle = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("article not found"));
        log.info("getArticleBySlug() : foundArticle={}", foundArticle);

        Set<String> tags = tagGrpcClient.getTagStringsByArticleId(foundArticle.getId());
        log.info("getArticleBySlug() : tags={}", tags);
        ProfileResponse profileResponse = profileGrpcClient.getProfileByUserId(foundArticle.getAuthorId(), loginUserId);
        log.info("getArticleBySlug() : profileResponse={}", profileResponse);
        Long[] favoriteInfo = favoriteGrpcClient.getFavoriteInfoByArticleId(foundArticle.getId(), loginUserId);
        log.info("getArticleBySlug() : favoriteInfo={}", favoriteInfo);

        return ArticleResponse.of(foundArticle, tags, profileResponse, favoriteInfo);
    }

    @Transactional
    public int deleteArticleBySlug(final String slug, final Long loginUserId, final String token) {
        log.info("deleteArticleBySlug() : loginUserId={}", loginUserId);

        Article foundArticle = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("article not found"));

        //todo::delete tags and comments ???
        if (! foundArticle.getAuthorId().equals(loginUserId))
            throw new IllegalArgumentException("The author is different from the logged-in user.");

        List<Long> deletedTagIds = deleteTagsByArticleIdWithRestTemplate(foundArticle.getId(), token);
        log.info("deleteArticleBySlug() : deletedTagIds={}", deletedTagIds);

//        throw new IllegalArgumentException("throw error");
        articleRepository.delete(foundArticle);

        return 1;
    }

    private List<Long> deleteTagsByArticleIdWithRestTemplate(final Long articleId, final String token) {
        log.info("deleteTagsByArticleIdWithRestTemplate() : articleId={}", articleId);

        ResponseEntity<List> response = restTemplate.exchange(
                "http://localhost:8080/api/tags/" + articleId,
                HttpMethod.DELETE,
                new HttpEntity<String>(getHttpHeadersByToken(token)),
                List.class);

        return response.getBody();
    }
}
