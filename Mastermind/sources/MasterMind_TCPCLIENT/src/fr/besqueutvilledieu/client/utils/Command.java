package fr.besqueutvilledieu.client.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import fr.besqueutvilledieu.client.entity.ClientEntity;
import fr.besqueutvilledieu.client.game.Game;
import fr.besqueutvilledieu.client.gamegrid.Grid;
import fr.besqueutvilledieu.client.gamegrid.Pattern;
import fr.besqueutvilledieu.client.handler.ArchiveManager;

public class Command {

	/*
	 * Initialization of a scanner to read user commands and a second one to read
	 * player's game parameters in case of custom game
	 */
	Scanner sc;
	Scanner set;

	public Command() {
		this.sc = new Scanner(System.in);
		this.set = new Scanner(System.in);
	}

	/* Get the player's command line */
	public String[] getCommandsFromUser() {
		String inputs = sc.nextLine();
		String[] commands = inputs.split("\\s+");
		System.out.println("");
		return commands;
	}

	/*
	 * Retrieves from commands the player's colors choice for a new attempt, return
	 * null if no colors in command line
	 */
	public static String[] retrieveColors(String[] commands, int nbColors) {
		ArrayList<String> allowedColors = getAllowedColors(nbColors);
		ArrayList<String> allColors = getAllowedColors(8);
		boolean allColorsAllowed = true;
		for (String command : commands) {
			if (!allowedColors.contains(command)) {
				allColorsAllowed = false;
				if (allColors.contains(command)) {
					Display.displayInvalidPawnsInput();
				}
				break;
			}
		}
		if (allColorsAllowed) {
			return Arrays.copyOf(commands, commands.length);
		}
		return null;
	}

	/* Retrieves allowed commands from command line */
	public String[] retrieveCommand(String[] commands) {
		ArrayList<String> allowedCommands = getAllowedCommands();
		if (commands.length >= 1 && commands.length <= 4) {
			String command = commands[0];
			if (command.startsWith("/") && allowedCommands.contains(command.substring(1))) {
				switch (commands.length) {
					case 1:
						return new String[] { command.substring(1) };
					case 2:
						return new String[] { command.substring(1), commands[1] };
					case 4:
						return new String[] { command.substring(1), commands[1], commands[2], commands[3] };
				}
			}
		}
		return null;
	}

	/* Retrieves the ArrayList of all allowed commands */
	private static ArrayList<String> getAllowedCommands() {
		ArrayList<String> allowedCommands = new ArrayList<>();
		allowedCommands.add("pawns");
		allowedCommands.add("size");
		allowedCommands.add("marks");
		allowedCommands.add("help");
		allowedCommands.add("new");
		allowedCommands.add("multi");
		allowedCommands.add("save");
		allowedCommands.add("load");
		allowedCommands.add("solve");
		allowedCommands.add("quit");
		allowedCommands.add("prev");
		allowedCommands.add("home");
		return allowedCommands;
	}

	/*
	 * Retrieves the ArrayList of all in-game allowed colors (depending on
	 * "nbColors")
	 */
	public static ArrayList<String> getAllowedColors(int nbColors) {
		ArrayList<String> allowedColors = new ArrayList<>();
		String[] colors = { "r", "g", "y", "b", "p", "w", "o", "t" };
		for (int i = 0; i < nbColors; i++) {
			allowedColors.add(colors[i]);
		}
		return allowedColors;
	}

	public static Color getColorFromColorKey(String colorKey) {
		/* Retrieves an element of enum Color from color key */
		Color color = null;
		switch (colorKey) {
			case "r":
				color = Color.RED;
				break;
			case "g":
				color = Color.GREEN;
				break;
			case "b":
				color = Color.BLUE;
				break;
			case "y":
				color = Color.YELLOW;
				break;
			case "p":
				color = Color.PINK;
				break;
			case "t":
				color = Color.TURQUOISE;
				break;
			case "o":
				color = Color.ORANGE;
				break;
			case "w":
				color = Color.WHITE;
				break;
			default:
				return Color.RESET;
		}
		return color;
	}

	/* Retrieves an ArrayList of enum Color from color commands */
	public ArrayList<Color> getColorsListFromColorKeys(String[] colorKeys) {
		ArrayList<Color> colors = new ArrayList<>();
		Color currentColor;
		for (String color : colorKeys) {
			currentColor = getColorFromColorKey(color);
			if (currentColor != null) {
				colors.add(currentColor);
			}
		}
		return colors;
	}

	/* Executes commands from command line */
	public boolean executeCommand(String[] command, Game game) throws IOException, InterruptedException {
		ArchiveManager am = new ArchiveManager();
		switch (command[0]) {
			case "pawns":
				if (game != null) {
					Display.displayGamePawns(game.getNbColors());
				} else {
					Display.displayGamePawns(8);
				}
				return true;
			case "size":
				if (game != null) {
					Display.displayGameBoardSize(game.getSolutionSize(), game.getMaxAttempts());
				} else {
					Display.displayNotInGame();
				}
				return true;
			case "marks":
				Display.displayMarkersHelp();
				return true;
			case "help":
				if (game != null) {
					Display.displayGameCommands(game.getSolutionSize());
				} else {
					Display.displayHomeCommands();
				}
				return true;
			case "multi":
				// ClientEntity ce = new ClientEntity("Tiramisu", "localhost", 55678);
				// ce.start();
				// DO NOT WORK
				if (command.length == 4) {
					// Fermeture du Scanner pour la durée de la partie multijoueur
					// sc.close();
					// Récupération des données clients pour la connexion au serveur
					String name = command[1];
					String address = command[2];
					int port = Integer.parseInt(command[3]);
					// Création d'une nouvelle entité Client
					if (ConnectionVerifyer.isValidConnection(address, port) == true) {

						ClientEntity ce = new ClientEntity(name, address, port);
						if (ce.isCorrectlyCreated()) {
							ce.start();
							ce.join(); // make the thread blocking the programme
							// Réouverture du Scanner
							this.sc = new Scanner(System.in);
							this.set = new Scanner(System.in);
							Display.displayHomePage();
							Display.displayHomeCommands();
						}

					}
				} else {
					Display.displayInvalidCommand();
				}

				return true;
			case "save":
				if (game != null) {
					if (command.length == 2) {
						am.saveGame(game.getPlayerAttempts(), game.getSolution(), command[1]);
					} else {
						Display.displayInvalidCommand();
					}
				}
				return true;
			case "load":
				if (game == null) {
					if (command.length == 2) {
						Object[] gameData = am.loadGame(command[1]);
						game = new Game();
						game.setSolution((Pattern) gameData[3]);
						game.setPlayerAttempts((Grid) gameData[2]);
						game.setSolutionSize((int) gameData[0]);
						game.playGame();
						System.out.println("You are returning to the home page in 3...");
						try {
							Thread.sleep(1000);
							System.out.println("                                      2...");
							Thread.sleep(1000);
							System.out.println("                                      1...");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						;
						Display.displayHomePage();
					} else {
						Display.displayInvalidCommand();
					}
				} else {
					Display.displayAlreadyInGame();
				}
				return true;
			case "new":
				boolean correctGameLaunch = true;
				if (game == null) {
					switch (command.length) {
						case 1:
							game = new Game();
							game.playGame();
							break;
						case 2:
							if (command[1].equalsIgnoreCase("easy")) {
								game = new Game(12, 4, false, false, 6);
								game.playGame();
							} else if (command[1].equalsIgnoreCase("normal")) {
								game = new Game(12, 4, true, true, 6);
								game.playGame();
							} else if (command[1].equalsIgnoreCase("experienced")) {
								game = new Game(10, 4, true, true, 8);
								game.playGame();
							} else if (command[1].equalsIgnoreCase("expert")) {
								game = new Game(10, 5, true, true, 8);
								game.playGame();
							} else if (command[1].equalsIgnoreCase("set")) {
								System.out.println("Please Enter Max Attempts: ");
								int maxAttempts = set.nextInt();
								System.out.println("Please Enter Game Board Width: ");
								int solutionSize = set.nextInt();
								System.out.println("Please Enter If Markers are Shuffled (true/false): ");
								boolean shuffle = Boolean.valueOf(set.next()).booleanValue();
								System.out.println("Please Enter If Colors can be repeated (true/false): ");
								boolean allowRepeat = Boolean.valueOf(set.next()).booleanValue();
								System.out.println("Please Enter Numbers of Colors: ");
								int nbColors = set.nextInt();
								game = new Game(maxAttempts, solutionSize, shuffle, allowRepeat, nbColors);
								game.playGame();
							} else {
								correctGameLaunch = false;
								Display.displayInvalidCommand();
							}
							break;
						default:
							break;
					}
					if (correctGameLaunch) {
						System.out.println("You are returning to the home page in 3...");
						try {
							Thread.sleep(1000);
							System.out.println("                                      2...");
							Thread.sleep(1000);
							System.out.println("                                      1...");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						;
						Display.displayHomePage();
					}
				} else {
					Display.displayAlreadyInGame();
				}
				return true;
			case "solve":
				if (game != null) {
					Display.displaySolution(game.getSolution());
					return false;
				} else {
					Display.displayNotInGame();
					return true;
				}
			case "quit":
				System.out.println("The game is closing in 3...");
				try {
					Thread.sleep(1000);
					System.out.println("                     2...");
					Thread.sleep(1000);
					System.out.println("                     1...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
				return false;
			case "prev":
				if (game != null) {
					Display.displayGrid(game.getPlayerAttempts());
				} else {
					Display.displayNotInGame();
				}
				return true;
			case "home":
				if (game != null) {
					return false;
				} else {
					Display.displayNotInGame();
					return true;
				}
			default:
				return true;
		}
	}
}
