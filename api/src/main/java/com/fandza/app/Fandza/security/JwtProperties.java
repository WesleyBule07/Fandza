package com.fandza.app.Fandza.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fandza.security.jwt")
public record JwtProperties(String secret, Duration expiration) {
}
