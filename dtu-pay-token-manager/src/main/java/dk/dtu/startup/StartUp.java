package dk.dtu.startup;

import dk.dtu.businesslogic.services.TokenService;
import messaging.implementations.RabbitMqQueue;

/**
 * @author  Andrei Soldan 243873
 */
public class StartUp {

	public static void main(String[] args) throws Exception {
		new StartUp().startUp();
	}

	private void startUp() throws Exception {
		new TokenService(new RabbitMqQueue("rabbitMq"));

		// Keep the main thread alive to prevent shutdown
		System.out.println("Account Manager is running. Press Ctrl+C to stop.");
		synchronized (this) {
			wait(); // Keeps the main thread alive indefinitely
		}
	}
}
