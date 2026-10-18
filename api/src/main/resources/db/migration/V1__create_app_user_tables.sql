CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_app_user_email UNIQUE (email)
);

CREATE TABLE driver (
    id UUID PRIMARY KEY,
    phone_number VARCHAR(30),
    license_number VARCHAR(100),
    license_expiry TIMESTAMP WITH TIME ZONE,
    account_status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_driver_app_user FOREIGN KEY (id) REFERENCES app_user (id)
);

CREATE TABLE legal_guardian (
    id UUID PRIMARY KEY,
    phone_number VARCHAR(30),
    CONSTRAINT fk_legal_guardian_app_user FOREIGN KEY (id) REFERENCES app_user (id)
);
