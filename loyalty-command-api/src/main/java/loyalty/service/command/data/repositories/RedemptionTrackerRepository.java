package loyalty.service.command.data.repositories;

import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionTrackerRepository extends JpaRepository<RedemptionTrackerEntity, String> {

    RedemptionTrackerEntity findByPaymentId(String paymentId);
    List<RedemptionTrackerEntity> findByLoyaltyBankId(String loyaltyBankId);
}
