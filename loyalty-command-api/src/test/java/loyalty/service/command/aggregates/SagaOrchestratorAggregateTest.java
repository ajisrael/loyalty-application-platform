package loyalty.service.command.aggregates;

import loyalty.service.command.commands.EndAccountAndLoyaltyBankCreationCommand;
import loyalty.service.command.commands.StartAccountAndLoyaltyBankCreationCommand;
import loyalty.service.core.events.AccountAndLoyaltyBankCreationEndedEvent;
import loyalty.service.core.events.AccountAndLoyaltyBankCreationStartedEvent;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SagaOrchestratorAggregateTest {

    private FixtureConfiguration<SagaOrchestratorAggregate> fixture;

    private static final String TEST_REQUEST_ID = UUID.randomUUID().toString();
    private static final String TEST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_LOYALTY_BANK_ID = UUID.randomUUID().toString();
    private static final String TEST_BUSINESS_ID = UUID.randomUUID().toString();

    private static final StartAccountAndLoyaltyBankCreationCommand startAccountAndLoyaltyBankCreationCommand =
            StartAccountAndLoyaltyBankCreationCommand.builder()
                    .requestId(TEST_REQUEST_ID)
                    .accountId(TEST_ACCOUNT_ID)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .email(TEST_EMAIL)
                    .loyaltyBankId(TEST_LOYALTY_BANK_ID)
                    .businessId(TEST_BUSINESS_ID)
                    .build();

    private static final AccountAndLoyaltyBankCreationStartedEvent accountAndLoyaltyBankCreationStartedEvent =
            AccountAndLoyaltyBankCreationStartedEvent.builder()
                    .requestId(startAccountAndLoyaltyBankCreationCommand.getRequestId())
                    .accountId(startAccountAndLoyaltyBankCreationCommand.getAccountId())
                    .firstName(startAccountAndLoyaltyBankCreationCommand.getFirstName())
                    .lastName(startAccountAndLoyaltyBankCreationCommand.getLastName())
                    .email(startAccountAndLoyaltyBankCreationCommand.getEmail())
                    .loyaltyBankId(startAccountAndLoyaltyBankCreationCommand.getLoyaltyBankId())
                    .businessId(startAccountAndLoyaltyBankCreationCommand.getBusinessId())
                    .build();

    private static final EndAccountAndLoyaltyBankCreationCommand endAccountAndLoyaltyBankCreationCommand =
            EndAccountAndLoyaltyBankCreationCommand.builder()
                    .requestId(TEST_REQUEST_ID)
                    .build();

    private static AccountAndLoyaltyBankCreationEndedEvent accountAndLoyaltyBankCreationEndedEvent =
            AccountAndLoyaltyBankCreationEndedEvent.builder()
                    .requestId(endAccountAndLoyaltyBankCreationCommand.getRequestId())
                    .build();

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(SagaOrchestratorAggregate.class);
    }

    @Test
    @DisplayName("StartAccountAndLoyaltyBankCreationCommand results in AccountAndLoyaltyBankCreationStartedEvent")
    void testAccountAggregate_whenStartAccountAndLoyaltyBankCreationCommandHandledWithNoPriorActivity_ShouldIssueAccountAndLoyaltyBankCreationStartedEvent() {
        // Act & Assert
        fixture.givenNoPriorActivity()
                .when(startAccountAndLoyaltyBankCreationCommand)
                .expectEvents(accountAndLoyaltyBankCreationStartedEvent)
                .expectState(state -> {
                    assertEquals(accountAndLoyaltyBankCreationStartedEvent.getRequestId(), state.getRequestId(), "RequestIds should match");
                });
    }

    @Test
    @DisplayName("Cannot start creation for an account and loyalty bank that has already been started")
    void testAccountAggregate_whenStartAccountAndLoyaltyBankCreationCommandHandledWithPriorActivity_ShouldThrowException() {
        fixture.given(accountAndLoyaltyBankCreationStartedEvent)
                .when(startAccountAndLoyaltyBankCreationCommand)
                .expectException(EventStoreException.class);
    }

    @Test
    @DisplayName("EndAccountAndLoyaltyBankCreationCommand results in AccountAndLoyaltyBankCreationEndedEvent")
    void testDeleteAccount_whenEndAccountAndLoyaltyBankCreationCommandHandled_ShouldIssueAccountAndLoyaltyBankCreationEndedEvent() {
        // Arrange & Act & Assert
        fixture.given(accountAndLoyaltyBankCreationStartedEvent)
                .when(endAccountAndLoyaltyBankCreationCommand)
                .expectEvents(accountAndLoyaltyBankCreationEndedEvent)
                .expectMarkedDeleted();
    }

    @Test
    @DisplayName("Cannot end creation of an account and loyalty bank that hasn't started")
    void testDeleteAccount_whenEndAccountAndLoyaltyBankCreationCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(endAccountAndLoyaltyBankCreationCommand)
                .expectException(AggregateNotFoundException.class);
    }
}