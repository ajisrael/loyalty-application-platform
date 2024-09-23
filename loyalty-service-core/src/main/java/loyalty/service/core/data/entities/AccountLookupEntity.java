package loyalty.service.core.data.entities;

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
@Table(name = "account_lookup")
public class AccountLookupEntity {

    @Id
    @Column(name = "account_id", unique = true)
    private String accountId;
    @Column(name = "email", unique = true)
    private String email;
}
