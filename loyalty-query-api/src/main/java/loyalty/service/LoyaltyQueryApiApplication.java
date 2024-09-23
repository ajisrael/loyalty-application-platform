package loyalty.service;

import loyalty.service.core.errorhandling.LoyaltyServiceEventsErrorHandler;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class LoyaltyQueryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoyaltyQueryApiApplication.class, args);
	}

	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		// TODO: Save group strings to constants
		configurer.registerListenerInvocationErrorHandler("account-group",
				configuration -> new LoyaltyServiceEventsErrorHandler());
		configurer.registerListenerInvocationErrorHandler("loyalty-bank-group",
				configuration -> new LoyaltyServiceEventsErrorHandler());
	}
}
