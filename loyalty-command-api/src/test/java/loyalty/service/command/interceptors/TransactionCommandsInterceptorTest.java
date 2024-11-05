package loyalty.service.command.interceptors;

import loyalty.service.command.commands.transactions.CreateCapturedTransactionCommand;
import loyalty.service.command.commands.transactions.CreateVoidTransactionCommand;
import loyalty.service.command.data.entities.RedemptionTrackerEntity;
import loyalty.service.command.data.repositories.RedemptionTrackerRepository;
import loyalty.service.core.exceptions.ExcessiveCapturePointsException;
import loyalty.service.core.exceptions.ExcessiveVoidPointsException;
import loyalty.service.core.exceptions.PaymentIdNotFoundException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionCommandsInterceptorTest {

    @Mock
    private RedemptionTrackerRepository redemptionTrackerRepository;

    @InjectMocks
    private TransactionCommandsInterceptor transactionCommandsInterceptor;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_PAYMENT_ID = UUID.randomUUID().toString();
    private static final int TEST_POINTS = 100;

    @Test
    @DisplayName("Can handle valid CreateVoidTransactionCommand")
    void testHandle_whenCreateVoidTransactionCommandandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        CreateVoidTransactionCommand command = CreateVoidTransactionCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity();
        redemptionTrackerEntity.setPaymentId(TEST_PAYMENT_ID);
        redemptionTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        redemptionTrackerEntity.setAuthorizedPoints(TEST_POINTS);
        redemptionTrackerEntity.setCapturedPoints(0);

        when(redemptionTrackerRepository.findByPaymentId(TEST_PAYMENT_ID)).thenReturn(redemptionTrackerEntity);

        // Act
        CommandMessage<?> result = transactionCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
    }


    @Test
    @DisplayName("Can handle invalid CreateVoidCommand for non existing redemption tracker")
    void testHandle_whenCreateVoidCommandHandledAndRedemptionTrackerDoesNotExist_shouldThrowException() {
        // Arrange
        CreateVoidTransactionCommand command = CreateVoidTransactionCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        when(redemptionTrackerRepository.findByPaymentId(TEST_PAYMENT_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(PaymentIdNotFoundException.class, () -> {
            CommandMessage<?> result = transactionCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw PaymentIdNotFoundException");

        // Assert
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
    }

    @Test
    @DisplayName("Can handle invalid CreateVoidCommand voiding more points that available")
    void testHandle_whenCreateVoidCommandHandledAndAttemptingToVoidMorePointsThanAvailable_shouldThrowException() {
        // Arrange
        CreateVoidTransactionCommand command = CreateVoidTransactionCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity();
        redemptionTrackerEntity.setPaymentId(TEST_PAYMENT_ID);
        redemptionTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        redemptionTrackerEntity.setAuthorizedPoints(TEST_POINTS);
        redemptionTrackerEntity.setCapturedPoints(50);

        when(redemptionTrackerRepository.findByPaymentId(TEST_PAYMENT_ID)).thenReturn(redemptionTrackerEntity);

        // Act & Assert
        assertThrows(ExcessiveVoidPointsException.class, () -> {
            CommandMessage<?> result = transactionCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw ExcessiveVoidPointsException");

        // Assert
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
    }

    @Test
    @DisplayName("Can handle valid CreateCapturedTransactionCommand")
    void testHandle_whenCreateCapturedTransactionCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        CreateCapturedTransactionCommand command = CreateCapturedTransactionCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity();
        redemptionTrackerEntity.setPaymentId(TEST_PAYMENT_ID);
        redemptionTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        redemptionTrackerEntity.setAuthorizedPoints(TEST_POINTS);
        redemptionTrackerEntity.setCapturedPoints(0);

        when(redemptionTrackerRepository.findByPaymentId(TEST_PAYMENT_ID)).thenReturn(redemptionTrackerEntity);

        // Act
        CommandMessage<?> result = transactionCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
    }


    @Test
    @DisplayName("Can handle invalid CreateCapturedCommand for non existing redemption tracker")
    void testHandle_whenCreateCapturedCommandHandledAndRedemptionTrackerDoesNotExist_shouldThrowException() {
        // Arrange
        CreateCapturedTransactionCommand command = CreateCapturedTransactionCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        when(redemptionTrackerRepository.findByPaymentId(TEST_PAYMENT_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(PaymentIdNotFoundException.class, () -> {
            CommandMessage<?> result = transactionCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw PaymentIdNotFoundException");

        // Assert
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
    }

    @Test
    @DisplayName("Can handle invalid CreateCapturedCommand capturing more points that available")
    void testHandle_whenCreateCapturedCommandHandledAndAttemptingToCaptureMorePointsThanAvailable_shouldThrowException() {
        // Arrange
        CreateCapturedTransactionCommand command = CreateCapturedTransactionCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .paymentId(TEST_PAYMENT_ID)
                .points(TEST_POINTS)
                .build();

        RedemptionTrackerEntity redemptionTrackerEntity = new RedemptionTrackerEntity();
        redemptionTrackerEntity.setPaymentId(TEST_PAYMENT_ID);
        redemptionTrackerEntity.setLoyaltyBankId(TEST_LOYALTY_BANK_ID);
        redemptionTrackerEntity.setAuthorizedPoints(TEST_POINTS);
        redemptionTrackerEntity.setCapturedPoints(50);

        when(redemptionTrackerRepository.findByPaymentId(TEST_PAYMENT_ID)).thenReturn(redemptionTrackerEntity);

        // Act & Assert
        assertThrows(ExcessiveCapturePointsException.class, () -> {
            CommandMessage<?> result = transactionCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw ExcessiveCapturePointsException");

        // Assert
        verify(redemptionTrackerRepository, times(1)).findByPaymentId(TEST_PAYMENT_ID);
    }
}