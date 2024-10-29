package loyalty.service.command.data.repositories;

import loyalty.service.command.config.AxonConfig;
import loyalty.service.command.data.entities.TransactionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {AxonConfig.class})
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_TRANSACTION_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;
    private static final Instant TEST_TIMESTAMP_BEFORE = LocalDate.of(2000,1,1).atStartOfDay(ZoneId.of("UTC")).toInstant();
    private static final Instant TEST_TIMESTAMP_REQUEST = LocalDate.of(2000,1,2).atStartOfDay(ZoneId.of("UTC")).toInstant();
    private static final Instant TEST_TIMESTAMP_AFTER = LocalDate.of(2000,2,1).atStartOfDay(ZoneId.of("UTC")).toInstant();
;

    @Test
    @DisplayName("Should get all. transactions with timestamp less than date requested")
    void testFindAllByTimestampBefore_whenTimestampProvided_shouldReturnTransactions() {
        // Arrange
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setTransactionId(UUID.randomUUID().toString());
        transaction1.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transaction1.setPoints(TEST_POINTS);
        transaction1.setTimestamp(TEST_TIMESTAMP_BEFORE);
        testEntityManager.persistAndFlush(transaction1);

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setTransactionId(UUID.randomUUID().toString());
        transaction2.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transaction2.setPoints(TEST_POINTS);
        transaction2.setTimestamp(TEST_TIMESTAMP_REQUEST);
        testEntityManager.persistAndFlush(transaction2);

        TransactionEntity transaction3 = new TransactionEntity();
        transaction3.setTransactionId(UUID.randomUUID().toString());
        transaction3.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        transaction3.setPoints(TEST_POINTS);
        transaction3.setTimestamp(TEST_TIMESTAMP_AFTER);
        testEntityManager.persistAndFlush(transaction3);

        // Act
        List<TransactionEntity> transactions = transactionRepository.findAllByTimestampBefore(TEST_TIMESTAMP_REQUEST);

        // Assert
        assertEquals(1, transactions.size(), "Should only get one transaction");
        TransactionEntity storedTransaction = transactions.get(0);
        assertEquals(transaction1.getTransactionId(), storedTransaction.getTransactionId(), "TransactionIds should match");
        assertEquals(transaction1.getLoyaltyBankId(), storedTransaction.getLoyaltyBankId(), "LoyaltyBankIds should match");
        assertEquals(transaction1.getPoints(), storedTransaction.getPoints(), "Pointss should match");
        assertEquals(transaction1.getTimestamp(), storedTransaction.getTimestamp(), "Timestamps should match");
    }
}