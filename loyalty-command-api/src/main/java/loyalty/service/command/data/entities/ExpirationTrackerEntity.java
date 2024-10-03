package loyalty.service.command.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "expiration_tracker")
public class ExpirationTrackerEntity {

    @Id
    @Column(name = "loyalty_bank_id", unique = true)
    private String loyaltyBankId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "loyalty_bank_id")
    private List<TransactionEntity> transactionList = new ArrayList<>();

    public ExpirationTrackerEntity(String loyaltyBankId) {
        this.loyaltyBankId = loyaltyBankId;
    }

    public void addTransaction(TransactionEntity transactionEntity) {
        transactionList.add(transactionEntity);
    }

    public void removeTransaction(TransactionEntity transactionEntity) {
        transactionList.remove(transactionEntity);
    }
}
