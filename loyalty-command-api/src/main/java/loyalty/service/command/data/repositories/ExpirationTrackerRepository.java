package loyalty.service.command.data.repositories;

import loyalty.service.command.data.entities.ExpirationTrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExpirationTrackerRepository extends JpaRepository<ExpirationTrackerEntity, String> {

    ExpirationTrackerEntity findByLoyaltyBankId(String loyaltyBankId);
}
