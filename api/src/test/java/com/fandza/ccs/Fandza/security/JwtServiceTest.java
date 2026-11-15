package com.fandza.ccs.Fandza.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fandza.ccs.Fandza.entity.Driver;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
    private static final String OTHER_SECRET = Base64.getEncoder()
            .encodeToString("fedcba9876543210fedcba9876543210".getBytes(StandardCharsets.UTF_8));

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, Duration.ofHours(8)));

    private Driver driver() {
        return new Driver("Jane Doe", "jane@fandza.co.mz", "encoded-hash");
    }

    @Test
    void shouldGenerateRoundTripValidToken() {
        String token = jwtService.generateToken(driver());

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("jane@fandza.co.mz");
        assertThat(jwtService.extractExpiration(token)).isAfter(Instant.now());
    }

    @Test
    void shouldRejectExpiredToken() {
        String expiredToken = tokenIssuedAt(
                Instant.now().minus(10, ChronoUnit.MINUTES), Instant.now().minus(5, ChronoUnit.MINUTES));

        assertThat(jwtService.isValid(expiredToken)).isFalse();
        assertThatThrownBy(() -> jwtService.extractEmail(expiredToken)).isInstanceOf(JwtException.class);
    }

    private String tokenIssuedAt(Instant issuedAt, Instant expiresAt) {
        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(SECRET), "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(key));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("fandza")
                .subject("jane@fandza.co.mz")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = jwtService.generateToken(driver());

        assertThat(jwtService.isValid(token + "x")).isFalse();
    }

    @Test
    void shouldRejectTokenSignedWithAnotherSecret() {
        JwtService other = new JwtService(new JwtProperties(OTHER_SECRET, Duration.ofHours(8)));
        String foreignToken = other.generateToken(driver());

        assertThat(jwtService.isValid(foreignToken)).isFalse();
        assertThatThrownBy(() -> jwtService.extractEmail(foreignToken)).isInstanceOf(JwtException.class);
    }
}
