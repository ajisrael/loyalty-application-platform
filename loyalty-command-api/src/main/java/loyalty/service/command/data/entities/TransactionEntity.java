package loyalty.service.command.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import loyalty.service.core.validation.ProjectionId;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @ProjectionId(message = "TransactionId should be Valid")
    @NotNull(message = "TransactionId cannot be null")
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "points")
    private int points;

    @NotNull(message = "Timestamp cannot be null")
    @Column(name = "timestamp")
    private Instant timestamp;

    @ProjectionId(message = "LoyaltyBankId should be valid")
    @NotNull(message = "LoyaltyBankId cannot be null")
    @Column(name = "expiration_tracker_id")
    private String loyaltyBankId;

    public void addPoints(int points) {
        this.points += points;
    }
}
