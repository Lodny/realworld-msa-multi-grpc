package com.lodny.rwcommon.filter;

import com.lodny.rwcommon.properties.JwtProperty;
import com.lodny.rwcommon.util.JwtUtil;
import com.lodny.rwcommon.util.LoginInfo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProperty jwtProperty;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(jwtProperty.getHeader());
        log.info("[F] doFilterInternal() : auth={}", auth);
        if (auth == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = request.getSession().getId();
        String token = jwtUtil.getToken(auth);
        if (token == null)
            return;
        log.info("doFilterInternal() : token={}", token);

        Claims claims = jwtUtil.getClaimsByToken(token);
        log.info("doFilterInternal() : claims={}", claims);
        LoginInfo loginInfo = new LoginInfo(
                claims.getSubject(),
                Long.parseLong((String)claims.get("userId")),
                token);

        jwtUtil.putLoginInfo(sessionId, loginInfo);
        filterChain.doFilter(request, response);
        jwtUtil.removeLoginInfo(sessionId); //todo::중간에 exception 나게 되면?
    }
}
