package loyalty.service.command.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import loyalty.service.core.validation.ProjectionId;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "expiration_tracker")
public class ExpirationTrackerEntity {

    @Id
    @ProjectionId(message = "LoyaltyBankId should be valid")
    @NotNull(message = "LoyaltyBankId cannot be null")
    @Column(name = "loyalty_bank_id", unique = true)
    private String loyaltyBankId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="loyalty_bank_id")
    private List<TransactionEntity> transactionList = new ArrayList<>(); // TODO: maybe convert this to an ordered hash map to simplify addTransaction() method

    public ExpirationTrackerEntity(String loyaltyBankId) {
        this.loyaltyBankId = loyaltyBankId;
    }

    public void addTransaction(TransactionEntity transactionEntity) {
        // TODO: check that the loyalty bankId on the transaction entity matches the expiration tracker entity
        // TODO: check that transaction doesn't already exist in list
        transactionList.add(transactionEntity);
    }

    public void removeTransaction(TransactionEntity transactionEntity) {
        transactionList.remove(transactionEntity);
    }
}
