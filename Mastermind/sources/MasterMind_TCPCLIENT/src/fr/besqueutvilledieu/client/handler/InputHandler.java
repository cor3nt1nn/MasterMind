package fr.besqueutvilledieu.client.handler;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import fr.besqueutvilledieu.client.entity.ClientEntity;

public class InputHandler extends Thread {
	ClientEntity instance;
	Scanner sc;
	private AtomicBoolean isRunning = new AtomicBoolean(true);
	private AtomicInteger turnCounter = new AtomicInteger(0);

	public InputHandler(ClientEntity clientEntity) {
		this.instance = clientEntity;
	}

	@Override
	public synchronized void run() {
		this.sc = new Scanner(System.in);
		while (isRunning.get() == true) {
			if (isRunning.get() == false)
				break;
			new CommandHandler(instance, sc.nextLine()).performCommand();
			;

		}

	}

	public void close() throws IOException {
		this.interrupt();
		this.isRunning.set(false);

	}

	public Scanner getScanner() {
		return sc;
	}

	public AtomicBoolean isRunning() {
		return isRunning;
	}

	public void isRunning(boolean t) {
		isRunning.set(t);
	}

	public AtomicInteger getTurnCounter() {
		return turnCounter;
	}

}
