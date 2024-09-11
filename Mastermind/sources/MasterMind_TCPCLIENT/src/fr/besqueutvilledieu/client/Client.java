package fr.besqueutvilledieu.client;

import java.io.IOException;

import fr.besqueutvilledieu.client.utils.Command;
import fr.besqueutvilledieu.client.utils.Display;

/*
 * LESS AMBITIOUS CLIENT BUT MORE ABLE TO BE WORKING ON DUE DATE
 * @Maxance.VILLEDIEU@etu.uca.fr
 * @Corentin.BESQUEUT@etu.uca.fr 
 * 
 * TURN BY TURN GAME -> TCP PROCOL WILL BE USED
 */
public class Client {
	public static void main(String[] args) throws IOException, InterruptedException {
		Command cd = new Command();
		String[] commands;
		String[] colorsFromCommands;
		Boolean keepOpen = true;
		Display.displayHeader();
		while (keepOpen) {
			commands = cd.getCommandsFromUser();
			colorsFromCommands = Command.retrieveColors(commands, 8);
			if (colorsFromCommands != null) {
				Display.displayNotInGame();
			} else {
				String[] query = cd.retrieveCommand(commands);
				if (query != null) {
					keepOpen = cd.executeCommand(query, null);
				} else {
					Display.displayInvalidCommand();
				}
			}
		}
	}
}
