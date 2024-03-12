package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import com.lodny.rwproxy.entity.wrapper.WrapProfileResponse;
import com.lodny.rwproxy.service.FollowGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

    private final FollowGrpcClient followGrpcClient;

//    private static final String API_URL = "http://localhost:8080/api";

    @JwtTokenRequired
    @PostMapping("/profiles/{username}/follow")
    public ResponseEntity<?> follow(@PathVariable final String username,
                                    @LoginUser final LoginInfo loginInfo) {
        log.info("follow() : username={}", username);
        log.info("follow() : loginInfo={}", loginInfo);

        ProfileResponse profileResponse = followGrpcClient.follow(username, loginInfo.getUserId());
        log.info("follow() : profileResponse={}", profileResponse);

        return ResponseEntity.ok(new WrapProfileResponse(profileResponse));
    }

    @JwtTokenRequired
    @DeleteMapping("/profiles/{username}/follow")
    public ResponseEntity<?> unfollow(@PathVariable final String username,
                                      @LoginUser final LoginInfo loginInfo) {
        log.info("unfollow() : username={}", username);
        log.info("unfollow() : loginInfo={}", loginInfo);

        ProfileResponse profileResponse = followGrpcClient.unfollow(username, loginInfo.getUserId());
        log.info("unfollow() : profileResponse={}", profileResponse);

        return ResponseEntity.ok(new WrapProfileResponse(profileResponse));
    }
}
