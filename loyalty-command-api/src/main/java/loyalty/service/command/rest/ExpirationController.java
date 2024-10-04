package loyalty.service.command.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import loyalty.service.command.commands.transactions.CreatePendingTransactionCommand;
import loyalty.service.command.rest.requests.CreateLoyaltyTransactionRequestModel;
import loyalty.service.command.rest.responses.TransactionCreatedResponseModel;
import loyalty.service.command.service.PointExpirationService;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

@RestController
@RequestMapping("/expire")
@Tag(name = "Loyalty Service Command API")
public class ExpirationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpirationController.class);

    @Autowired
    private PointExpirationService pointExpirationService;

    @PostMapping()
    @Operation(summary = "Expire transactions before the passed date")
    public ResponseEntity<String> expireTransactions(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Convert LocalDate to Instant at the start of the day in UTC
        Instant expirationDate = date.atStartOfDay(ZoneId.of("UTC")).toInstant();

        String requestId = UUID.randomUUID().toString();

        LOGGER.info(Markers.append(REQUEST_ID, requestId), "Expiring transactions before {}", expirationDate);

        pointExpirationService.expireTransactionsBeforeDate(expirationDate, requestId);

        return ResponseEntity.ok("Request to expire transactions before " + expirationDate + " sent");
    }
}
