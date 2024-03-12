package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
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

//    @JwtTokenRequired
//    @PostMapping("/profiles/{username}/follow")
//    public ResponseEntity<?> follow(@PathVariable final String username,
//                                    @LoginUser final LoginInfo loginInfo) {
//        log.info("follow() : username={}", username);
//        log.info("follow() : loginInfo={}", loginInfo);
//
//        WrapProfileResponse wrapProfileResponse = followService.follow(username, loginInfo.getUserId(), loginInfo.getToken());
//        log.info("follow() : wrapProfileResponse.profile()={}", wrapProfileResponse.profile());
//
//        return ResponseEntity.ok(wrapProfileResponse);
//    }

//    @JwtTokenRequired
//    @DeleteMapping("/profiles/{username}/follow")
//    public ResponseEntity<?> unfollow(@PathVariable final String username,
//                                      @LoginUser final LoginInfo loginInfo) {
//        log.info("unfollow() : username={}", username);
//        log.info("unfollow() : loginInfo={}", loginInfo);
//
//        WrapProfileResponse wrapProfileResponse = followService.unfollow(username, loginInfo.getUserId(), loginInfo.getToken());
//        log.info("unfollow() : wrapProfileResponse.profile()={}", wrapProfileResponse.profile());
//
//        return ResponseEntity.ok(wrapProfileResponse);
//    }
//
//    @JwtTokenRequired
//    @GetMapping("/profiles/{username}/follow")
//    public ResponseEntity<?> isFollow(@PathVariable final String username,
//                                      @LoginUser final LoginInfo loginInfo) {
//        log.info("isFollow() : username={}", username);
//        log.info("isFollow() : loginInfo={}", loginInfo);
//
//        Boolean following = followService.isFollow(username, loginInfo.getUserId(), loginInfo.getToken());
//        log.info("isFollow() : following={}", following);
//
//        return ResponseEntity.ok(following);
//    }
//
//    @JwtTokenRequired
//    @GetMapping("/follow/followee-list")
//    public ResponseEntity<?> getFollowees(@LoginUser final LoginInfo loginInfo) {
//        log.info("getFollowees() : loginInfo={}", loginInfo);
//
//        List<Long> followees = followService.getFollowees(loginInfo.getUserId());
//        log.info("getFollowees() : followees={}", followees);
//
//        return ResponseEntity.ok(followees);
//    }
}
