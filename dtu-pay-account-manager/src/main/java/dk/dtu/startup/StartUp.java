package dk.dtu.startup;

import dk.dtu.businesslogic.AccountService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
	public static void main(String[] args) throws Exception {
		new StartUp().startUp();
	}

	private void startUp() throws Exception {
		new AccountService(new RabbitMqQueue("rabbitMq"));

		// Keep the main thread alive to prevent shutdown
		System.out.println("Account Manager is running. Press Ctrl+C to stop.");
		synchronized (this) {
			wait(); // Keeps the main thread alive indefinitely
		}
	}
}
