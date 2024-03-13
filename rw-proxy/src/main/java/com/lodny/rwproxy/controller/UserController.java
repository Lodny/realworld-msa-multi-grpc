package com.lodny.rwproxy.controller;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
import com.lodny.rwcommon.util.LoginInfo;
import com.lodny.rwproxy.entity.dto.LoginRequest;
import com.lodny.rwproxy.entity.dto.RegisterUserRequest;
import com.lodny.rwproxy.entity.dto.UpdateUserRequest;
import com.lodny.rwproxy.entity.dto.UserResponse;
import com.lodny.rwproxy.entity.wrapper.WrapLoginRequest;
import com.lodny.rwproxy.entity.wrapper.WrapRegisterUserRequest;
import com.lodny.rwproxy.entity.wrapper.WrapUpdateUserRequest;
import com.lodny.rwproxy.entity.wrapper.WrapUserResponse;
import com.lodny.rwproxy.service.UserGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserGrpcClient userGrpcClient;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody final WrapRegisterUserRequest wrapRegisterUserRequest) {
        RegisterUserRequest registerUserRequest = wrapRegisterUserRequest.user();
        log.info("registerUser() : registerUserRequest={}", registerUserRequest);

        UserResponse userResponse = userGrpcClient.registerUser(registerUserRequest);
        log.info("registerUser() : userResponse={}", userResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapUserResponse(userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final WrapLoginRequest wrapLoginRequest) {
        LoginRequest loginRequest = wrapLoginRequest.user();
        log.info("login() : loginRequest={}", loginRequest);

        UserResponse userResponse = userGrpcClient.login(loginRequest);
        log.info("login() : userResponse={}", userResponse);

        return ResponseEntity.ok(new WrapUserResponse(userResponse));
    }

    @JwtTokenRequired
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody final WrapUpdateUserRequest wrapUpdateUserRequest,
                                        @LoginUser final LoginInfo loginInfo) {
        UpdateUserRequest updateUserRequest = wrapUpdateUserRequest.user();
        log.info("updateUser() : updateUserRequest={}", updateUserRequest);

        UserResponse userResponse = userGrpcClient.updateUser(updateUserRequest, loginInfo.getUserId());
        log.info("registerUser() : userResponse={}", userResponse);

        return ResponseEntity.ok(new WrapUserResponse(userResponse));
    }
}
