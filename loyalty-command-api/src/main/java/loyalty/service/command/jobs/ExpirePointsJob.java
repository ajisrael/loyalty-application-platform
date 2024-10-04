package loyalty.service.command.jobs;

import loyalty.service.command.service.PointExpirationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ExpirePointsJob {

    @Autowired
    PointExpirationService pointExpirationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredTransactions() {
        pointExpirationService.expireTransactionsBeforeDate(
                Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                UUID.randomUUID().toString()
        );
    }
}
