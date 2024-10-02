package loyalty.service.command.data.repositories;

import loyalty.service.command.data.entities.BusinessLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessLookupRepository extends JpaRepository<BusinessLookupEntity, String> {

    BusinessLookupEntity findByBusinessId(String businessId);
}
