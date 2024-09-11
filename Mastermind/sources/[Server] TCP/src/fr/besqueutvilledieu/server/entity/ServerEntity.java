package fr.besqueutvilledieu.server.entity;
/*
 * TCP SERVER REPRESENTATION 
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.besqueutvilledieu.client.game.Game;
import fr.besqueutvilledieu.server.packet.LaunchGamePacket;
import fr.besqueutvilledieu.server.GameStates;
import fr.besqueutvilledieu.server.command.InputHandler;
import fr.besqueutvilledieu.server.handler.ClientHandler;
import fr.besqueutvilledieu.server.packet.CustomMessagePacket;
import fr.besqueutvilledieu.server.packet.Deliverable;
import fr.besqueutvilledieu.server.packet.GameStartPacket;
import fr.besqueutvilledieu.server.packet.SettingsGamePacket;

public class ServerEntity extends Thread {
	private ServerSocket serverSocket;
	private int port;
	private int maxPlayer;
	boolean shuffle;
	private Game winGrid;
	int nbColors;
	int solutionSize;
	int maxAttempts;
	boolean allowRepeat;
	public boolean forceStart = false;
	private AtomicBoolean isRunning = new AtomicBoolean(true);
	private ExecutorService pool;
	boolean startGameLoop = false;
	Scanner sc;
	private GameStates currentState = GameStates.WAITING;
	public Vector<ClientHandler> connectedClient = new Vector<ClientHandler>();
	public ConcurrentMap<ClientHandler, String[]> playerMoves = new ConcurrentHashMap<ClientHandler, String[]>();
	public CopyOnWriteArrayList<ClientHandler> winners = new CopyOnWriteArrayList<ClientHandler>();
	public CopyOnWriteArrayList<ClientHandler> inGamePlayer = new CopyOnWriteArrayList<ClientHandler>();
	public CopyOnWriteArrayList<ClientHandler> spectators = new CopyOnWriteArrayList<ClientHandler>();
	InputHandler ih = null;
	Integer acceptanceCount = 0;

	public ServerEntity(int port, int maxPlayer, int maxAttempts, int solutionSize, boolean shuffle,
			boolean allowRepeat, int nbColors) throws IOException {
		try {
			this.setServerSocket(new ServerSocket(port));
			pool = Executors.newCachedThreadPool();
		} catch (IOException e) {
			System.err.println("Error while starting server !");
			e.printStackTrace();
		}

		ih = new InputHandler(this);
		ih.start();
		this.port = port;
		this.maxAttempts = maxAttempts;
		this.solutionSize = solutionSize;
		this.nbColors = nbColors;
		this.shuffle = shuffle;
		this.allowRepeat = allowRepeat;
		this.maxPlayer = maxPlayer;
		winGrid = new Game(maxAttempts, solutionSize, shuffle, allowRepeat, nbColors);

		System.out.println("GAME INSTANCE FOR SOLUTION SUCCESSFULLY LAUNCHED");
	}

	public synchronized void run() {

		while (this.isRunning.get() == true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				acceptanceCount++;
				if (acceptanceCount % 2 == 0) {
					ClientHandler client = new ClientHandler(clientSocket, this);
					client.start();

					connectedClient.add(client);
	
					pool.execute(client);
					// MESSAGE FOR PLAYER NEEDED TO START THE GAME

					if (connectedClient.size() < getMaxPlayer() && (currentState == GameStates.WAITING)) {
						sendAll(new CustomMessagePacket("[SERVER] Players required to start : " + connectedClient.size()
								+ "/" + getMaxPlayer()));
						client.sendPacket(new SettingsGamePacket(" " + getGameInstance().getMaxAttempts() + " "
								+ getGameInstance().getSolutionSize() + " " + getGameInstance().getShuffle() + " "
								+ getGameInstance().getAllowRepeat() + " " + getGameInstance().getNbColors() + " "));
						// SEND SETTINGS PACKET
					} else if ((connectedClient.size() >= getMaxPlayer() && currentState == GameStates.WAITING)
							|| (forceStart == true && currentState == GameStates.WAITING)) {
						if (forceStart == true)
							maxPlayer = connectedClient.size();

						// RIGHT NUMBER OF PLAYER ARE IN THE GAME
						sendAll(new SettingsGamePacket(" " + getGameInstance().getMaxAttempts() + " "
								+ getGameInstance().getSolutionSize() + " " + getGameInstance().getShuffle() + " "
								+ getGameInstance().getAllowRepeat() + " " + getGameInstance().getNbColors() + " "));
						inGamePlayer.addAll(connectedClient);
						wait(200);
						currentState = GameStates.PREGAME;

					}
					if (currentState == GameStates.PREGAME) {
						sendAll(new LaunchGamePacket("Game can start"));
					}
					if (inGamePlayer.size() >= getMaxPlayer() && currentState == GameStates.PREGAME) {
						sendAll(new GameStartPacket("PLAY"));
						currentState = GameStates.GAME;
					}
				} else
					System.out.println("Client verifyer caught !");
			} catch (IOException | InterruptedException e) {
				System.out.println("Server Socket Closed");
			}
			// -----------------------------------------------------------------

		}

	}

	public void sendAll(Deliverable packet) {
		for (ClientHandler ch : connectedClient) {
			if (ch != null) {
				ch.sendPacket(packet);
			}
		}
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	private void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public int getPort() {
		return port;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public GameStates getCurrentGameState() {
		return this.currentState;
	}

	public void setCurrentGameState(GameStates newState) {
		this.currentState = newState;
	}

	public Game getGameInstance() {
		return this.winGrid;
	}

	public InputHandler getInputHandler() {
		return ih;
	}

	public void setMaxPlayer(int number) {
		maxPlayer = number;
	}

	public void close() throws InterruptedException, IOException {
		getInputHandler().close();
		System.out.println("Input handler closed !");
		this.isRunning.set(false);
		this.getServerSocket().close();
		this.interrupt();
		pool.shutdownNow();
		System.out.println("All closed - Exit");
		System.exit(0);
	}

	public ExecutorService getThreadPool() {
		return pool;
	}

}
