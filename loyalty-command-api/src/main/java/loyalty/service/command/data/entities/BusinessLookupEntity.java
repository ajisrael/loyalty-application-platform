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
@Table(name = "business_lookup")
public class BusinessLookupEntity {

    @Id
    @Column(name = "business_id", unique = true)
    private String businessId;
}
