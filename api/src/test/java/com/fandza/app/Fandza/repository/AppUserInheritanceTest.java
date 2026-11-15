package com.fandza.app.Fandza.repository;

import com.fandza.app.Fandza.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

import com.fandza.app.Fandza.entity.Admin;
import com.fandza.app.Fandza.entity.AppUser;
import com.fandza.app.Fandza.entity.Driver;
import com.fandza.app.Fandza.entity.LegalGuardian;
import com.fandza.app.Fandza.enums.AccountStatus;
import com.fandza.app.Fandza.enums.UserType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AppUserInheritanceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void cleanDatabase() {
        appUserRepository.deleteAll();
    }

    @Test
    void driverShouldPersistThroughJoinedInheritance() {
        Driver driver = new Driver("Ana Bila", "ana@fandza.co.mz", "encoded-hash");
        driver.setPhoneNumber("+258841111111");
        driver.setLicenseNumber("L-99");

        UUID id = entityManager.persist(driver).getId();
        entityManager.flush();
        entityManager.clear();

        AppUser loaded = appUserRepository.findById(id).orElseThrow();
        assertThat(loaded).isInstanceOf(Driver.class);
        assertThat(loaded.getUserType()).isEqualTo(UserType.DRIVER);
        assertThat(loaded.getEmail()).isEqualTo("ana@fandza.co.mz");
        assertThat(loaded.getCreatedAt()).isNotNull();

        Driver loadedDriver = (Driver) loaded;
        assertThat(loadedDriver.getAccountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(loadedDriver.getLicenseNumber()).isEqualTo("L-99");
    }

    @Test
    void legalGuardianShouldPersistThroughJoinedInheritance() {
        LegalGuardian guardian = new LegalGuardian("Bento", "bento@fandza.co.mz", "encoded-hash");
        guardian.setPhoneNumber("+258842222222");

        UUID id = entityManager.persist(guardian).getId();
        entityManager.flush();
        entityManager.clear();

        List<AppUser> users = appUserRepository.findAll();
        assertThat(users).hasSize(1);

        AppUser loaded = users.get(0);
        assertThat(loaded.getId()).isEqualTo(id);
        assertThat(loaded).isInstanceOf(LegalGuardian.class);
        assertThat(loaded.getUserType()).isEqualTo(UserType.LEGAL_GUARDIAN);
    }

    @Test
    void adminShouldPersistThroughJoinedInheritance() {
        Admin admin = new Admin("Root Admin", "admin@fandza.co.mz", "encoded-hash");

        UUID id = entityManager.persist(admin).getId();
        entityManager.flush();
        entityManager.clear();

        AppUser loaded = appUserRepository.findById(id).orElseThrow();
        assertThat(loaded).isInstanceOf(Admin.class);
        assertThat(loaded.getUserType()).isEqualTo(UserType.ADMIN);
        assertThat(loaded.getCreatedAt()).isNotNull();
    }
}
