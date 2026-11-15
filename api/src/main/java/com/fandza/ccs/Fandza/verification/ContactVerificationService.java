package com.fandza.ccs.Fandza.verification;

import com.fandza.ccs.Fandza.entity.ContactVerification;
import com.fandza.ccs.Fandza.exception.InvalidVerificationCodeException;
import com.fandza.ccs.Fandza.exception.TooManyRequestsException;
import com.fandza.ccs.Fandza.repository.ContactVerificationRepository;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactVerificationService {

    private static final Logger log = LoggerFactory.getLogger(ContactVerificationService.class);

    private final ContactVerificationRepository repository;
    private final VerificationProperties properties;
    private final SecureRandom random = new SecureRandom();

    public ContactVerificationService(ContactVerificationRepository repository, VerificationProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Transactional
    public ContactVerification issue(String destination, String purpose) {
        Optional<ContactVerification> latest = repository
                .findFirstByDestinationAndPurposeOrderByLastSentAtDesc(destination, purpose);

        if (latest.isPresent()) {
            ContactVerification current = latest.get();
            Instant now = Instant.now();
            long secondsSinceSend = Duration.between(current.getLastSentAt(), now).getSeconds();
            if (secondsSinceSend < properties.resendIntervalSeconds()) {
                long wait = properties.resendIntervalSeconds() - secondsSinceSend;
                throw new TooManyRequestsException(
                        "Please wait " + wait + "s before requesting a new code");
            }
            long recentSends = repository.countByDestinationAndLastSentAtAfter(destination,
                    now.minusSeconds(3600));
            if (recentSends >= properties.maxSendsPerHour()) {
                throw new TooManyRequestsException("Too many codes requested for this contact");
            }
        }

        String code = generateCode();
        ContactVerification verification = new ContactVerification(
                destination, purpose, hashOf(code), Instant.now());
        repository.save(verification);
        deliver(destination, code);
        return verification;
    }

    @Transactional
    public void confirm(String destination, String purpose, String code) {
        ContactVerification verification = repository
                .findFirstByDestinationAndPurposeOrderByLastSentAtDesc(destination, purpose)
                .orElseThrow(() -> new InvalidVerificationCodeException("Verification code not found"));

        if (verification.getUsedAt() != null) {
            throw new InvalidVerificationCodeException("Verification code was already used");
        }
        if (Instant.now().isAfter(verification.getExpiresAt())) {
            throw new InvalidVerificationCodeException("Verification code has expired");
        }
        if (verification.getAttempts() >= properties.maxAttempts()) {
            throw new TooManyRequestsException("Too many incorrect attempts; request a new code");
        }

        if (!hashOf(code).equals(verification.getCodeHash())) {
            verification.setAttempts(verification.getAttempts() + 1);
            throw new InvalidVerificationCodeException("Verification code is incorrect");
        }

        verification.setUsedAt(Instant.now());
    }

    public boolean isVerified(String destination, String purpose) {
        return repository.findFirstByDestinationAndPurposeOrderByLastSentAtDesc(destination, purpose)
                .map(v -> v.getUsedAt() != null
                        && !Instant.now().isAfter(v.getExpiresAt().plusSeconds(86400)))
                .orElse(false);
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, properties.codeLength());
        return String.format("%0" + properties.codeLength() + "d", random.nextInt(bound));
    }

    String hashOf(String code) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(
                    mac.doFinal(code.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Unable to hash verification code", e);
        }
    }

    private void deliver(String destination, String code) {
        // Delivery channel is reserved for the notification feature (A7). For the prototype
        // the code is logged; no real SMS/email is sent.
        log.info("[prototype] verification code for {} -> {}", destination, code);
    }
}
