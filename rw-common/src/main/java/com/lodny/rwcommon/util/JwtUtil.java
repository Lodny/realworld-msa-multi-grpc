package com.lodny.rwcommon.util;

import com.lodny.rwcommon.properties.JwtProperty;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperty jwtProperty;

    private static final Map<String, LoginInfo> userMap = new HashMap<>();


    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        log.info("ready() : jwtProperty={}", jwtProperty);
    }

    public String createToken(final String email, final Long userId) {
        Date expiration = new Date(System.currentTimeMillis() + jwtProperty.getExpirationMS());

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString())
                .setExpiration(expiration)
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperty.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getToken(final String authString) {
        String tokenTitle = jwtProperty.getTokenTitle();
        if (authString == null || !authString.startsWith(tokenTitle))
            return null;

        return authString.replace(tokenTitle, "");
    }

    public Claims getClaimsByToken(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("getEmailByToken() : 1={}", 1);
            throw new IllegalArgumentException("The token has expired.");
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("UnsupportedJwtException");
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("MalformedJwtException");
        } catch (SignatureException e) {
            throw new IllegalArgumentException("SignatureException");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("IllegalArgumentException");
        }
    }

    public void putLoginInfo(final String sessionId, final LoginInfo loginInfo) {
        log.info("putLoginUser() : sessionId={}", sessionId);
        userMap.put(sessionId, loginInfo);
    }

    public void removeLoginInfo(final String sessionId) {
        log.info("removeLoginUser() : sessionId={}", sessionId);
        userMap.remove(sessionId);
    }

    public static LoginInfo getAuthenticatedUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        String sessionId = attributes.getSessionId();
        log.info("getAuthenticatedUser() : sessionId={}", sessionId);

        return userMap.get(sessionId);
    }
}
