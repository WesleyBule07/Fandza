package com.fandza.ccs.Fandza.util;

import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.exception.InvalidContactException;
import java.util.regex.Pattern;

public final class ContactNormalizer {

    private static final Pattern PHONE_E164 = Pattern.compile("\\+2588[3-7]\\d{7}");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private ContactNormalizer() {
    }

    public static String normalize(ContactType type, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidContactException("Contact value must not be blank");
        }
        return switch (type) {
            case PHONE -> normalizePhone(rawValue);
            case EMAIL -> normalizeEmail(rawValue);
        };
    }

    private static String normalizePhone(String raw) {
        String digits = raw.replaceAll("[\\s\\-().]", "");
        if (digits.startsWith("+")) {
            digits = digits.substring(1);
        } else if (digits.length() == 9) {
            digits = "258" + digits;
        }
        String e164 = "+" + digits;
        if (!PHONE_E164.matcher(e164).matches()) {
            throw new InvalidContactException("Phone must be a valid Mozambican number (+258 8X XXX XXXX)");
        }
        return e164;
    }

    private static String normalizeEmail(String raw) {
        String email = raw.trim().toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new InvalidContactException("Email must be a valid address");
        }
        return email;
    }
}
