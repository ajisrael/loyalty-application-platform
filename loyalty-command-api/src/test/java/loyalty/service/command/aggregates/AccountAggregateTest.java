package loyalty.service.command.aggregates;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.command.commands.DeleteAccountCommand;
import loyalty.service.command.commands.UpdateAccountCommand;
import loyalty.service.command.commands.rollbacks.RollbackAccountCreationCommand;
import loyalty.service.core.events.account.*;
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
    private static final String TEST_NEW_LAST_NAME = "Doent";
    private static final String TEST_NEW_EMAIL = "new@email.com";

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
            .lastName(TEST_NEW_LAST_NAME)
            .email(TEST_NEW_EMAIL)
            .build();

    private static final UpdateAccountCommand updateAccountFirstNameCommand = UpdateAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .firstName(TEST_NEW_FIRST_NAME)
            .build();

    private static final UpdateAccountCommand updateAccountLastNameCommand = UpdateAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .lastName(TEST_NEW_LAST_NAME)
            .build();

    private static final UpdateAccountCommand updateAccountEmailCommand = UpdateAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .email(TEST_NEW_EMAIL)
            .build();

    private static final UpdateAccountCommand updateNoAccountAttributesCommand = UpdateAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .build();

    private static final AccountFirstNameChangedEvent accountFirstNameChangedEvent = AccountFirstNameChangedEvent.builder()
            .requestId(updateAccountCommand.getRequestId())
            .accountId(updateAccountCommand.getAccountId())
            .oldFirstName(TEST_FIRST_NAME)
            .newFirstName(updateAccountCommand.getFirstName())
            .build();

    private static final AccountLastNameChangedEvent accountLastNameChangedEvent = AccountLastNameChangedEvent.builder()
            .requestId(updateAccountCommand.getRequestId())
            .accountId(updateAccountCommand.getAccountId())
            .oldLastName(TEST_LAST_NAME)
            .newLastName(updateAccountCommand.getLastName())
            .build();

    private static final AccountEmailChangedEvent accountEmailChangedEvent = AccountEmailChangedEvent.builder()
            .requestId(updateAccountCommand.getRequestId())
            .accountId(updateAccountCommand.getAccountId())
            .oldEmail(TEST_EMAIL)
            .newEmail(updateAccountCommand.getEmail())
            .build();

    private static final DeleteAccountCommand deleteAccountCommand = DeleteAccountCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
            .build();

    private static final AccountDeletedEvent accountDeletedEvent = AccountDeletedEvent.builder()
            .requestId(deleteAccountCommand.getRequestId())
            .accountId(deleteAccountCommand.getAccountId())
            .build();

    private static final RollbackAccountCreationCommand rollbackAccountCreationCommand = RollbackAccountCreationCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .accountId(TEST_ACCOUNT_ID)
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
                .expectEvents(accountCreatedEvent)
                .expectState(state -> {
                    assertEquals(accountCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountCreatedEvent.getFirstName(), state.getFirstName(), "FirstNames should match");
                    assertEquals(accountCreatedEvent.getLastName(), state.getLastName(), "LastNames should match");
                    assertEquals(accountCreatedEvent.getEmail(), state.getEmail(), "Emails should match");
                });
    }

    @Test
    @DisplayName("Cannot create account that has already been created")
    void testAccountAggregate_whenCreateAccountCommandHandledWithPriorActivity_ShouldThrowException() {
        fixture.given(accountCreatedEvent)
                .when(createAccountCommand)
                .expectException(EventStoreException.class);
    }

    @Test
    @DisplayName("UpdateAccountCommand results in individual events for each change")
    void testUpdateAccount_whenUpdateAccountCommandHandled_ShouldIssueAccountUpdatedEvents() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(updateAccountCommand)
                .expectEvents(accountFirstNameChangedEvent, accountLastNameChangedEvent, accountEmailChangedEvent)
                .expectState(state -> {
                    assertEquals(accountFirstNameChangedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountFirstNameChangedEvent.getNewFirstName(), state.getFirstName(), "FirstNames should match");
                    assertEquals(accountFirstNameChangedEvent.getOldFirstName(), accountCreatedEvent.getFirstName(), "Old FirstName should match");
                    assertEquals(accountLastNameChangedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountLastNameChangedEvent.getNewLastName(), state.getLastName(), "LastNames should match");
                    assertEquals(accountLastNameChangedEvent.getOldLastName(), accountCreatedEvent.getLastName(), "Old LastName should match");
                    assertEquals(accountEmailChangedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountEmailChangedEvent.getNewEmail(), state.getEmail(), "Emails should match");
                    assertEquals(accountEmailChangedEvent.getOldEmail(), accountCreatedEvent.getEmail(), "Old Email should match");
                });
    }

    @Test
    @DisplayName("UpdateAccountCommand for first name results in AccountFirstNameChangedEvent")
    void testUpdateAccount_whenUpdateAccountCommandForFirstNameHandled_ShouldIssueAccountFirstNameChangedEvent() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(updateAccountFirstNameCommand)
                .expectEvents(accountFirstNameChangedEvent)
                .expectState(state -> {
                    assertEquals(accountFirstNameChangedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountFirstNameChangedEvent.getNewFirstName(), state.getFirstName(), "FirstNames should match");
                    assertEquals(accountFirstNameChangedEvent.getOldFirstName(), accountCreatedEvent.getFirstName(), "Old FirstName should match");
                    assertEquals(accountCreatedEvent.getLastName(), state.getLastName(), "LastName should not change");
                    assertEquals(accountCreatedEvent.getEmail(), state.getEmail(), "Email should not change");
                });
    }

    @Test
    @DisplayName("UpdateAccountCommand for first name results in AccountLastNameChangedEvent")
    void testUpdateAccount_whenUpdateAccountCommandForLastNameHandled_ShouldIssueAccountLastNameChangedEvent() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(updateAccountLastNameCommand)
                .expectEvents(accountLastNameChangedEvent)
                .expectState(state -> {
                    assertEquals(accountLastNameChangedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountLastNameChangedEvent.getNewLastName(), state.getLastName(), "LastNames should match");
                    assertEquals(accountLastNameChangedEvent.getOldLastName(), accountCreatedEvent.getLastName(), "Old LastName should match");
                    assertEquals(accountCreatedEvent.getFirstName(), state.getFirstName(), "FirstName should not change");
                    assertEquals(accountCreatedEvent.getEmail(), state.getEmail(), "Email should not change");
                });
    }

    @Test
    @DisplayName("UpdateAccountCommand for first name results in AccountEmailChangedEvent")
    void testUpdateAccount_whenUpdateAccountCommandForEmailHandled_ShouldIssueAccountEmailChangedEvent() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(updateAccountEmailCommand)
                .expectEvents(accountEmailChangedEvent)
                .expectState(state -> {
                    assertEquals(accountEmailChangedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(accountEmailChangedEvent.getNewEmail(), state.getEmail(), "Emails should match");
                    assertEquals(accountEmailChangedEvent.getOldEmail(), accountCreatedEvent.getEmail(), "Old Email should match");
                    assertEquals(accountCreatedEvent.getFirstName(), state.getFirstName(), "FirstName should not change");
                    assertEquals(accountCreatedEvent.getLastName(), state.getLastName(), "LastName should not change");
                });
    }

    @Test
    @DisplayName("UpdateAccountCommand with no changes results in no events")
    void testUpdateAccount_whenUpdateAccountCommandWithNoChangesHandled_ShouldNotIssueAnyEvents() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(updateNoAccountAttributesCommand)
                .expectEvents()
                .expectState(state -> {
                    assertEquals(accountCreatedEvent.getAccountId(), state.getAccountId(), "AccountId should not change");
                    assertEquals(accountCreatedEvent.getFirstName(), state.getFirstName(), "FirstName should not change");
                    assertEquals(accountCreatedEvent.getLastName(), state.getLastName(), "LastName should not change");
                    assertEquals(accountCreatedEvent.getEmail(), state.getEmail(), "Email should not change");
                });
    }


    @Test
    @DisplayName("Cannot update an account that hasn't been created")
    void testUpdateAccount_whenUpdateAccountCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(updateAccountCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("DeleteAccountCommand results in AccountDeletedEvent")
    void testDeleteAccount_whenDeleteAccountCommandHandled_ShouldIssueAccountDeletedEvent() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(deleteAccountCommand)
                .expectEvents(accountDeletedEvent)
                .expectMarkedDeleted();
    }

    @Test
    @DisplayName("Cannot delete an account that hasn't been created")
    void testDeleteAccount_whenDeleteAccountCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(deleteAccountCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("RollbackAccountCreationCommand results in AccountDeletedEvent")
    void testRollbackAccountCreation_whenRollbackAccountCreationCommandHandled_ShouldIssueAccountDeletedEvent() {
        // Arrange & Act & Assert
        fixture.given(accountCreatedEvent)
                .when(rollbackAccountCreationCommand)
                .expectEvents(accountDeletedEvent)
                .expectMarkedDeleted();
    }

    @Test
    @DisplayName("Cannot delete an account that hasn't been created")
    void testRollbackAccountCreation_whenRollbackAccountCreationCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(rollbackAccountCreationCommand)
                .expectException(AggregateNotFoundException.class);
    }
}