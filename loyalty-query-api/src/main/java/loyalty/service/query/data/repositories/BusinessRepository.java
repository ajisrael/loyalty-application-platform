package loyalty.service.query.data.repositories;

import loyalty.service.query.data.entities.BusinessEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BusinessRepository extends MongoRepository<BusinessEntity, String> {

    Optional<BusinessEntity> findByBusinessId(String businessId);
}
