package com.lodny.rwfollow.service;

import com.lodny.rwcommon.properties.JwtProperty;
import com.lodny.rwfollow.entity.Follow;
import com.lodny.rwfollow.entity.FollowId;
import com.lodny.rwfollow.entity.wrapper.WrapProfileResponse;
import com.lodny.rwfollow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final RestTemplate restTemplate;
    private final JwtProperty jwtProperty;

    private Long getUserIdWithRestTemplate(final String username, final String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtProperty.getTokenTitle() + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Long> response = restTemplate.exchange(
//                FollowController.API_URL + "/users/" + username + "/id",
                "http://localhost:8080/api/users/" + username + "/id",
                HttpMethod.GET,
                entity,
                Long.class);

        return response.getBody();
    }

    private WrapProfileResponse getProfileResponseWithRestTemplate(final String username) {
        ResponseEntity<WrapProfileResponse> response = restTemplate.exchange(
//                FollowController.API_URL + "/profiles/" + username,
                "http://localhost:8080/api/profiles/" + username,
                HttpMethod.GET,
                new HttpEntity<String>(new HttpHeaders()),
                WrapProfileResponse.class);

        return response.getBody();
    }

    public WrapProfileResponse follow(final String username, final long followerId, final String token) {
        Long followeeId = getUserIdWithRestTemplate(username, token);
        log.info("follow() : followeeId={}", followeeId);

        followRepository.save(Follow.of(followeeId, followerId));

        WrapProfileResponse wrapProfileResponse = getProfileResponseWithRestTemplate(username);
        wrapProfileResponse.profile().setFollowing(true);

        return wrapProfileResponse;
    }

    public WrapProfileResponse unfollow(final String username, final long followerId, final String token) {
        Long followeeId = getUserIdWithRestTemplate(username, token);
        log.info("follow() : followeeId={}", followeeId);

        followRepository.deleteById(new FollowId(followeeId, followerId));

        WrapProfileResponse wrapProfileResponse = getProfileResponseWithRestTemplate(username);
        wrapProfileResponse.profile().setFollowing(false);

        return wrapProfileResponse;
    }

    public Boolean isFollow(final String username, final long followerId, final String token) {
        Long followeeId = getUserIdWithRestTemplate(username, token);
        log.info("isFollow() : followeeId={}", followeeId);

        return followRepository.findById(new FollowId(followeeId, followerId)) != null;
    }

    public List<Long> getFollowees(final long followerId) {
        return followRepository
                .findAllByIdFollowerId(followerId).stream()
                .map(followee -> followee.getId().getFolloweeId())
                .toList();

    }
}
