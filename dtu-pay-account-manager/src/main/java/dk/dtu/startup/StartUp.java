package dk.dtu.startup;

import dk.dtu.businesslogic.services.AccountService;
import messaging.implementations.RabbitMqQueue;

import java.util.logging.Logger;

/**
 * @author  Andrei Soldan 243873
 */
public class StartUp {

	private static final Logger logger = Logger.getLogger(StartUp.class.getName());

	public static void main(String[] args) throws Exception {

		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			logger.info("Unhandled exception in thread: " + thread.getName() + " " + throwable.getCause());
		});

		new StartUp().startUp();
	}

	private void startUp() throws Exception {

		new AccountService(new RabbitMqQueue("rabbitMq"));

		// Keep the main thread alive to prevent shutdown
		logger.info("Account Manager is running. Press Ctrl+C to stop.");
		synchronized (this) {
			wait(); // Keeps the main thread alive indefinitely
		}
	}
}
