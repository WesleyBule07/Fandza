package com.fandza.ccs.Fandza.controller;

import com.fandza.ccs.Fandza.verification.ContactVerificationService;
import com.fandza.ccs.Fandza.verification.VerificationProperties;
import com.fandza.ccs.Fandza.util.ContactNormalizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fandza.ccs.Fandza.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/contact-verifications")
@Tag(name = "Contact verification", description = "Issue and confirm contact verification codes")
@Validated
public class ContactVerificationController {

    private static final String PURPOSE = "CONTACT_VERIFICATION";

    private final ContactVerificationService verificationService;
    private final VerificationProperties verificationProperties;

    public ContactVerificationController(ContactVerificationService verificationService,
            VerificationProperties verificationProperties) {
        this.verificationService = verificationService;
        this.verificationProperties = verificationProperties;
    }

    public record IssueRequest(
            @NotNull(message = "contact type must not be null") ContactType contactType,
            @NotBlank(message = "contact value must not be blank") String contactValue) {
    }

    public record ConfirmRequest(
            @NotNull(message = "contact type must not be null") ContactType contactType,
            @NotBlank(message = "contact value must not be blank") String contactValue,
            @NotBlank(message = "code must not be blank") String code) {
    }

    public record IssueResponse(String destination, Instant expiresAt) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Send a verification code to a contact",
            description = "Normalizes the contact, generates a 4-digit code (hashed, single use, short expiry) and delivers it.")
    @ApiResponse(responseCode = "200", description = "Code issued")
    @ApiResponse(responseCode = "400", description = "Invalid contact")
    @ApiResponse(responseCode = "429", description = "Resend interval or hourly limit exceeded")
    public IssueResponse issue(@RequestBody IssueRequest request) {
        String destination = ContactNormalizer.normalize(request.contactType(), request.contactValue());
        var verification = verificationService.issue(destination, PURPOSE);
        return new IssueResponse(destination, verification.getExpiresAt());
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Confirm a contact with the verification code")
    @ApiResponse(responseCode = "204", description = "Contact verified")
    @ApiResponse(responseCode = "400", description = "Wrong, expired or already used code")
    @ApiResponse(responseCode = "429", description = "Attempt limit exceeded")
    public void confirm(@RequestBody ConfirmRequest request) {
        String destination = ContactNormalizer.normalize(request.contactType(), request.contactValue());
        verificationService.confirm(destination, PURPOSE, request.code().trim());
    }
}
