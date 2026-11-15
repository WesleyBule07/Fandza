-- Contact verification codes: hashed 4-digit codes with short expiry,
-- attempt limits and resend throttling. One row per issued code.

CREATE TABLE contact_verification (
    id uuid NOT NULL,
    destination varchar(255) NOT NULL,
    purpose varchar(64) NOT NULL,
    code_hash varchar(255) NOT NULL,
    expires_at timestamptz NOT NULL,
    attempts int NOT NULL DEFAULT 0,
    last_sent_at timestamptz NOT NULL,
    used_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT pk_contact_verification PRIMARY KEY (id)
);

CREATE INDEX idx_contact_verification_destination ON contact_verification (destination, purpose);
