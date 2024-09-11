package fr.besqueutvilledieu.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import fr.besqueutvilledieu.client.gamegrid.Line;
import fr.besqueutvilledieu.client.pawn.GamePawn;
import fr.besqueutvilledieu.client.pawn.Pawn;
import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.utils.Command;
import fr.besqueutvilledieu.server.GameStates;
import fr.besqueutvilledieu.server.entity.ServerEntity;
import fr.besqueutvilledieu.server.packet.CustomMessagePacket;
import fr.besqueutvilledieu.server.packet.Deliverable;
import fr.besqueutvilledieu.server.packet.EndConnectionPacket;
import fr.besqueutvilledieu.server.packet.GameContinuePacket;
import fr.besqueutvilledieu.server.packet.GameWaitPacket;
import fr.besqueutvilledieu.server.packet.LaunchGamePacket;
import fr.besqueutvilledieu.server.packet.LoginPacket;
import fr.besqueutvilledieu.server.packet.PlayContentPacket;

/*
 * Handler for Client -> keep stream from connection 
 * 
 */
public class ClientHandler extends Thread {
	private String username;
	private BufferedReader reader;
	private PrintWriter writer;
	private ServerEntity server;
	private Socket socket;

	public ClientHandler(Socket clientSocket, ServerEntity serverEntity) {
		this.socket = clientSocket;
		this.server = serverEntity;
		try {
			this.writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	@Override
	public synchronized void run() {
		try {
			this.writer = new PrintWriter(getSocket().getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
			String packetReceived;
			while ((packetReceived = reader.readLine()) != null) {
				// LOGIN PHASE
				// ---------------------------------------------------------------------
				if (packetReceived.startsWith(LoginPacket.prefix)) {
					this.username = packetReceived.toString().substring(LoginPacket.prefix.length()).split(" ")[0];
					if (getInstance().getCurrentGameState() != GameStates.WAITING) {
						getInstance().sendAll(new CustomMessagePacket(this.getUsername() + " joins as spectator !"));
						getInstance().spectators.add(this);

					}

					System.out.println("[+] " + getUsername() + " joined the room !");

					// ---------------------------------------------------------------------------------------------
					// LAUNCH THE CLIENT PHASE (ENSURE THAT ALL CLIENTS ARE READY TO
					// START)-------------------------------
				} else if (packetReceived.startsWith(LaunchGamePacket.prefix)
						&& getInstance().getCurrentGameState() == GameStates.PREGAME) {

					System.out.println("[+] " + getUsername() + " has started its game");
					getInstance().setCurrentGameState(GameStates.GAME);
					// -------------------------------------------------------------------------------------------------------------------------------
					// ------------------------------------ CHECK THE CASE OF A CONTENT WAS SENT
					// -----------------------------------------------

				} else if ((getInstance().getCurrentGameState() == GameStates.GAME
						|| getInstance().getCurrentGameState() == GameStates.WAITING_FOR_TURN)
						&& packetReceived.startsWith(PlayContentPacket.prefix)) {
					// IF PLAYER HAVE ALREADY PLAYED
					if (getInstance().playerMoves.containsKey(this)) {

						sendPacket(new GameWaitPacket("WAITING FOR OTHERS TO PLAY"));
					} else {

						getInstance().playerMoves.put(this,
								packetReceived.substring(PlayContentPacket.prefix.length()).split(" "));

						// TRANSFORM COLORS (STRING) WE RECEIVED TO PAWNS.
						ArrayList<GamePawn> pawnForTurn = new ArrayList<GamePawn>();
						for (int i = 0; i < getInstance().playerMoves.get(this).length; i++) {
							pawnForTurn.add(new GamePawn(
									Command.getColorFromColorKey(getInstance().playerMoves.get(this)[i]), i));
						}
						// CONVERT THESE PAWNS TO LINE
						Line gameLineForClient = new Line(getInstance().getGameInstance().getSolutionSize());
						gameLineForClient.addAll(pawnForTurn);

						// GET MARKERS FROM THIS ATTEMPT

						Line markerLineForClient = getInstance().getGameInstance()
								.getMarkersForAttempt(gameLineForClient, getInstance().getGameInstance().getShuffle());

						// SERIALIZE MARKER
						StringBuffer markerSerialize = new StringBuffer();
						/*
						 * CREATE G ALIAS FOR GB COLOR
						 */
						for (int i = 0; i < markerLineForClient.getLinkedList().size(); i++) {
							char prefix = ' ';
							if (markerLineForClient.getLinkedList().get(i).getColor().getColorKey() == "GB") {
								prefix = 'G';
							} else if (markerLineForClient.getLinkedList().get(i).getColor().getColorKey() == "w") {
								prefix = 'W';
							} else if (markerLineForClient.getLinkedList().get(i).getColor().getColorKey() == "W")
								prefix = 'D';

							markerSerialize.append(prefix + " ");

						}
						// ----------------------------------------------------------------------------------------------------

						// SEND THE ATTEMPT TO PLAYERS (SPECTATOR AND IN GAME PLAYERS) -> SENDALL
						getInstance()
								.sendAll(new PlayContentPacket(this.getUsername() + " " + markerSerialize.toString()));

						int wellPlacedPawn = 0;
						// CHECK MARKERS
						for (Pawn mp : markerLineForClient.getLinkedList()) {
							if (mp.getColor() == Color.GREYBLUE)
								wellPlacedPawn++;
						}
						// CHECK THE WIN
						if (wellPlacedPawn == markerLineForClient.getLinkedList().size()
								&& (getInstance().getCurrentGameState() == GameStates.GAME
										|| getInstance().getCurrentGameState() == GameStates.WAITING_FOR_TURN)) {
							synchronized (getInstance().winners) {
								getInstance().winners.add(this);
							}
							wellPlacedPawn = 0;
						}

						// CHECK IF ALL PLAYERS HAVE SUBMIT THEIR TRIES
						if (getInstance().playerMoves.keySet().size() < getInstance().inGamePlayer.size()
								&& getInstance().getCurrentGameState() == GameStates.GAME) {
							getInstance().setCurrentGameState(GameStates.WAITING_FOR_TURN);

						}
						if (getInstance().getCurrentGameState() == GameStates.WAITING_FOR_TURN
								&& getInstance().winners.size() == 0) {

							sendPacket(new GameWaitPacket("WAITING FOR OTHERS TO PLAY")); // PREV SENDALL

						}
						// else
						// ALL PEOPLE HAVE PLAYED
						// SEND VERIFICATIONS Markers TO ALL
						// CHECK IF SOMEONE HAS WIN
						// IF SOMEONE HAS WON END THE GAME

					}
					if (getInstance().playerMoves.keySet().size() >= getInstance().inGamePlayer.size()) {
						getInstance().sendAll(new GameContinuePacket("All players have play their turn"));
						getInstance().playerMoves.clear();
						getInstance().setCurrentGameState(GameStates.GAME);

						// check for winners
						if (getInstance().winners.size() > 0
								&& getInstance().getCurrentGameState() == GameStates.GAME) {
							StringBuffer winnerMSG = new StringBuffer("WINNER");

							if (getInstance().winners.size() == 1) {
								winnerMSG.append(" IS " + getInstance().winners.get(0).getUsername());
							} else {

								int i = 0;
								winnerMSG.append(" ARE ");
								for (ClientHandler winner : getInstance().winners) {

									winnerMSG.append(winner.getUsername()
											+ (i == getInstance().winners.size() - 1 ? " " : " AND "));
									i++;
								}

							}
							getInstance()
									.sendAll(new EndConnectionPacket("THANKS FOR PLAYING - " + winnerMSG.toString()));

							getInstance().setCurrentGameState(GameStates.END);

						} else {
							// IF NO WINNERS ADD A FAKE ATTEMPT FOR SERVER
							getInstance().getGameInstance().getPlayerAttempts().addAttempt(null, null);

						}
					}

				} else if (packetReceived.startsWith(EndConnectionPacket.prefix)) {
					if (server.inGamePlayer.contains(this))
						server.inGamePlayer.remove(this);
					if (server.connectedClient.contains(this))
						server.connectedClient.remove(this);
					server.sendAll(new CustomMessagePacket("[-] " + this.getUsername()));
					this.close();
				}
				// NO WINNERS KICK ALL
				if (getInstance().getGameInstance().getPlayerAttempts().getSize() == getInstance().getGameInstance()
						.getMaxAttempts()) {
					getInstance().sendAll(new EndConnectionPacket("THANKS FOR PLAYING - NO WINNERS"));

					getInstance().setCurrentGameState(GameStates.END);
				}

				if (packetReceived.startsWith(CustomMessagePacket.prefix)) {
					for (ClientHandler ch : getInstance().connectedClient) {
						if (ch != this)
							ch.sendPacket(new CustomMessagePacket("[CHAT] " + getUsername() + ": "
									+ packetReceived.substring(CustomMessagePacket.prefix.length())));
					}
				}

			}
			if (server.getCurrentGameState() == GameStates.END) {
				// close handler
				try {
					this.close();
					System.out.println("Handler closed !");
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				try {
					// close input server
					if (server.isInterrupted() == false) {
						server.close();

					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

		} catch (IOException | InterruptedException e) {

		}
	}

	public void sendPacket(Deliverable packet) {
		getWriter().println(packet);
	}

	public Socket getSocket() {
		return this.socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public String getUsername() {
		return username;
	}

	public synchronized ServerEntity getInstance() {
		return server;
	}

	public void close() throws IOException, InterruptedException {
		getWriter().close();
		getReader().close();
		getSocket().close();

	}

}
