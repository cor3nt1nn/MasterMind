package fr.besqueutvilledieu.server.command;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.besqueutvilledieu.server.entity.ServerEntity;

public class InputHandler extends Thread{
	ServerEntity se;
	Scanner sc;
	private AtomicBoolean isRunning = new AtomicBoolean(true);

	public InputHandler(ServerEntity se) {
		this.se =se;
	}
	@Override
	public synchronized void run() {
		this.sc = new Scanner(System.in);
		while(isRunning.get() == true) {
			try {
				if(isRunning.get() == false) break;
				new CommandHandler(se, sc.nextLine()).performCommand();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	public void close() {
		this.isRunning.set(false);
		this.interrupt();
	}
	public AtomicBoolean isRunning() {
		return isRunning;
	}
	public void isRunning(boolean t) {
		 isRunning.set(t);
	}
}
