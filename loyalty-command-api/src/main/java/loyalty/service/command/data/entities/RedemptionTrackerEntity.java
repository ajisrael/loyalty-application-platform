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
@Table(name = "redemption_tracker")
public class RedemptionTrackerEntity {

    @Id
    @Column(name = "payment_id", unique = true)
    private String paymentId;
    @Column(name = "loyalty_bank_id")
    private String loyaltyBankId;
    @Column(name = "authorized_points")
    private int authorizedPoints;
    @Column(name = "captured_points")
    private int capturedPoints;

    public void voidAuthorizedPoints(int points) {
        this.authorizedPoints -= points;
    }

    public void addCapturedPoints(int points) {
        this.capturedPoints += points;
    }

    public int getPointsAvailableForRedemption() {
        return this.authorizedPoints - this.capturedPoints;
    }
}
