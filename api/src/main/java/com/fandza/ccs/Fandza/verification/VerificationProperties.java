package com.fandza.ccs.Fandza.verification;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fandza.verification")
public record VerificationProperties(
        String secret,
        int codeLength,
        long codeTtlSeconds,
        int maxAttempts,
        long resendIntervalSeconds,
        int maxSendsPerHour
) {

    public VerificationProperties {
        if (secret == null || secret.isBlank()) {
            secret = "fandza-local-verification-secret";
        }
        if (codeLength <= 0) {
            codeLength = 4;
        }
        if (codeTtlSeconds <= 0) {
            codeTtlSeconds = 600;
        }
        if (maxAttempts <= 0) {
            maxAttempts = 5;
        }
        if (resendIntervalSeconds <= 0) {
            resendIntervalSeconds = 60;
        }
        if (maxSendsPerHour <= 0) {
            maxSendsPerHour = 5;
        }
    }
}
