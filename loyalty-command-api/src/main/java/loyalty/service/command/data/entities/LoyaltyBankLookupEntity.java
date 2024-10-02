package loyalty.service.command.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_bank_lookup")
public class LoyaltyBankLookupEntity {

    @Id
    @Column(name = "loyalty_bank_id", unique = true)
    private String loyaltyBankId;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "business_id")
    private String businessId;
}
