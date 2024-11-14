package loyalty.service.command.aggregates;

import loyalty.service.command.commands.CreateLoyaltyBankCommand;
import loyalty.service.command.commands.DeleteLoyaltyBankCommand;
import loyalty.service.command.commands.ExpireAllPointsCommand;
import loyalty.service.command.commands.rollbacks.RollbackLoyaltyBankCreationCommand;
import loyalty.service.command.commands.transactions.*;
import loyalty.service.core.events.AllPointsExpiredEvent;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.transactions.*;
import loyalty.service.core.exceptions.IllegalLoyaltyBankStateException;
import loyalty.service.core.exceptions.InsufficientPointsException;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static loyalty.service.core.constants.DomainConstants.*;
import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyBankAggregateTest {

    private FixtureConfiguration<LoyaltyBankAggregate> fixture;

    private static final String TEST_REQUEST_ID = "test-request-id";
    private static final String TEST_LOYALTY_BANK_ID = "test-loyalty-bank-id";
    private static final String TEST_ACCOUNT_ID = "test-account-id";
    private static final String TEST_BUSINESS_ID = "test-business-id";
    private static final String TEST_PAYMENT_ID = "test-business-id";
    private static final int TEST_PENDING_POINTS = 100;
    private static final int TEST_EARNED_POINTS = 100;
    private static final int TEST_AWARD_POINTS = 100;
    private static final int TEST_AUTHORIZED_POINTS = 100;
    private static final int TEST_VOID_POINTS = 100;
    private static final int TEST_CAPTURED_POINTS = 100;
    private static final int TEST_EXPIRE_POINTS = 50;

    private static final CreateLoyaltyBankCommand createLoyaltyBankCommand = CreateLoyaltyBankCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .accountId(TEST_ACCOUNT_ID)
            .businessId(TEST_BUSINESS_ID)
            .build();

    private static final LoyaltyBankCreatedEvent loyaltyBankCreatedEvent = LoyaltyBankCreatedEvent.builder()
            .requestId(createLoyaltyBankCommand.getRequestId())
            .loyaltyBankId(createLoyaltyBankCommand.getLoyaltyBankId())
            .accountId(createLoyaltyBankCommand.getAccountId())
            .businessId(createLoyaltyBankCommand.getBusinessId())
            .build();

    private static final CreatePendingTransactionCommand createPendingTransactionCommand = CreatePendingTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .points(TEST_PENDING_POINTS)
            .build();

    private static final CreatePendingTransactionCommand createNegativePendingTransactionCommand = CreatePendingTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .points(TEST_PENDING_POINTS * -1)
            .build();

    private static final PendingTransactionCreatedEvent pendingTransactionCreatedEvent = PendingTransactionCreatedEvent.builder()
            .requestId(createPendingTransactionCommand.getRequestId())
            .loyaltyBankId(createPendingTransactionCommand.getLoyaltyBankId())
            .points(createPendingTransactionCommand.getPoints())
            .build();

    private static final CreateEarnedTransactionCommand createEarnedTransactionCommand = CreateEarnedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .points(TEST_EARNED_POINTS)
            .build();

    private static final CreateEarnedTransactionCommand createNegativeEarnedTransactionCommand = CreateEarnedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .points(TEST_EARNED_POINTS * -1)
            .build();

    private static final EarnedTransactionCreatedEvent earnedTransactionCreatedEvent = EarnedTransactionCreatedEvent.builder()
            .requestId(createEarnedTransactionCommand.getRequestId())
            .loyaltyBankId(createEarnedTransactionCommand.getLoyaltyBankId())
            .points(createEarnedTransactionCommand.getPoints())
            .build();

    private static final CreateAwardedTransactionCommand createAwardedTransactionCommand = CreateAwardedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .points(TEST_AWARD_POINTS)
            .build();

    private static final CreateAwardedTransactionCommand createNegativeAwardedTransactionCommand = CreateAwardedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .points(TEST_AWARD_POINTS * -1)
            .build();

    private static final AwardedTransactionCreatedEvent awardedTransactionCreatedEvent = AwardedTransactionCreatedEvent.builder()
            .requestId(createAwardedTransactionCommand.getRequestId())
            .loyaltyBankId(createAwardedTransactionCommand.getLoyaltyBankId())
            .points(createAwardedTransactionCommand.getPoints())
            .build();

    private static final CreateAuthorizedTransactionCommand createAuthorizedTransactionCommand = CreateAuthorizedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .paymentId(TEST_PAYMENT_ID)
            .points(TEST_AUTHORIZED_POINTS)
            .build();

    private static final CreateAuthorizedTransactionCommand createNegativeAuthorizedTransactionCommand = CreateAuthorizedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .paymentId(TEST_PAYMENT_ID)
            .points(TEST_AUTHORIZED_POINTS * -1)
            .build();

    private static final AuthorizedTransactionCreatedEvent authorizedTransactionCreatedEvent = AuthorizedTransactionCreatedEvent.builder()
            .requestId(createAuthorizedTransactionCommand.getRequestId())
            .loyaltyBankId(createAuthorizedTransactionCommand.getLoyaltyBankId())
            .paymentId(createAuthorizedTransactionCommand.getPaymentId())
            .points(createAuthorizedTransactionCommand.getPoints())
            .build();

    private static final CreateVoidTransactionCommand createVoidTransactionCommand = CreateVoidTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .paymentId(TEST_PAYMENT_ID)
            .points(TEST_VOID_POINTS)
            .build();

    private static final VoidTransactionCreatedEvent voidTransactionCreatedEvent = VoidTransactionCreatedEvent.builder()
            .requestId(createVoidTransactionCommand.getRequestId())
            .loyaltyBankId(createVoidTransactionCommand.getLoyaltyBankId())
            .paymentId(createVoidTransactionCommand.getPaymentId())
            .points(createVoidTransactionCommand.getPoints())
            .build();

    private static final CreateCapturedTransactionCommand createCapturedTransactionCommand = CreateCapturedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .paymentId(TEST_PAYMENT_ID)
            .points(TEST_CAPTURED_POINTS)
            .build();

    private static final CreateCapturedTransactionCommand createNegativeCapturedTransactionCommand = CreateCapturedTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .paymentId(TEST_PAYMENT_ID)
            .points(TEST_CAPTURED_POINTS * -1)
            .build();

    private static final CapturedTransactionCreatedEvent capturedTransactionCreatedEvent = CapturedTransactionCreatedEvent.builder()
            .requestId(createCapturedTransactionCommand.getRequestId())
            .loyaltyBankId(createCapturedTransactionCommand.getLoyaltyBankId())
            .paymentId(createCapturedTransactionCommand.getPaymentId())
            .points(createCapturedTransactionCommand.getPoints())
            .build();

    private static final CreateExpirePointsTransactionCommand createExpirePointsTransactionCommand = CreateExpirePointsTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .targetTransactionId(TEST_REQUEST_ID)
            .points(TEST_EXPIRE_POINTS)
            .build();

    private static final CreateExpirePointsTransactionCommand createNegativeExpirePointsTransactionCommand = CreateExpirePointsTransactionCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .targetTransactionId(TEST_REQUEST_ID)
            .points(TEST_EXPIRE_POINTS * -1)
            .build();

    private static final ExpiredTransactionCreatedEvent expiredTransactionCreatedEvent = ExpiredTransactionCreatedEvent.builder()
            .requestId(createExpirePointsTransactionCommand.getRequestId())
            .loyaltyBankId(createExpirePointsTransactionCommand.getLoyaltyBankId())
            .targetTransactionId(createExpirePointsTransactionCommand.getTargetTransactionId())
            .points(createExpirePointsTransactionCommand.getPoints())
            .build();

    private static final ExpireAllPointsCommand expireAllPointsCommand = ExpireAllPointsCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .build();

    private static final DeleteLoyaltyBankCommand deleteLoyaltyBankCommand = DeleteLoyaltyBankCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .build();

    private static final LoyaltyBankDeletedEvent loyaltyBankDeletedEvent = LoyaltyBankDeletedEvent.builder()
            .requestId(deleteLoyaltyBankCommand.getRequestId())
            .loyaltyBankId(deleteLoyaltyBankCommand.getLoyaltyBankId())
            .accountId(TEST_ACCOUNT_ID)
            .businessId(TEST_BUSINESS_ID)
            .build();

    private static final RollbackLoyaltyBankCreationCommand rollbackLoyaltyBankCreationCommand = RollbackLoyaltyBankCreationCommand.builder()
            .requestId(TEST_REQUEST_ID)
            .loyaltyBankId(TEST_LOYALTY_BANK_ID)
            .build();

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(LoyaltyBankAggregate.class);
    }

    @Test
    @DisplayName("CreateLoyaltyBankCommand results in LoyaltyBankCreatedEvent")
    void testLoyaltyBankAggregate_whenCreateLoyaltyBankCommandHandledWithNoPriorActivity_shouldIssueLoyaltyBankCreatedEvent() {
        // Act & Assert
        fixture.givenNoPriorActivity()
                .when(createLoyaltyBankCommand)
                .expectEvents(loyaltyBankCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(0, state.getEarned(), "Earned should be 0");
                    assertEquals(0, state.getAuthorized(), "Authorized should be 0");
                    assertEquals(0, state.getCaptured(), "Captured should be 0");
                    assertEquals(0, state.getAvailablePoints(), "Available points should be 0");
                });
    }

    @Test
    @DisplayName("Cannot create loyaltyBank that has already been created")
    void testLoyaltyBankAggregate_whenCreateLoyaltyBankCommandHandledWithPriorActivity_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createLoyaltyBankCommand)
                .expectException(EventStoreException.class);
    }

    @Test
    @DisplayName("CreatePendingTransactionCommand results in PendingTransactionCreatedEvent")
    void testCreatePendingTransaction_whenCreatePendingTransactionCommandHandled_shouldIssuePendingTransactionCreatedEvent() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createPendingTransactionCommand)
                .expectEvents(pendingTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(pendingTransactionCreatedEvent.getPoints(), state.getPending(), "Pending points were not what was expected");
                    assertEquals(0, state.getEarned(), "Earned should be 0");
                    assertEquals(0, state.getAuthorized(), "Authorized should be 0");
                    assertEquals(0, state.getCaptured(), "Captured should be 0");
                    assertEquals(0, state.getAvailablePoints(), "Available points should be 0");
                });
    }

    @Test
    @DisplayName("Cannot create pending transaction that would make balance negative")
    void testCreatePendingTransaction_whenCreatePendingTransactionCommandWouldPutPendingBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createNegativePendingTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create pending transaction for a loyaltyBank that hasn't been created")
    void testCreatePendingTransaction_whenCreatePendingTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createPendingTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("CreateEarnedTransactionCommand results in EarnedTransactionCreatedEvent")
    void testCreateEarnedTransaction_whenCreateEarnedTransactionCommandHandled_shouldIssueEarnedTransactionCreatedEvent() {
        int expectedPendingPoints = pendingTransactionCreatedEvent.getPoints() - earnedTransactionCreatedEvent.getPoints();
        int expectedAvailablePoints = earnedTransactionCreatedEvent.getPoints();

        fixture.given(loyaltyBankCreatedEvent, pendingTransactionCreatedEvent)
                .when(createEarnedTransactionCommand)
                .expectEvents(earnedTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(expectedPendingPoints, state.getPending(), "Pending points were not what was expected");
                    assertEquals(earnedTransactionCreatedEvent.getPoints(), state.getEarned(), "Earned points were not what was expected");
                    assertEquals(0, state.getAuthorized(), "Authorized should be 0");
                    assertEquals(0, state.getCaptured(), "Captured should be 0");
                    assertEquals(expectedAvailablePoints, state.getAvailablePoints(), "Available points were not what was expected");
                });
    }

    @Test
    @DisplayName("Cannot create earned transaction with insufficient pending points")
    void testCreateEarnedTransaction_whenCreateEarnedTransactionCommandWouldPutPendingBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createEarnedTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, PENDING))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create earned transaction that would make earned balance negative")
    void testCreateEarnedTransaction_whenCreateEarnedTransactionCommandWouldPutEarnedBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent, createPendingTransactionCommand)
                .when(createNegativeEarnedTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, EARNED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create earned transaction for a loyaltyBank that hasn't been created")
    void testCreateEarnedTransaction_whenCreateEarnedTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createEarnedTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("CreateAwardedTransactionCommand results in AwardedTransactionCreatedEvent")
    void testCreateAwardedTransaction_whenCreateAwardedTransactionCommandHandled_shouldIssueAwardedTransactionCreatedEvent() {
        int expectedAvailablePoints = awardedTransactionCreatedEvent.getPoints();

        fixture.given(loyaltyBankCreatedEvent)
                .when(createAwardedTransactionCommand)
                .expectEvents(awardedTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(awardedTransactionCreatedEvent.getPoints(), state.getEarned(), "Earned points were not what was expected");
                    assertEquals(0, state.getAuthorized(), "Authorized should be 0");
                    assertEquals(0, state.getCaptured(), "Captured should be 0");
                    assertEquals(expectedAvailablePoints, state.getAvailablePoints(), "Available points were not what was expected");
                });
    }

    @Test
    @DisplayName("Cannot create award transaction that would make earned balance negative")
    void testCreateAwardedTransaction_whenCreateAwardedTransactionCommandWouldPutEarnedBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createNegativeAwardedTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, EARNED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create award transaction for a loyaltyBank that hasn't been created")
    void testCreateAwardedTransaction_whenCreateAwardedTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createAwardedTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("CreateAuthorizedTransactionCommand results in AuthorizedTransactionCreatedEvent")
    void testCreateAuthorizedTransaction_whenCreateAuthorizedTransactionCommandHandled_shouldIssueAuthorizedTransactionCreatedEvent() {
        int expectedAvailablePoints = awardedTransactionCreatedEvent.getPoints() - authorizedTransactionCreatedEvent.getPoints();

        fixture.given(loyaltyBankCreatedEvent, awardedTransactionCreatedEvent)
                .when(createAuthorizedTransactionCommand)
                .expectEvents(authorizedTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(awardedTransactionCreatedEvent.getPoints(), state.getEarned(), "Earned points were not what was expected");
                    assertEquals(authorizedTransactionCreatedEvent.getPoints(), state.getAuthorized(), "Authorized points were not what was expected");
                    assertEquals(0, state.getCaptured(), "Captured should be 0");
                    assertEquals(expectedAvailablePoints, state.getAvailablePoints(), "Available points were not what was expected");
                });
    }

    @Test
    @DisplayName("Cannot create authorize transaction that would make authorized balance negative")
    void testCreateAuthorizedTransaction_whenCreateAuthorizedTransactionCommandHandledWouldMakeAuthorizedBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createNegativeAuthorizedTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, AUTHORIZED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create authorize transaction for more points than available")
    void testCreateAuthorizedTransaction_whenCreateAuthorizedTransactionCommandHandledForMorePointsThanAvailable_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createAuthorizedTransactionCommand)
                .expectException(InsufficientPointsException.class)
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create authorize transaction for a loyaltyBank that hasn't been created")
    void testCreateAuthorizedTransaction_whenCreateAuthorizedTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createAuthorizedTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("CreateVoidTransactionCommand results in VoidTransactionCreatedEvent")
    void testCreateVoidTransaction_whenCreateVoidTransactionCommandHandled_shouldIssueVoidTransactionCreatedEvent() {
        int expectedAuthorizedPoints = authorizedTransactionCreatedEvent.getPoints() - voidTransactionCreatedEvent.getPoints();
        int expectedAvailablePoints = awardedTransactionCreatedEvent.getPoints() - expectedAuthorizedPoints;

        fixture.given(loyaltyBankCreatedEvent, awardedTransactionCreatedEvent, authorizedTransactionCreatedEvent)
                .when(createVoidTransactionCommand)
                .expectEvents(voidTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(awardedTransactionCreatedEvent.getPoints(), state.getEarned(), "Earned points were not what was expected");
                    assertEquals(expectedAuthorizedPoints, state.getAuthorized(), "Authorized points were not what was expected");
                    assertEquals(0, state.getCaptured(), "Captured should be 0");
                    assertEquals(expectedAvailablePoints, state.getAvailablePoints(), "Available points were not what was expected");
                });
    }

    @Test
    @DisplayName("Cannot create void transaction that would make authorized balance negative")
    void testCreateVoidTransaction_whenCreateVoidTransactionCommandHandledWouldMakeVoidBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createVoidTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, AUTHORIZED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create void transaction for a loyaltyBank that hasn't been created")
    void testCreateVoidTransaction_whenCreateVoidTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createVoidTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("CreateCapturedTransactionCommand results in CapturedTransactionCreatedEvent")
    void testCreateCapturedTransaction_whenCreateCapturedTransactionCommandHandled_shouldIssueCapturedTransactionCreatedEvent() {
        int expectedAuthorizedPoints = authorizedTransactionCreatedEvent.getPoints() - capturedTransactionCreatedEvent.getPoints();
        int expectedAvailablePoints = awardedTransactionCreatedEvent.getPoints() - expectedAuthorizedPoints - capturedTransactionCreatedEvent.getPoints();

        fixture.given(loyaltyBankCreatedEvent, awardedTransactionCreatedEvent, authorizedTransactionCreatedEvent)
                .when(createCapturedTransactionCommand)
                .expectEvents(capturedTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(awardedTransactionCreatedEvent.getPoints(), state.getEarned(), "Earned points were not what was expected");
                    assertEquals(expectedAuthorizedPoints, state.getAuthorized(), "Authorized points were not what was expected");
                    assertEquals(capturedTransactionCreatedEvent.getPoints(), state.getCaptured(), "Captured points were not what was expected");
                    assertEquals(expectedAvailablePoints, state.getAvailablePoints(), "Available points were not what was expected");
                });
    }

    @Test
    @DisplayName("Cannot create capture transaction that would make authorized balance negative")
    void testCreateCapturedTransaction_whenCreateCapturedTransactionCommandHandledWouldMakeAuthorizedBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createCapturedTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, AUTHORIZED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create capture transaction that would make captured balance negative")
    void testCreateCapturedTransaction_whenCreateCapturedTransactionCommandHandledWouldMakeCapturedBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent, awardedTransactionCreatedEvent, authorizedTransactionCreatedEvent)
                .when(createNegativeCapturedTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, CAPTURED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create capture transaction for a loyaltyBank that hasn't been created")
    void testCreateCapturedTransaction_whenCreateCapturedTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createCapturedTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("CreateExpirePointsTransactionCommand results in ExpiredTransactionCreatedEvent")
    void testCreateExpirePointsTransaction_whenCreateExpirePointsTransactionCommandHandled_shouldIssueExpiredTransactionCreatedEvent() {
        int expectedEarnedPoints = awardedTransactionCreatedEvent.getPoints() * 2; // two award events given
        int expectedAuthorizedPoints = authorizedTransactionCreatedEvent.getPoints() - capturedTransactionCreatedEvent.getPoints();
        int expectedCapturedPoints = capturedTransactionCreatedEvent.getPoints() + expiredTransactionCreatedEvent.getPoints();
        int expectedAvailablePoints = expectedEarnedPoints - expectedAuthorizedPoints - expectedCapturedPoints;

        fixture.given(
                        loyaltyBankCreatedEvent,
                        awardedTransactionCreatedEvent,
                        authorizedTransactionCreatedEvent,
                        capturedTransactionCreatedEvent,
                        awardedTransactionCreatedEvent // second award transaction makes sure there is points available to expire
                )
                .when(createExpirePointsTransactionCommand)
                .expectEvents(expiredTransactionCreatedEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(expectedEarnedPoints, state.getEarned(), "Earned points were not what was expected");
                    assertEquals(expectedAuthorizedPoints, state.getAuthorized(), "Authorized points were not what was expected");
                    assertEquals(expectedCapturedPoints, state.getCaptured(), "Captured points were not what was expected");
                    assertEquals(expectedAvailablePoints, state.getAvailablePoints(), "Available points were not what was expected");
                });
    }

    @Test
    @DisplayName("Cannot create expire transaction that would make captured balance negative")
    void testCreateExpirePointsTransaction_whenCreateExpirePointsTransactionCommandHandledWouldMakeCapturedPointsBalanceNegative_shouldThrowException() {
        fixture.given(loyaltyBankCreatedEvent)
                .when(createNegativeExpirePointsTransactionCommand)
                .expectException(IllegalLoyaltyBankStateException.class)
                .expectExceptionMessage(String.format(LOYALTY_BANK_PROPERTY_BALANCE_CANNOT_BE_NEGATIVE, CAPTURED))
                .expectNoEvents();
    }

    @Test
    @DisplayName("Cannot create expire transaction for a loyaltyBank that hasn't been created")
    void testCreateExpirePointsTransaction_whenCreateExpirePointsTransactionCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(createExpirePointsTransactionCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("ExpireAllPointsCommand results in ExpiredTransactionCreatedEvent")
    void testExpireAllPoints_whenExpireAllPointsCommandHandled_shouldIssueAllPointsExpiredEvent() {
        // Arrange
        int expectedEarnedPoints = awardedTransactionCreatedEvent.getPoints() * 2;

        AllPointsExpiredEvent allPointsExpiredEvent = AllPointsExpiredEvent.builder()
                .requestId(expireAllPointsCommand.getRequestId())
                .loyaltyBankId(expireAllPointsCommand.getLoyaltyBankId())
                .accountId(loyaltyBankCreatedEvent.getAccountId())
                .businessId(loyaltyBankCreatedEvent.getBusinessId())
                .pendingPointsRemoved(pendingTransactionCreatedEvent.getPoints())
                .authorizedPointsVoided(authorizedTransactionCreatedEvent.getPoints())
                .pointsExpired(expectedEarnedPoints)
                .build();

        int expectedCapturedPoints = allPointsExpiredEvent.getPointsExpired();

        // Arrange & Act & Assert
        fixture.given(
                        loyaltyBankCreatedEvent,
                        awardedTransactionCreatedEvent,
                        authorizedTransactionCreatedEvent,  // award and auth transactions makes sure there is points available to "void"
                        awardedTransactionCreatedEvent,     // second award transaction makes sure there are points available to expire
                        pendingTransactionCreatedEvent
                )
                .when(expireAllPointsCommand)
                .expectEvents(allPointsExpiredEvent)
                .expectState(state -> {
                    assertEquals(loyaltyBankCreatedEvent.getLoyaltyBankId(), state.getLoyaltyBankId(), "LoyaltyBankIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getAccountId(), state.getAccountId(), "AccountIds should match");
                    assertEquals(loyaltyBankCreatedEvent.getBusinessId(), state.getBusinessId(), "BusinessIds should match");
                    assertEquals(0, state.getPending(), "Pending should be 0");
                    assertEquals(expectedEarnedPoints, state.getEarned(), "Earned points were not what was expected");
                    assertEquals(0, state.getAuthorized(), "Authorized should be 0");
                    assertEquals(expectedCapturedPoints, state.getCaptured(), "Captured points were not what was expected");
                    assertEquals(0, state.getAvailablePoints(), "Available points should be 0");
                });
    }

    @Test
    @DisplayName("Cannot expire all points for a loyaltyBank that hasn't been created")
    void testExpireAllPoints_whenExpireAllPointsCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(expireAllPointsCommand)
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    @DisplayName("DeleteLoyaltyBankCommand results in LoyaltyBankDeletedEvent")
    void testDeleteLoyaltyBank_whenDeleteLoyaltyBankCommandHandled_shouldIssueLoyaltyBankDeletedEvent() {
        // Arrange & Act & Assert
        fixture.given(loyaltyBankCreatedEvent)
                .when(deleteLoyaltyBankCommand)
                .expectEvents(loyaltyBankDeletedEvent)
                .expectMarkedDeleted();
    }

    @Test
    @DisplayName("Cannot delete an loyaltyBank that hasn't been created")
    void testDeleteLoyaltyBank_whenDeleteLoyaltyBankCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(deleteLoyaltyBankCommand)
                .expectException(AggregateNotFoundException.class);
    }


    @Test
    @DisplayName("RollbackLoyaltyBankCreationCommand results in LoyaltyBankDeletedEvent")
    void testRollbackLoyaltyBankCreation_whenRollbackLoyaltyBankCreationCommandHandled_shouldIssueLoyaltyBankDeletedEvent() {
        // Arrange & Act & Assert
        fixture.given(loyaltyBankCreatedEvent)
                .when(rollbackLoyaltyBankCreationCommand)
                .expectEvents(loyaltyBankDeletedEvent)
                .expectMarkedDeleted();
    }

    @Test
    @DisplayName("Cannot delete an loyaltyBank that hasn't been created")
    void testRollbackLoyaltyBankCreation_whenRollbackLoyaltyBankCreationCommandHandledWithNoPriorActivity_shouldThrowException() {
        fixture.givenNoPriorActivity()
                .when(rollbackLoyaltyBankCreationCommand)
                .expectException(AggregateNotFoundException.class);
    }

    // TODO: Create tests for deleting loyalty bank with points and skipping the check for available points
}