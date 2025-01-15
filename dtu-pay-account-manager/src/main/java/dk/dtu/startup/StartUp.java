package dk.dtu.startup;

import dk.dtu.businesslogic.AccountService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
	public static void main(String[] args) throws Exception {
		new StartUp().startUp();
	}

	private void startUp() throws Exception {
		new AccountService(new RabbitMqQueue("rabbitMq"));
	}
}
