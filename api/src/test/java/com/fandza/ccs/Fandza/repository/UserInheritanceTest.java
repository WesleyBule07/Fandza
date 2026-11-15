package com.fandza.ccs.Fandza.repository;

import com.fandza.ccs.Fandza.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

import com.fandza.ccs.Fandza.entity.Driver;
import com.fandza.ccs.Fandza.entity.LegalGuardian;
import com.fandza.ccs.Fandza.entity.User;
import com.fandza.ccs.Fandza.enums.AccountStatus;
import com.fandza.ccs.Fandza.enums.ContactType;
import com.fandza.ccs.Fandza.enums.UserType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
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
class UserInheritanceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private static final Pattern UUID_V7 = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void driverShouldPersistThroughJoinedInheritance() {
        Driver driver = new Driver("Ana Bila", ContactType.PHONE, "+258840000001");
        driver.setLicenseNumber("L-99");

        UUID id = entityManager.persist(driver).getId();
        entityManager.flush();
        entityManager.clear();

        User loaded = userRepository.findById(id).orElseThrow();
        assertThat(loaded).isInstanceOf(Driver.class);
        assertThat(loaded.getUserType()).isEqualTo(UserType.DRIVER);
        assertThat(loaded.getContactValue()).isEqualTo("+258840000001");
        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(loaded.getUpdatedAt()).isNotNull();
        assertThat(UUID_V7.matcher(id.toString()).matches()).isTrue();

        Driver loadedDriver = (Driver) loaded;
        assertThat(loadedDriver.getAccountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(loadedDriver.getLicenseNumber()).isEqualTo("L-99");
    }

    @Test
    void legalGuardianShouldPersistThroughJoinedInheritance() {
        LegalGuardian guardian = new LegalGuardian("Bento", ContactType.EMAIL, "bento@fandza.co.mz");

        UUID id = entityManager.persist(guardian).getId();
        entityManager.flush();
        entityManager.clear();

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);

        User loaded = users.get(0);
        assertThat(loaded.getId()).isEqualTo(id);
        assertThat(loaded).isInstanceOf(LegalGuardian.class);
        assertThat(loaded.getUserType()).isEqualTo(UserType.LEGAL_GUARDIAN);
        assertThat(loaded.getCreatedAt()).isInstanceOf(Instant.class);
    }

    @Test
    void userRepositoryShouldFindAndCheckUniquenessByContact() {
        LegalGuardian guardian = new LegalGuardian("Bento", ContactType.EMAIL, "bento@fandza.co.mz");
        UUID id = entityManager.persist(guardian).getId();
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.existsByContactValue("bento@fandza.co.mz")).isTrue();
        assertThat(userRepository.existsByContactValue("other@fandza.co.mz")).isFalse();
        assertThat(userRepository.findByContactValue("bento@fandza.co.mz")).hasValueSatisfying(
                user -> assertThat(user.getId()).isEqualTo(id));
    }
}
