package com.lodny.rwcommon.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {
    private String header;
    private String secret;
    private String tokenTitle;
    private Long expirationMS;
}
