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

import static loyalty.service.core.constants.ExceptionMessages.CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE;
import static loyalty.service.core.constants.ExceptionMessages.CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "redemption_tracker")
public class RedemptionTrackerEntity {

    @Id
    @ProjectionId(message = "PaymentId should be valid")
    @NotNull(message = "PaymentId cannot be null")
    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @ProjectionId(message = "LoyaltyBankId should be valid")
    @NotNull(message = "LoyaltyBankId cannot be null")
    @Column(name = "loyalty_bank_id")
    private String loyaltyBankId;

    // TODO: verify that both sets of points are not negative
    @Column(name = "authorized_points")
    private int authorizedPoints;

    @Column(name = "captured_points")
    private int capturedPoints;

    public void voidAuthorizedPoints(int points) {
        if (this.getPointsAvailableForRedemption() - points < 0) {
            throw new IllegalArgumentException(CANNOT_VOID_MORE_POINTS_THAN_AVAILABLE);
        }
        this.authorizedPoints -= points;
    }

    public void addCapturedPoints(int points) {
        if (points > this.getPointsAvailableForRedemption()) {
            throw new IllegalArgumentException(CANNOT_CAPTURE_MORE_POINTS_THAN_AVAILABLE);
        }
        this.capturedPoints += points;
    }

    public int getPointsAvailableForRedemption() {
        return this.authorizedPoints - this.capturedPoints;
    }
}
