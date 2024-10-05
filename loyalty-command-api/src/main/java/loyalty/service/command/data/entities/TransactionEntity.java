package loyalty.service.command.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionEntity {
    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "points")
    private int points;

    @Column(name = "timestamp")
    private Instant timestamp;

    @Column(name = "expiration_tracker_id")
    private String loyaltyBankId;

    public void addPoints(int points) {
        this.points += points;
    }
}
