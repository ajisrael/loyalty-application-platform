package loyalty.service.command.aggregates;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountAggregateTest {

    private FixtureConfiguration<AccountAggregate> fixture;

    private static final String TEST_REQUEST_ID = "test-request-id";
    private static final String TEST_ACCOUNT_ID = "test-account-id";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "john.doe@test.com";
    private static final String TEST_NEW_FIRST_NAME = "Johnathan";

    private static final CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .build();

    private static final AccountCreatedEvent accountCreatedEvent  = AccountCreatedEvent.builder()
            .requestId(createAccountCommand.getRequestId())
            .accountId(createAccountCommand.getAccountId())
            .firstName(createAccountCommand.getFirstName())
            .lastName(createAccountCommand.getLastName())
            .email(createAccountCommand.getEmail())
            .build();

    private static final UpdateAccountCommand updateAccountCommand = UpdateAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .firstName(TEST_NEW_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .build();

    private static final AccountUpdatedEvent accountUpdatedEvent = AccountUpdatedEvent.builder()
            .requestId(updateAccountCommand.getRequestId())
            .accountId(updateAccountCommand.getAccountId())
            .firstName(updateAccountCommand.getFirstName())
            .lastName(updateAccountCommand.getLastName())
            .email(updateAccountCommand.getEmail())
            .build();

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
    }

    @Test
    @DisplayName("CreateAccountCommand results in AccountCreatedEvent")
    void testAccountAggregate_whenCreateAccountCommandHandledWithNoPriorActivity_ShouldIssueAccountCreatedEvent() {
        // Act & Assert
        fixture.givenNoPriorActivity()
                .when(createAccountCommand)
                .expectEvents(accountCreatedEvent);
    }

    @Test
    @DisplayName("CreateAccountCommand doesn't process on an existing AccountAggregate")
    void testAccountAggregate_whenCreateAccountCommandHandledWithPriorActivity_ShouldThrowException() {
        fixture.given(accountCreatedEvent)
                .when(createAccountCommand)
                .expectException(EventStoreException.class);
    }

    @Test
    @DisplayName("UpdateAccountCommand results in AccountUpdatedEvent")
    void testUpdateAccount_whenUpdateAccountCommandHandled_ShouldIssueAccountUpdatedEvent() {
        // Act & Assert
        fixture.given(accountCreatedEvent)
                .when(updateAccountCommand)
                .expectEvents(accountUpdatedEvent);
    }

    @Test
    @DisplayName("Cannot update an acount that hasn't been created")
    void testUpdateAccount_whenUpdateAccountCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(updateAccountCommand)
                .expectException(AggregateNotFoundException.class);
    }
}