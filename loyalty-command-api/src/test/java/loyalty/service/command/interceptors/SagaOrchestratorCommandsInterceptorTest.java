package loyalty.service.command.interceptors;

import loyalty.service.command.commands.StartAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.core.exceptions.BusinessNotFoundException;
import loyalty.service.core.exceptions.EmailExistsForAccountException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SagaOrchestratorCommandsInterceptorTest {

    @Mock
    private AccountLookupRepository accountLookupRepository;
    @Mock
    private BusinessLookupRepository businessLookupRepository;

    @InjectMocks
    SagaOrchestratorCommandsInterceptor sagaOrchestratorCommandsInterceptor;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();


    @Test
    @DisplayName("Can handle valid StartAccountAndLoyaltyBankCreationCommand")
    void testHandle_whenStartAccountAndLoyaltyBankCreationCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        StartAccountAndLoyaltyBankCreationCommand command = StartAccountAndLoyaltyBankCreationCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        when(accountLookupRepository.findByEmail(TEST_EMAIL)).thenReturn(null);

        BusinessLookupEntity businessLookupEntity = new BusinessLookupEntity(TEST_BUSINESS_ID);
        when(businessLookupRepository.findByBusinessId(TEST_BUSINESS_ID)).thenReturn(businessLookupEntity);

        // Act
        CommandMessage<?> result = sagaOrchestratorCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can handle invalid StartAccountAndLoyaltyBankCreationCommand for existing email")
    void testHandle_whenStartAccountAndLoyaltyBankCreationCommandHandledAndEmailAlreadyExists_shouldThrowException() {
        // Arrange
        StartAccountAndLoyaltyBankCreationCommand command = StartAccountAndLoyaltyBankCreationCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        when(accountLookupRepository.findByEmail(TEST_EMAIL)).thenReturn(new AccountLookupEntity(TEST_ACCOUNT_ID, TEST_EMAIL));

        // Act & Assert
        assertThrows(EmailExistsForAccountException.class, () -> {
            CommandMessage<?> result = sagaOrchestratorCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw EmailExistsForAccountException");

        // Assert
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(businessLookupRepository, times(0)).findByBusinessId(TEST_BUSINESS_ID);
    }

    @Test
    @DisplayName("Can handle invalid StartAccountAndLoyaltyBankCreationCommand for non existing business")
    void testHandle_whenStartAccountAndLoyaltyBankCreationCommandHandledAndBusinessDoesNotExist_shouldThrowException() {
        // Arrange
        StartAccountAndLoyaltyBankCreationCommand command = StartAccountAndLoyaltyBankCreationCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        when(accountLookupRepository.findByEmail(TEST_EMAIL)).thenReturn(null);
        when(businessLookupRepository.findByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessNotFoundException.class, () -> {
            CommandMessage<?> result = sagaOrchestratorCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw BusinessNotFoundException");

        // Assert
        verify(accountLookupRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
    }
}