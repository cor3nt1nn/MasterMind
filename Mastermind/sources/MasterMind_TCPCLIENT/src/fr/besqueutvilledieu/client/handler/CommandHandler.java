package fr.besqueutvilledieu.client.handler;

import java.util.ArrayList;

import fr.besqueutvilledieu.client.entity.ClientEntity;
import fr.besqueutvilledieu.client.gamegrid.Line;
import fr.besqueutvilledieu.client.packet.CustomMessagePacket;
import fr.besqueutvilledieu.client.packet.PlayContentPacket;
import fr.besqueutvilledieu.client.pawn.GamePawn;
import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.utils.Command;
import fr.besqueutvilledieu.client.utils.Display;

public class CommandHandler {
	static ArrayList<String> COMMAND = new ArrayList<String>();
	String[] cmd;
	ClientEntity instance;

	public CommandHandler(ClientEntity instance, String args) {
		this.instance = instance;
		COMMAND.add("/color");
		COMMAND.add("/infos");
		COMMAND.add("/chat");

		this.cmd = args.split(" ");

	}

	public synchronized void performCommand() { // synchronized
		if (instance.getInputHandler().isRunning().get() == false)
			return;
		if (!COMMAND.contains(cmd[0])) {
			System.err.println("This command is not recognized !");
			return;
		}

		switch (cmd[0]) {
			case "/color":
				// LOCKED -> player can play \ server instruction
				if (instance.getGameInstance() == null) {
					System.out.println("The game has not started !");
					return;
				}
				if (instance.isLocked() == false) {
					if (checkColorCommandValidity(cmd) == false) {
						System.out.println(
								"COMMAND ERROR: VALID COLORS NEEDED " + instance.getGameInstance().getSolutionSize());
						Display.displayGamePawns(instance.getGameInstance().getNbColors());
						break;
					}
					StringBuffer contentToSend = new StringBuffer();
					for (int i = 1; i < cmd.length; i++)
						contentToSend.append(cmd[i] + " ");

					instance.sendPacket(new PlayContentPacket(contentToSend.toString()));
					Line l = new Line(instance.getGameInstance().getSolutionSize());

					for (int i = 1; i < cmd.length; i++) {
						GamePawn p = new GamePawn(Command.getColorFromColorKey(cmd[i]), i - 1);
						l.add(p);
					}
					Display.displayLine(l, instance.getInputHandler().getTurnCounter().getAndIncrement());
				} else
					System.out.println(Color.RED.getANSICode() + "Wait your turn !" + Color.RESET.getANSICode());
				break;
			case "/infos":
				if (instance.getGameInstance() != null) {
					Display.displayMarkersHelp();
					Display.displayGamePawns(instance.getGameInstance().getNbColors());
				} else
					System.out.println(Color.RED.getANSICode() + "Game has not started !" + Color.RESET.getANSICode());
				break;
			case "/chat":
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < cmd.length; i++)
					sb.append(cmd[i] + " ");
				instance.sendPacket(new CustomMessagePacket(sb.toString()));
				System.out.println(Color.ORANGE.getANSICode() + "You have just sent the message: " + sb.toString()
						+ Color.RESET.getANSICode());
			default:
				break;
		}
	}

	boolean checkColorCommandValidity(String[] cmd) {
		int rightFormed = 0;
		for (int i = 1; i < cmd.length; i++) { // for more easier command let escape the command prefix -> better
												// complexity
			if (Command.getColorFromColorKey(cmd[i]) != Color.RESET
					&& Command.getAllowedColors(instance.getGameInstance().getNbColors()).contains(cmd[i])) {
				rightFormed++;
			} else
				return false;
		}
		return rightFormed == instance.getGameInstance().getSolutionSize();
	}

}
