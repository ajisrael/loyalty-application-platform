package loyalty.service;

import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import static loyalty.service.core.constants.DomainConstants.*;

@EnableDiscoveryClient
@SpringBootApplication
public class LoyaltyQueryApiApplication {

	// TODO: update bank return to not include account id
	// TODO: update loyalty bank to have a name
	// TODO: change loyalty bank query endpoint to require account id and name
	// TODO: add loyalty bank query endpoint/projection to get all loyalty banks for an account

	public static void main(String[] args) {
		SpringApplication.run(LoyaltyQueryApiApplication.class, args);
	}

	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		// TODO: Save group strings to constants
		configurer.registerListenerInvocationErrorHandler(ACCOUNT_GROUP,
				configuration -> new LoyaltyServiceEventsErrorHandler());
		configurer.registerListenerInvocationErrorHandler(BUSINESS_GROUP,
				configuration -> new LoyaltyServiceEventsErrorHandler());
		configurer.registerListenerInvocationErrorHandler(LOYALTY_BANK_GROUP,
				configuration -> new LoyaltyServiceEventsErrorHandler());
		configurer.registerListenerInvocationErrorHandler(ACTIVITY_LOG_GROUP,
				configuration -> new LoyaltyServiceEventsErrorHandler());
	}
}
