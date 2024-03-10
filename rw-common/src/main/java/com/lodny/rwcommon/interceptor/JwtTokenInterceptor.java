package com.lodny.rwcommon.interceptor;

import com.lodny.rwcommon.annotation.JwtTokenRequired;
import com.lodny.rwcommon.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
//@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[I] preHandle() : request={}", request);

        if (handler instanceof HandlerMethod method) {
            if (!method.getMethod().isAnnotationPresent(JwtTokenRequired.class))
                return true;

            if (JwtUtil.getAuthenticatedUser() == null)
                throw new IllegalArgumentException("token not found");
//                    response.sendRedirect("/my-error");
        }

        return true;
    }
}
