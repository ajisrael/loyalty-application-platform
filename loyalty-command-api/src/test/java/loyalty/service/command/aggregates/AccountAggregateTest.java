package loyalty.service.command.aggregates;

import loyalty.service.command.commands.CreateAccountCommand;
import loyalty.service.core.events.AccountCreatedEvent;
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
class AccountAggregateTest {

    private FixtureConfiguration<AccountAggregate> fixture;

    public static final String TEST_REQUEST_ID = "test-request-id";
    public static final String TEST_ACCOUNT_ID = "test-account-id";
    public static final String TEST_FIRST_NAME = "John";
    public static final String TEST_LAST_NAME = "Doe";
    public static final String TEST_EMAIL = "john.doe@test.com";

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
    }

    @Test
    @DisplayName("CreateAccountCommand results in AccountCreatedEvent")
    void testAccountAggregate_whenCreateAccountCommandHandledWithNoPriorActivity_ShouldIssueAccountCreatedEvent() {
        // Arrange
        CreateAccountCommand command = CreateAccountCommand.builder()
                .requestId(TEST_REQUEST_ID)
                .accountId(TEST_ACCOUNT_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .email(TEST_EMAIL)
                .build();

        AccountCreatedEvent event  = AccountCreatedEvent.builder()
                .requestId(command.getRequestId())
                .accountId(command.getAccountId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .build();

        // Act & Assert
        fixture.givenNoPriorActivity()
                .when(command)
                .expectEvents(event);
    }


}