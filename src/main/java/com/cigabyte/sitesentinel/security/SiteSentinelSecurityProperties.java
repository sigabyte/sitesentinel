package com.cigabyte.sitesentinel.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(
        prefix = "sitesentinel.security"
)
@Validated
public class SiteSentinelSecurityProperties {

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;

    public SiteSentinelSecurityProperties(
            String username,
            String password
    ) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}