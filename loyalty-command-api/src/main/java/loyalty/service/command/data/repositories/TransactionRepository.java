package loyalty.service.command.data.repositories;

import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import loyalty.service.command.data.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    TransactionEntity findByTransactionId(String transactionId);

    // Custom query to find transactions before a specific date
    @Query("SELECT t FROM TransactionEntity t WHERE t.timestamp < :expirationDate")
    List<TransactionEntity> findAllByTimestampBefore(Instant expirationDate);
}

