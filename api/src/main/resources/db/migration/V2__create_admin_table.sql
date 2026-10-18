CREATE TABLE app_admin (
    id UUID PRIMARY KEY,
    CONSTRAINT fk_app_admin_app_user FOREIGN KEY (id) REFERENCES app_user (id)
);
