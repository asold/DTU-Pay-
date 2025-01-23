package dk.dtu.startup;


import dk.dtu.businesslogic.services.PaymentService;
import messaging.implementations.RabbitMqQueue;

/**
 * @author  Andrei Soldan 243873
 */
public class StartUp {


	public static void main(String[] args) throws Exception {

		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			throwable.printStackTrace();
		});

		new StartUp().startUp();
	}

	private void startUp() throws Exception {
		new PaymentService(new RabbitMqQueue("rabbitMq"));

		// Keep the main thread alive to prevent shutdown
		System.out.println("Payment Service is running. Press Ctrl+C to stop.");
		synchronized (this) {
			wait(); // Keeps the main thread alive indefinitely
		}
	}
}
