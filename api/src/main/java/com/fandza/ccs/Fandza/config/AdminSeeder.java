package com.fandza.ccs.Fandza.config;

import com.fandza.ccs.Fandza.entity.Admin;
import com.fandza.ccs.Fandza.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String fullName;

    public AdminSeeder(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${fandza.admin.email:}") String email,
            @Value("${fandza.admin.password:}") String password,
            @Value("${fandza.admin.full-name:Administrator}") String fullName) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            log.info("Admin bootstrap skipped: set FANDZA_ADMIN_EMAIL and FANDZA_ADMIN_PASSWORD to create the first admin");
            return;
        }
        if (appUserRepository.existsByEmail(email)) {
            log.info("Admin bootstrap skipped: a user with email {} already exists", email);
            return;
        }
        appUserRepository.save(new Admin(fullName, email, passwordEncoder.encode(password)));
        log.info("Admin bootstrap created the administrator account for {}", email);
    }
}
