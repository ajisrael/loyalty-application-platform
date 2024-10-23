package loyalty.service.command.data.entities;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import loyalty.service.command.config.AxonConfig;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class AccountLookupEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private AccountLookupEntity accountLookupEntity;

    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID_2 = UUID.randomUUID().toString();
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_EMAIL_2 = "test2@test.com";

    @BeforeEach
    void setup() {
        accountLookupEntity = new AccountLookupEntity();
        accountLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        accountLookupEntity.setEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Can store valid AccountLookupEntity")
    void testAccountLookupEntity_whenValidAccountDetailsProvided_shouldReturnStoredAccountDetails() {
        // Act
        AccountLookupEntity storedAccountLookupEntity = testEntityManager.persistAndFlush(accountLookupEntity);

        // Assert
        assertEquals(accountLookupEntity.getAccountId(), storedAccountLookupEntity.getAccountId(), "AccountIds should match");
        assertEquals(accountLookupEntity.getEmail(), storedAccountLookupEntity.getEmail(), "Emails should match");
    }

    @Test
    @DisplayName("Cannot store AccountLookupEntity with same accountId")
    void testAccountLookupEntity_whenAccountIdIsNotUnique_shouldThrowException() {
        // Arrange
        AccountLookupEntity secondAccountLookupEntity = new AccountLookupEntity();
        secondAccountLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        secondAccountLookupEntity.setEmail(TEST_EMAIL_2);

        testEntityManager.persistAndFlush(secondAccountLookupEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(accountLookupEntity);
        }, "Should throw PersistenceException due to duplicate accountId");
    }

    @Test
    @DisplayName("Cannot store AccountLookupEntity with same email")
    void testAccountLookupEntity_whenEmailIsNotUnique_shouldThrowException() {
        // Arrange
        AccountLookupEntity secondAccountLookupEntity = new AccountLookupEntity();
        secondAccountLookupEntity.setAccountId(TEST_ACCOUNT_ID_2);
        secondAccountLookupEntity.setEmail(TEST_EMAIL);

        testEntityManager.persistAndFlush(secondAccountLookupEntity);

        // Act & Assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(accountLookupEntity);
        }, "Should throw PersistenceException due to duplicate email");
    }

    @Test
    @DisplayName("Cannot store AccountLookupEntity with invalid accountId format")
    void testAccountLookupEntity_whenInvalidAccountIdProvided_shouldThrowException() {
        // Arrange
        accountLookupEntity.setAccountId("invalid-uuid");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(accountLookupEntity);
        }, "Should throw ConstraintViolationException due to invalid UUID format");
    }

    @Test
    @DisplayName("Cannot store AccountLookupEntity with invalid email format")
    void testAccountLookupEntity_whenInvalidEmailProvided_shouldThrowException() {
        // Arrange
        accountLookupEntity.setEmail("invalid-email");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(accountLookupEntity);
        }, "Should throw ConstraintViolationException due to invalid email format");
    }

    @Test
    @DisplayName("Cannot store AccountLookupEntity with null accountId")
    void testAccountLookupEntity_whenAccountIdIsNull_shouldThrowException() {
        // Arrange
        accountLookupEntity.setAccountId(null);

        // Act & Assert
        assertThrows(IdentifierGenerationException.class, () -> {
            testEntityManager.persistAndFlush(accountLookupEntity);
        }, "Should throw IdentifierGenerationException due to null accountId");
    }

    @Test
    @DisplayName("Cannot store AccountLookupEntity with null email")
    void testAccountLookupEntity_whenEmailIsNull_shouldThrowException() {
        // Arrange
        accountLookupEntity.setEmail(null);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.persistAndFlush(accountLookupEntity);
        }, "Should throw ConstraintViolationException due to null email");
    }
}