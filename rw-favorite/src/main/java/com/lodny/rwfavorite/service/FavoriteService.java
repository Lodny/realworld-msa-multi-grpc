package com.lodny.rwfavorite.service;

import com.lodny.rwcommon.properties.JwtProperty;
import com.lodny.rwfavorite.entity.Favorite;
import com.lodny.rwfavorite.entity.FavoriteId;
import com.lodny.rwfavorite.entity.wrapper.WrapArticleResponse;
import com.lodny.rwfavorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestTemplate restTemplate;
    private final JwtProperty jwtProperty;

    private Long getArticleIdBySlugWithRestTemplate(final String slug) {  //}, final String token) {
        ResponseEntity<Long> response = restTemplate.exchange(
//                FollowController.API_URL + "/users/" + username + "/id",
                "http://localhost:8080/api/articles/" + slug + "/id",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Long.class);

        return response.getBody();
    }

    private WrapArticleResponse getArticleResponseWithRestTemplate(final String slug,
                                                                   final String token) {
        log.info("getArticleResponseWithRestTemplate() : slug={}", slug);

        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(token))
            headers.set("Authorization", jwtProperty.getTokenTitle() + token);

        ResponseEntity<WrapArticleResponse> response = restTemplate.exchange(
                "http://localhost:8080/api/articles/" + slug,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                WrapArticleResponse.class);

        return response.getBody();
    }

    public WrapArticleResponse favorite(final String slug, final long loginUserId, final String token) {
        log.info("favorite() : loginUserId={}", loginUserId);
        log.info("favorite() : token={}", token);

        final var articleId = getArticleIdBySlugWithRestTemplate(slug);
        favoriteRepository.save(Favorite.of(articleId, loginUserId));
        return getArticleResponseWithRestTemplate(slug, token);
    }

    public WrapArticleResponse unfavorite(final String slug, final long loginUserId, final String token) {
        log.info("unfavorite() : loginUserId={}", loginUserId);
        log.info("unfavorite() : token={}", token);

        final var articleId = getArticleIdBySlugWithRestTemplate(slug);
        favoriteRepository.deleteById(new FavoriteId(articleId, loginUserId));
        return getArticleResponseWithRestTemplate(slug, token);
    }

    public Long[] favoriteInfo(final Long articleId, final long loginUserId) {
        Long favoritesCount = favoriteRepository.countByIdArticleId(articleId);
        log.info("favoriteInfo() : favoritesCount={}", favoritesCount);

        Favorite favorite = favoriteRepository.findById(new FavoriteId(articleId, loginUserId));
        log.info("addFavorite() : favorite={}", favorite);

        return new Long[]{favoritesCount, favorite == null ? 0L : 1L};
    }

    public List<Long> getArticleIdsByUserid(final Long userId) {
        log.info("getArticleIdsByUserid() : userId={}", userId);

        return favoriteRepository
                .findAllByIdUserId(userId).stream()
                .map(favorite -> favorite.getId().getArticleId())
                .toList();
    }
}
