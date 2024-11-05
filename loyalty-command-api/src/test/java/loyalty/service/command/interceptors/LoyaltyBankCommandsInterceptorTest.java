package loyalty.service.command.interceptors;

import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.entities.BusinessLookupEntity;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.command.data.repositories.BusinessLookupRepository;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.exceptions.AccountExistsWithLoyaltyBankException;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.exceptions.BusinessNotFoundException;
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
class LoyaltyBankCommandsInterceptorTest {

    @Mock
    private AccountLookupRepository accountLookupRepository;
    @Mock
    private BusinessLookupRepository businessLookupRepository;
    @Mock
    private LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    @InjectMocks
    LoyaltyBankCommandsInterceptor loyaltyBankCommandsInterceptor;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("Can handle valid CreateLoyaltyBankCommand")
    void testHandle_whenCreateLoyaltyBankCommandHandledAndCommandIsValid_shouldReturnCommand() {
        // Arrange
        CreateLoyaltyBankCommand command = CreateLoyaltyBankCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        AccountLookupEntity accountLookupEntity = new AccountLookupEntity();
        accountLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        accountLookupEntity.setEmail("test@test.com");

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(accountLookupEntity);

        BusinessLookupEntity businessLookupEntity = new BusinessLookupEntity();
        businessLookupEntity.setBusinessId(TEST_BUSINESS_ID);

        when(businessLookupRepository.findByBusinessId(TEST_BUSINESS_ID)).thenReturn(businessLookupEntity);

        when(loyaltyBankLookupRepository.findByBusinessIdAndAccountId(TEST_BUSINESS_ID, TEST_ACCOUNT_ID)).thenReturn(null);

        // Act
        CommandMessage<?> result = loyaltyBankCommandsInterceptor.handle(
                List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
        );

        // Assert
        assertEquals(command, result.getPayload(), "Command should not be changed by interceptor");
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
        verify(loyaltyBankLookupRepository, times(1)).findByBusinessIdAndAccountId(TEST_BUSINESS_ID, TEST_ACCOUNT_ID);
    }

    @Test
    @DisplayName("Can handle invalid CreateLoyaltyBankCommand for non existing account")
    void testHandle_whenCreateLoyaltyBankCommandHandledAndAccountDoesNotExist_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand command = CreateLoyaltyBankCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            CommandMessage<?> result = loyaltyBankCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw AccountNotFoundException");

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(businessLookupRepository, times(0)).findByBusinessId(TEST_BUSINESS_ID);
        verify(loyaltyBankLookupRepository, times(0)).findByBusinessIdAndAccountId(TEST_BUSINESS_ID, TEST_ACCOUNT_ID);
    }

    @Test
    @DisplayName("Can handle invalid CreateLoyaltyBankCommand for non existing business")
    void testHandle_whenCreateLoyaltyBankCommandHandledAndBusinessDoesNotExist_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand command = CreateLoyaltyBankCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        AccountLookupEntity accountLookupEntity = new AccountLookupEntity();
        accountLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        accountLookupEntity.setEmail("test@test.com");

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(accountLookupEntity);

        when(businessLookupRepository.findByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessNotFoundException.class, () -> {
            CommandMessage<?> result = loyaltyBankCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw BusinessNotFoundException");

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
        verify(loyaltyBankLookupRepository, times(0)).findByBusinessIdAndAccountId(TEST_BUSINESS_ID, TEST_ACCOUNT_ID);
    }

    @Test
    @DisplayName("Can handle invalid CreateLoyaltyBankCommand for existing loyalty bank for account and business")
    void testHandle_whenCreateLoyaltyBankCommandHandledAndLoyaltyBankExistsForAccountAndBusiness_shouldThrowException() {
        // Arrange
        CreateLoyaltyBankCommand command = CreateLoyaltyBankCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                .accountId(TEST_ACCOUNT_ID)
                .businessId(TEST_BUSINESS_ID)
                .build();

        AccountLookupEntity accountLookupEntity = new AccountLookupEntity();
        accountLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        accountLookupEntity.setEmail("test@test.com");

        when(accountLookupRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(accountLookupEntity);

        BusinessLookupEntity businessLookupEntity = new BusinessLookupEntity();
        businessLookupEntity.setBusinessId(TEST_BUSINESS_ID);

        when(businessLookupRepository.findByBusinessId(TEST_BUSINESS_ID)).thenReturn(businessLookupEntity);

        LoyaltyBankLookupEntity loyaltyBankLookupEntity = new LoyaltyBankLookupEntity();
        loyaltyBankLookupEntity.setLoyaltyBankId(UUID.randomUUID().toString());
        loyaltyBankLookupEntity.setAccountId(TEST_ACCOUNT_ID);
        loyaltyBankLookupEntity.setBusinessId(TEST_BUSINESS_ID);

        when(loyaltyBankLookupRepository.findByBusinessIdAndAccountId(TEST_BUSINESS_ID, TEST_ACCOUNT_ID)).thenReturn(loyaltyBankLookupEntity);

        // Act & Assert
        assertThrows(AccountExistsWithLoyaltyBankException.class, () -> {
            CommandMessage<?> result = loyaltyBankCommandsInterceptor.handle(
                    List.of(new GenericCommandMessage<>(command))).apply(0, new GenericCommandMessage<>(command)
            );
        }, "Should throw AccountExistsWithLoyaltyBankException");

        // Assert
        verify(accountLookupRepository, times(1)).findByAccountId(TEST_ACCOUNT_ID);
        verify(businessLookupRepository, times(1)).findByBusinessId(TEST_BUSINESS_ID);
        verify(loyaltyBankLookupRepository, times(1)).findByBusinessIdAndAccountId(TEST_BUSINESS_ID, TEST_ACCOUNT_ID);
    }
}