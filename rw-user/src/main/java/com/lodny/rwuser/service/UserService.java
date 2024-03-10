package com.lodny.rwuser.service;

import com.lodny.rwcommon.exception.RealException;
import com.lodny.rwcommon.util.JwtUtil;
import com.lodny.rwuser.entity.RealWorldUser;
import com.lodny.rwuser.entity.dto.LoginRequest;
import com.lodny.rwuser.entity.dto.RegisterUserRequest;
import com.lodny.rwuser.entity.dto.UpdateUserRequest;
import com.lodny.rwuser.entity.dto.UserResponse;
import com.lodny.rwuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserResponse registerUser(final RegisterUserRequest registerUserRequest) {
        RealWorldUser newUser = RealWorldUser.of(registerUserRequest);
        RealWorldUser savedUser = userRepository.save(newUser);
        log.info("registerUser() : savedUser={}", savedUser);

        String token = jwtUtil.createToken(savedUser.getEmail(), savedUser.getId());
        log.info("registerUser() : token={}", token);

        return UserResponse.of(savedUser, token);
    }

    public UserResponse login(final LoginRequest loginRequest) {
        final String errorMsg = "email or password is invalid. please check.";

        RealWorldUser foundUser = userRepository.findByEmail(loginRequest.email())
                        .orElseThrow(() -> new RealException(errorMsg));
        log.info("login() : foundUser={}", foundUser);

        if (! loginRequest.password().equals(foundUser.getPassword()))
            throw new RealException(errorMsg);

        String token = jwtUtil.createToken(foundUser.getEmail(), foundUser.getId());
        log.info("login() : token={}", token);

        return UserResponse.of(foundUser, token);
    }

    public UserResponse updateUser(final UpdateUserRequest updateUserRequest, final UserResponse loginUser) {
        log.info("updateUser() : loginUser={}", loginUser);

        final RealWorldUser user = loginUser.user();
        user.update(updateUserRequest);
        RealWorldUser savedUser = userRepository.save(user);
        log.info("updateUser() : savedUser={}", savedUser);

        return UserResponse.of(savedUser, loginUser.token());
    }

    public RealWorldUser getUserByUsername(final String username) {
        final String errorMsg = "email or password is invalid. please check.";

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RealException(errorMsg));
    }

    public RealWorldUser getUserByEmail(final String email) {
        final String errorMsg = "email or password is invalid. please check.";

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RealException(errorMsg));
    }
}
