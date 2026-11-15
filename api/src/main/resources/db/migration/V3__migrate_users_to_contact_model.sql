-- Reshape user tables to the contact-based identity model.
-- One normalized contact per account (E.164 phone or email), verified flag,
-- account status lifted to the base table, Keycloak subject linkage, UTC timestamps.

ALTER TABLE app_user ADD COLUMN keycloak_id uuid;
CREATE UNIQUE INDEX uk_app_user_keycloak_id ON app_user (keycloak_id);

ALTER TABLE app_user ADD COLUMN contact_type varchar(16);
ALTER TABLE app_user ADD COLUMN contact_value varchar(255);
ALTER TABLE app_user ADD COLUMN contact_verified boolean NOT NULL DEFAULT false;
ALTER TABLE app_user ADD COLUMN account_status varchar(32);
ALTER TABLE app_user ADD COLUMN updated_at timestamptz NOT NULL DEFAULT now();

UPDATE app_user
SET contact_type = 'EMAIL',
    contact_value = lower(email),
    contact_verified = true;

UPDATE app_user u
SET account_status = COALESCE((SELECT d.account_status FROM driver d WHERE d.id = u.id), 'ACTIVE');

ALTER TABLE app_user ALTER COLUMN contact_type SET NOT NULL;
ALTER TABLE app_user ALTER COLUMN contact_value SET NOT NULL;
ALTER TABLE app_user ALTER COLUMN account_status SET NOT NULL;

CREATE UNIQUE INDEX uk_app_user_contact_value ON app_user (contact_value);

ALTER TABLE app_user ALTER COLUMN created_at SET DEFAULT now();
ALTER TABLE app_user DROP COLUMN password;
ALTER TABLE app_user DROP COLUMN email;

DROP TABLE app_admin;

ALTER TABLE driver DROP COLUMN account_status;
ALTER TABLE driver DROP COLUMN phone_number;
ALTER TABLE legal_guardian DROP COLUMN phone_number;
