package com.lodny.rwuser.service;

import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.entity.dto.ProfileResponse;
import com.lodny.rwuser.entity.dto.UserResponse;
import com.lodny.rwuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public ProfileResponse getProfile(final String username, final UserResponse loginUser) {
        RealWorldUser foundUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfile() : foundUser={}", foundUser);

        Boolean following = getFollowingWithRestTemplate(username, loginUser);
        log.info("getProfile() : following={}", following);

        return ProfileResponse.of(foundUser, following);
    }

    public ProfileResponse getProfileById(final Long userId, final UserResponse loginUser) {
        RealWorldUser foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        log.info("getProfileById() : foundUser={}", foundUser);

        Boolean following = getFollowingWithRestTemplate(foundUser.getUsername(), loginUser);
        log.info("getProfileById() : following={}", following);

        return ProfileResponse.of(foundUser, following);
    }

    private Boolean getFollowingWithRestTemplate(final String username, final UserResponse loginUser) {
        //todo::WebClient
        if (loginUser == null)
            return false;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Token " + loginUser.token());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://localhost:8080/api/profiles/" + username + "/follow",
                HttpMethod.GET,
                entity,
                Boolean.class);

        return response.getBody();
    }
}
