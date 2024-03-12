package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.ProfileResponse;
import com.lodny.rwproxy.entity.wrapper.WrapProfileResponse;
import com.lodny.rwproxy.service.ProfileGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileGrpcClient profileGrpcClient;

    private Long getLoginIdByLoginInfo(final LoginInfo loginInfo) {
        return loginInfo != null ? loginInfo.getUserId() : -1L;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getProfile(@PathVariable final String username,
                                        @LoginUser final LoginInfo loginInfo) {
        log.info("getProfile() : username={}", username);
        log.info("getProfile() : loginInfo={}", loginInfo);

        ProfileResponse profileResponse = profileGrpcClient.getProfileByUsername(username, getLoginIdByLoginInfo(loginInfo));
//        profileResponse.setImage(ImageUtil.nullToDefaultImage(profileResponse.getImage()));
        log.info("getProfile() : profileResponse={}", profileResponse);

        return ResponseEntity.ok(new WrapProfileResponse(profileResponse));
    }
}
