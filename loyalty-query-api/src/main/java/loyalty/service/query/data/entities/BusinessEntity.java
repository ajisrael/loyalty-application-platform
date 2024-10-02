package loyalty.service.query.data.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode
@Document(collection = "businesses")
public class BusinessEntity {

    @Id
    private String businessId;
    private String businessName;
}
