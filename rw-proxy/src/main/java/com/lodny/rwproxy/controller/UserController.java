package com.lodny.rwproxy.controller;

import com.lodny.rwproxy.entity.dto.LoginRequest;
import com.lodny.rwproxy.entity.dto.UserResponse;
import com.lodny.rwproxy.entity.wrapper.WrapLoginRequest;
import com.lodny.rwproxy.entity.wrapper.WrapUserResponse;
import com.lodny.rwproxy.service.UserGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserGrpcClient userGrpcClient;
//    private final UserService userService;
//
//    @PostMapping
//    public ResponseEntity<?> registerUser(@RequestBody final WrapRegisterUserRequest wrapRegisterUserRequest) {
//        RegisterUserRequest registerUserRequest = wrapRegisterUserRequest.user();
//        log.info("registerUser() : registerUserRequest={}", registerUserRequest);
//
//        UserResponse userResponse = userService.registerUser(registerUserRequest);
//        log.info("registerUser() : userResponse={}", userResponse);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(new WrapUserResponse(userResponse));
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final WrapLoginRequest wrapLoginRequest) {
        LoginRequest loginRequest = wrapLoginRequest.user();
        log.info("login() : loginRequest={}", loginRequest);

        UserResponse userResponse = userGrpcClient.login(loginRequest);
        log.info("login() : userResponse={}", userResponse);

        return ResponseEntity.ok(new WrapUserResponse(userResponse));
    }

//    @JwtTokenRequired
//    @PutMapping
//    public ResponseEntity<?> updateUser(@RequestBody final WrapUpdateUserRequest wrapUpdateUserRequest,
//                                        @LoginUser final UserResponse loginUser) {
//        UpdateUserRequest updateUserRequest = wrapUpdateUserRequest.user();
//        log.info("updateUser() : updateUserRequest={}", updateUserRequest);
//
//        UserResponse userResponse = userService.updateUser(updateUserRequest, loginUser);
//        log.info("registerUser() : userResponse={}", userResponse);
//
//        return ResponseEntity.ok(new WrapUserResponse(userResponse));
//    }
//
//    @GetMapping("/{username}/id")
//    public ResponseEntity<?> getUserId(@PathVariable() String username) {
//        log.info("getUserId() : username={}", username);
//
//        RealWorldUser foundUser = userService.getUserByUsername(username);
//        log.info("getUserId() : foundUser={}", foundUser);
//
//        return ResponseEntity.ok(foundUser.getId());
//    }
}
