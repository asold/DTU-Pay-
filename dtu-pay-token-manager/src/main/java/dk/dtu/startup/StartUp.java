package dk.dtu.startup;

import dk.dtu.businesslogic.services.TokenService;
import messaging.implementations.RabbitMqQueue;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Andrei Soldan 243873
 */
public class StartUp {

	private static final Logger logger = Logger.getLogger(StartUp.class.getName());

	public static void main(String[] args) throws Exception {

		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			logger.log(Level.SEVERE, "Unhandled exception in thread: " + thread.getName(), throwable);
		});

		new StartUp().startUp();
	}

	private void startUp() throws Exception {
		new TokenService(new RabbitMqQueue("rabbitMq"));

		// Keep the main thread alive to prevent shutdown
		logger.info("Token Manager is running. Press Ctrl+C to stop.");
		synchronized (this) {
			wait(); // Keeps the main thread alive indefinitely
		}
	}
}
