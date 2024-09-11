package fr.besqueutvilledieu.server.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import fr.besqueutvilledieu.client.utils.Display;
import fr.besqueutvilledieu.server.GameStates;
import fr.besqueutvilledieu.server.entity.ServerEntity;
import fr.besqueutvilledieu.server.handler.ClientHandler;
import fr.besqueutvilledieu.server.packet.CustomMessagePacket;
import fr.besqueutvilledieu.server.packet.EndConnectionPacket;
import fr.besqueutvilledieu.server.packet.LaunchGamePacket;
import fr.besqueutvilledieu.server.packet.SettingsGamePacket;

public class CommandHandler {
	static ArrayList<String> COMMAND = new ArrayList<String>();
	String[] cmd;
	ServerEntity server;
	public CommandHandler(ServerEntity server, String args) {
		COMMAND.add("/kick");
		COMMAND.add("/list");
		COMMAND.add("/forcestart");
		COMMAND.add("/solution");
		this.cmd = args.split(" ");
		this.server = server;
	}

	public synchronized void  performCommand() throws IOException, InterruptedException {
		if(server.getInputHandler().isRunning().get() == false) return;
		if (!COMMAND.contains(cmd[0]))
			System.err.println("This command is not recognized !"); // no error raised because it kill the thread
		switch (cmd[0]) {
		case "/kick":
			if (cmd.length != 3) {
				System.err.println("This command is malformed !");
				System.err.println("USAGE /kick <ip> <port>");
				break;
			} else {
				boolean founded = false;
				for (ClientHandler ch : server.connectedClient) {
					if (ch.getSocket().getInetAddress().toString().equals(cmd[1]) && ch.getSocket().getPort() == Integer.parseInt(cmd[2])) {
					
						ch.sendPacket( new EndConnectionPacket("You got kick by Server"));
						ch.sendPacket( new CustomMessagePacket("You got kick by Server"));
						if(server.connectedClient.contains(ch))
						server.connectedClient.remove(ch);
						if(server.inGamePlayer.contains(ch))
						server.inGamePlayer.remove(ch);
				
					
						
						ch.close();
						founded = true;
						System.out.println("PLEASE PERFORM THIS COMMAND AGAIN TO CONFIRM");
						break;
					}
				}
				if(founded == false) System.err.println("The user using this address: " + cmd[1] + " was not found");
			}
			
			break;
		case "/list":
			System.out.println("Number of online players " + server.connectedClient.size());
			for (ClientHandler ce : server.connectedClient) {
				if(ce.getUsername() != null)
				System.out.println(
						new Date() + " " + ce.getUsername() + " - " + ce.getSocket().getInetAddress() + " - " + ce.getSocket().getPort());
				
	
			}
			break;
		case "/forcestart":
			if(server.getCurrentGameState() == GameStates.WAITING) {
			server.sendAll(new CustomMessagePacket("[SERVER]: GAME HAS BEEN STARTED BY THE SERVER !"));
			server.sendAll(new LaunchGamePacket("Game can start"));
			server.sendAll(new SettingsGamePacket(" " + server.getGameInstance().getMaxAttempts() + " " +server.getGameInstance().getSolutionSize() + " " +server.getGameInstance().getShuffle() + " "
					+server.getGameInstance().getAllowRepeat() + " " + server.getGameInstance().getNbColors() + " "));
			server.setCurrentGameState(GameStates.PREGAME); 
			}else System.err.println("YOU CAN ONLY USE THIS COMMAND DURING THE WAINTING PHASE");
			break;
		case "/solution":
				Display.displaySolution(server.getGameInstance().getSolution());
			break;
		}
		

		System.out.println("COMMAND PERFORMED");
	}
}
