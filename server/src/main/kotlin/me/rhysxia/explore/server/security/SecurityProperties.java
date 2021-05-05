package me.rhysxia.explore.server.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@ConfigurationProperties("explore.security")
public class SecurityProperties {
    private String tokenName = HttpHeaders.AUTHORIZATION;
    private String tokenInternalName = "__TOKEN__";

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenInternalName() {
        return tokenInternalName;
    }

    public void setTokenInternalName(String tokenInternalName) {
        this.tokenInternalName = tokenInternalName;
    }
}
