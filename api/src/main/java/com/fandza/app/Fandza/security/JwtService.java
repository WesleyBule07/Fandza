package com.fandza.app.Fandza.security;

import com.fandza.app.Fandza.entity.AppUser;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String ISSUER = "fandza";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final Duration expiration;

    public JwtService(JwtProperties properties) {
        byte[] keyBytes = Base64.getDecoder().decode(properties.secret());
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.expiration = properties.expiration();
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(key));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    public String generateToken(AppUser user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plus(expiration))
                .claim("userType", user.getUserType().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String extractEmail(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        return jwtDecoder.decode(token).getExpiresAt();
    }

    public boolean isValid(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
