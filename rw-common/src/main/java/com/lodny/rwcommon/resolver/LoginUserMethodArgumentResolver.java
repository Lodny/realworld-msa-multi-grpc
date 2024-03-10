package com.lodny.rwcommon.resolver;

import com.lodny.rwcommon.util.JwtUtil;
import com.lodny.rwcommon.annotation.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
//@Component
public class LoginUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //        boolean hasUserType = User.class.isAssignableFrom(parameter.getParameterType());
//        return hasAnnotation && hasUserType;
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return JwtUtil.getAuthenticatedUser();
//        User user = userService.getLoginUserById(webRequest.getSessionId()).orElse(null);
//        webRequest.setAttribute("user",user,0);
//        return user;
    }
}
