package com.lodny.rwuser.controller;

import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.entity.dto.LoginRequest;
import com.lodny.rwuser.entity.dto.RegisterUserRequest;
import com.lodny.rwuser.entity.dto.UpdateUserRequest;
import com.lodny.rwuser.entity.dto.UserResponse;
import com.lodny.rwuser.entity.wrapper.WrapLoginRequest;
import com.lodny.rwuser.entity.wrapper.WrapRegisterUserRequest;
import com.lodny.rwuser.entity.wrapper.WrapUpdateUserRequest;
import com.lodny.rwuser.entity.wrapper.WrapUserResponse;
import com.lodny.rwuser.service.UserService;
import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.annotation.LoginUser;
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

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody final WrapRegisterUserRequest wrapRegisterUserRequest) {
        RegisterUserRequest registerUserRequest = wrapRegisterUserRequest.user();
        log.info("registerUser() : registerUserRequest={}", registerUserRequest);

        UserResponse userResponse = userService.registerUser(registerUserRequest);
        log.info("registerUser() : userResponse={}", userResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapUserResponse(userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final WrapLoginRequest wrapLoginRequest) {
        LoginRequest loginRequest = wrapLoginRequest.user();
        log.info("login() : loginRequest={}", loginRequest);

        UserResponse userResponse = userService.login(loginRequest);
        log.info("login() : userResponse={}", userResponse);

        return ResponseEntity.ok(new WrapUserResponse(userResponse));
    }

    @JwtTokenRequired
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody final WrapUpdateUserRequest wrapUpdateUserRequest,
                                        @LoginUser final UserResponse loginUser) {
        UpdateUserRequest updateUserRequest = wrapUpdateUserRequest.user();
        log.info("updateUser() : updateUserRequest={}", updateUserRequest);

        UserResponse userResponse = userService.updateUser(updateUserRequest, loginUser);
        log.info("registerUser() : userResponse={}", userResponse);

        return ResponseEntity.ok(new WrapUserResponse(userResponse));
    }

    @GetMapping("/{username}/id")
    public ResponseEntity<?> getUserId(@PathVariable() String username) {
        log.info("getUserId() : username={}", username);

        RealWorldUser foundUser = userService.getUserByUsername(username);
        log.info("getUserId() : foundUser={}", foundUser);

        return ResponseEntity.ok(foundUser.getId());
    }
}
