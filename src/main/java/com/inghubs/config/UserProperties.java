package com.inghubs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "users")
public class UserProperties {
    private UserDetails admin;
    private UserDetails user;

    @Data
    public static class UserDetails {
        private String username;
        private String password;
        private String roles;
    }
}
