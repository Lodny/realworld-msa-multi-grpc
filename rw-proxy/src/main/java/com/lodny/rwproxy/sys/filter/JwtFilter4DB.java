package com.lodny.rwproxy.sys.filter;

import com.lodny.rwcommon.util.JwtUtil;
import com.lodny.rwcommon.util.LoginInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter4DB extends OncePerRequestFilter {

//    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtFilter4DB::doFilterInternal() : 1={}", 1);

        LoginInfo loginInfo = JwtUtil.getAuthenticatedUser();
        if (loginInfo == null) {
            filterChain.doFilter(request, response);
            return;
        }

//        RealWorldUser foundUser = userRepository.findByEmail(loginInfo.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("user not found"));
//        log.info("[F] doFilterInternal() : foundUser={}", foundUser);
//
//        loginInfo.setUserResponse(UserResponse.of(foundUser, loginInfo.getToken()));

        filterChain.doFilter(request, response);
    }
}
