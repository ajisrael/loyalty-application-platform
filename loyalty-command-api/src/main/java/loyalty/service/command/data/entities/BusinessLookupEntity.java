package loyalty.service.command.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loyalty.service.core.validation.ProjectionId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "business_lookup")
public class BusinessLookupEntity {

    @Id
    @ProjectionId(message = "BusinessId should be valid")
    @NotNull(message = "BusinessId cannot be null")
    @Column(name = "business_id", unique = true)
    private String businessId;
}
