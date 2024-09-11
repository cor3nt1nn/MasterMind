package fr.besqueutvilledieu.server;

import java.io.IOException;
import java.net.SocketException;

import fr.besqueutvilledieu.server.entity.ServerEntity;

/*
 * LESS AMBITIOUS SERVER BUT MORE ABLE TO BE WORKING ON DUE DATE
 * @Maxance.VILLEDIEU@etu.uca.fr
 * @Corentin.BESQUEUT@etu.uca.fr 
 * TURN BY TURN GAME -> TCP PROCOL WILL BE USED
 */
public class Server {

	public static void main(String[] args) throws SocketException {
		ServerEntity se = null;
		try {
			se = new ServerEntity(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
					Integer.parseInt(args[2]), Integer.parseInt(args[3]), Boolean.valueOf(args[4]),
					Boolean.valueOf(args[5]), Integer.parseInt(args[6]));
		se.start();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server started on port: " + se.getPort());
	}
}
