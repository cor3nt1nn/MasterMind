package fr.besqueutvilledieu.client.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.besqueutvilledieu.client.game.Game;
import fr.besqueutvilledieu.client.handler.InputHandler;
import fr.besqueutvilledieu.client.packet.CustomMessagePacket;
import fr.besqueutvilledieu.client.packet.Deliverable;
import fr.besqueutvilledieu.client.packet.EndConnectionPacket;
import fr.besqueutvilledieu.client.packet.GameContinuePacket;
import fr.besqueutvilledieu.client.packet.GameStartPacket;
import fr.besqueutvilledieu.client.packet.GameWaitPacket;
import fr.besqueutvilledieu.client.packet.LaunchGamePacket;
import fr.besqueutvilledieu.client.packet.LoginPacket;
import fr.besqueutvilledieu.client.packet.PlayContentPacket;
import fr.besqueutvilledieu.client.packet.SettingsGamePacket;
import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.utils.Display;

public class ClientEntity extends Thread {
	private Socket clientSocket;
	// import the game
	/*
	 * PARAM
	 * 
	 */
	private InputHandler inH;
	private int connectionPort;
	private String name;
	private Game instance;
	boolean shuffle;
	int nbColors;
	int solutionSize;
	int maxAttempts;
	boolean allowRepeat;
	boolean canPlay = true;
	boolean isRunning = true;
	boolean correctlyCreated = false;
	private PrintWriter writer;
	private BufferedReader reader;
	boolean hasReceiveSettings = false;
	AtomicBoolean hasStopped = new AtomicBoolean(false);

	public ClientEntity(String name, String ipAddress, int port) throws UnknownHostException, IOException {
		if (name.isBlank())
			name = UUID.randomUUID().toString();
		this.name = name;

		this.connectionPort = port;
		this.clientSocket = new Socket(ipAddress, this.connectionPort);
		this.correctlyCreated = true;
		this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.writer = new PrintWriter(clientSocket.getOutputStream());
		sendPacket(new LoginPacket(getUsername()));

		this.inH = new InputHandler(this);
		inH.start();

	}

	public synchronized void run() {
		try {
			String packet;
			while (hasStopped.get() == false && (packet = reader.readLine()) != null) {

				if (packet.startsWith(SettingsGamePacket.prefix)) {
					// GETTING ALL THE SETTINGS
					String[] params = packet.toString().substring(SettingsGamePacket.prefix.length()).split(" ");
					maxAttempts = Integer.parseInt(params[1]);
					solutionSize = Integer.parseInt(params[2]);
					shuffle = Boolean.valueOf(params[3]);
					allowRepeat = Boolean.valueOf(params[4]);
					nbColors = Integer.parseInt(params[5]);
					hasReceiveSettings = true;

				} else if (packet.startsWith(CustomMessagePacket.prefix)) {
					// CASUAL MESSAGE FROM SERVER - COULD BE USED HAS CHATPACKET ALSO
					String desencapsulatedMSG = packet.substring(CustomMessagePacket.prefix.length());
					if(desencapsulatedMSG.startsWith("[CHAT]")) {
						System.out.println(Color.ORANGE.getANSICode()+desencapsulatedMSG+Color.RESET.getANSICode());
					}else
					System.out.println(desencapsulatedMSG);
				} else if (packet.startsWith(LaunchGamePacket.prefix)) {
					// MAKE ALL CLIENT START THE GAME
					instance = new Game(maxAttempts, solutionSize, shuffle, allowRepeat, nbColors);
					System.out.println(packet.substring(LaunchGamePacket.prefix.length()));
					sendPacket(new LaunchGamePacket("Game Started"));
					Display.displayNewGameMessage(instance.getMaxAttempts(), instance.getSolutionSize(),
							instance.getShuffle(), instance.getAllowRepeat(), instance.getNbColors());
					System.out.print("\n/color");
					for (int i = 0; i < instance.getSolutionSize(); i++) {
						System.out.print(" x");
					}
					System.out.println(" to make a guess attempt where x is the color of the pawn placed");
					System.out.println("/infos to show allowed color pawns and the meaning of the markers");
					Display.displayBottomSeparation();
				} else if (packet.startsWith(GameStartPacket.prefix)) {
					System.out.println(Color.GREEN.getANSICode()+"Your can play your first move !"+Color.RESET.getANSICode());
					canPlay = true;
					wait(200);
				} else if (packet.toString().startsWith(GameWaitPacket.prefix)) {
					System.out.println(Color.RED.getANSICode()+"Waiting for other players !"+Color.RESET.getANSICode());
					canPlay = false;
					wait(200);
				} else if (packet.toString().startsWith(GameContinuePacket.prefix)) {
					System.out.println(Color.GREEN.getANSICode()+"Your turn to play !"+Color.RESET.getANSICode());
					canPlay = true;
					wait(200);
				} else if (packet.startsWith(PlayContentPacket.prefix)) {
					String[] packetSer = packet.substring(PlayContentPacket.prefix.length()).split(" ");
					StringBuffer marker = new StringBuffer();
					for (int i = 1; i < packetSer.length; i++) {
						if (packetSer[i].equals("G")) {
							marker.append(new String(
									Color.GREYBLUE.getANSICode() + Character.toString(120) + Color.RESET.getANSICode())
									+ " ");
						} else if (packetSer[i].equals("W")) {
							marker.append(Color.WHITE.getANSICode() + Character.toString(120)
									+ Color.RESET.getANSICode() + " ");
						} else if (packetSer[i].equals("D"))
							marker.append(Color.WHITEPOINT.getANSICode() + Character.toString(46)
									+ Color.RESET.getANSICode() + " ");
					}
					Display.displayBottomSeparation();

					System.out.println("Result for player " + packetSer[0] + " " + marker.toString());
				} else if (packet.toString().startsWith(EndConnectionPacket.prefix)) {
					System.out.println(packet.substring(EndConnectionPacket.prefix.length()));

					close();
					break;
				}
			}
		} catch (IOException | InterruptedException e) {
		
			try {
				close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}

	}
	public void close() throws IOException {
		Display.displayBottomSeparation();
		Display.displayMessageToReturnToMenu();

		getInputHandler().close();

		this.clientSocket.close();
		isRunning = false;
		try {
			getInputHandler().join();
			getInputHandler().interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(Deliverable packet) {
		getWriter().println(packet.toString());
		getWriter().flush();
	}

	public InputHandler getInputHandler() {
		return inH;
	}

	public boolean isLocked() {
		return !canPlay;
	}

	public Game getGameInstance() {
		return instance;
	}

	public int getConnectionPort() {
		return connectionPort;
	}

	public String getUsername() {
		return name;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public AtomicBoolean hasStopped() {
		return hasStopped;
	}

}
