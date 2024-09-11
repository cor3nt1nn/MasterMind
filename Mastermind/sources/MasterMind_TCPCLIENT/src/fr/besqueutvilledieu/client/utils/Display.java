package fr.besqueutvilledieu.client.utils;

import java.util.ArrayList;
import java.util.Iterator;

import fr.besqueutvilledieu.client.gamegrid.Grid;
import fr.besqueutvilledieu.client.gamegrid.Line;
import fr.besqueutvilledieu.client.pawn.Pawn;

public class Display {
	public Display() {
	}

	public static void displayColoredBar(int lineJump, int width) {
		String[] colors = {
				"\033[38;2;255;0;0m",
				"\033[38;2;0;255;0m",
				"\033[38;2;255;255;0m",
				"\033[38;2;0;0;255m",
				"\033[38;2;255;51;153m",
				"\033[38;2;102;255;204m",
				"\033[38;2;255;133;51m"
		};
		int totalLength = width;
		int segmentLength = totalLength / (colors.length + 1);

		StringBuilder coloredBar = new StringBuilder();

		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < segmentLength; j++) {
				coloredBar.append(colors[i]);
				coloredBar.append("_");
			}
			coloredBar.append("\033[0m");
		}
		for (int i = 0; i < lineJump; i++) {
			coloredBar.append("\n");
		}
		System.out.println(coloredBar);
	}

	private static void displayName() {
		displayColoredBar(0, 80);
		System.out.println(
				" __  __    _    ____ _____ _____ ____    __  __ ___ _   _ ____\n"
						+ "|  \\/  |  / \\  / ___|_   _| ____|  _ \\  |  \\/  |_ _| \\ | |  _ \\       \033[38;2;153;102;51m|\033[0m\n"
						+ "| |\\/| | / _ \\ \\___ \\ | | |  _| | |_) | | |\\/| || ||  \\| | | | |      \033[38;2;153;102;51m|\033[0m\n"
						+ "| |  | |/ ___ \\ ___) || | | |___|  _ <  | |  | || || |\\  | |_| |      \033[38;2;153;102;51m|\033[0m\n"
						+ "|_|  |_/_/   \\_\\____/ |_| |_____|_| \\_\\ |_|  |_|___|_| \\_|____/       \033[38;2;153;102;51m|\033[0m");
		displayColoredBar(2, 80);
	}

	public static void displayGameBoardSize(int solutionSize, int maxAttempts) {
		System.out.println("\tYou are playing on a " + solutionSize + "-size board game\n");
		System.out.println("\tYou have " + maxAttempts + " to solve the puzzle\n");
		displayBottomSeparation();
	}

	public static void displayMessageToReturnToMenu() {
		System.out
				.println("\t Press ENTER TO come-back to starting menu.\n");
	}

	public static void displayGamePawns(int nbColors) {
		System.out.println("\t\t___________Color Pawns:___________\n");
		System.out.print("\t\t\t");
		int i = 0;
		if (nbColors == 6) {
			System.out.print("   ");
		}
		for (Color color : Color.values()) {
			if (color != Color.RESET
					&& (color.getColorMembership() == PawnType.GAME || color.getColorMembership() == PawnType.BOTH)
					&& i < nbColors) {
				System.out.print(color.getANSICode() + Character.toString(9608) + " " + Color.RESET.getANSICode());
				i++;
			}
		}
		System.out.print("\n\t\t\t");
		i = 0;
		if (nbColors == 6) {
			System.out.print("   ");
		}
		for (Color color : Color.values()) {
			if (color != Color.RESET
					&& (color.getColorMembership() == PawnType.GAME || color.getColorMembership() == PawnType.BOTH)
					&& i < nbColors) {
				System.out.print(color.getANSICode() + color.getColorKey() + " " + Color.RESET.getANSICode());
				i++;
			}
		}
		System.out.print("\n");
		displayBottomSeparation();
	}

	public static void displayMarkersHelp() {
		System.out.println("\n\t\t___________Markers:___________\n");
		System.out.println(
				"\tCorrect color, wrong position : " + Color.WHITE.getANSICode() + Character.toString(120)
						+ Color.RESET.getANSICode());
		System.out.println(
				"\tCorrect color, well placed : " + Color.GREYBLUE.getANSICode() + Character.toString(120)
						+ Color.RESET.getANSICode());
		displayBottomSeparation();
	}

	public static void displayHomeCommands() {
		System.out.println("\n\t\t___________Commands:___________\n");
		System.out.println("\tEnter /pawns to display colors game pawns");
		System.out.println("\tEnter /marks to display markers pawns");
		System.out.println("\tEnter /help to see home commands again");
		System.out.println("\tEnter /load [filename] to load a backup");
		System.out.println(
				"\tEnter /new,");
		System.out.println(
				"\t      /new [easy/normal/experienced/expert],              to start a new game");
		System.out.println(
				"\t      /new set");
		System.out.println(
				"\tEnter /multi [username] [Ip_Address] [Port] to start a new game on multiplayer mode (in case the server is running)");
		System.out.println("\tEnter /quit to leave the MasterMind program\n");
		displayBottomSeparation();
	}

	public static void displayHomePage() {
		displayTopSeparation();
		displayName();
		displayBottomSeparation();
	}

	public static void displayGameCommands(int solutionSize) {
		System.out.println("\n\t\t___________Commands:___________\n");
		System.out.print("\tEnter");
		for (int i = 0; i < solutionSize; i++) {
			System.out.print(" x");
		}
		System.out.println(" to make a guess attempt where x is the color of the pawn placed");
		System.out.println("\tEnter /pawns to display colors game pawns");
		System.out.println("\tEnter /size to display current board game size");
		System.out.println("\tEnter /marks to display markers pawns");
		System.out.println("\tEnter /help to see game commands again");
		System.out.println("\tEnter /save [filename] to save the actual state of the game");
		System.out.println("\tEnter /solve to view the solution and come back to home");
		System.out.println("\tEnter /prev to view all your previous attempts");
		System.out.println("\tEnter /home to leave the game and return to home\n");
		System.out.println("\tEnter /quit to leave the MasterMind program\n");
		displayBottomSeparation();
	}

	private static void displayPawn(Pawn p) {
		System.out.print(p.getColor().getANSICode() + Character.toString(p.getCarCode()) + " "
				+ Color.RESET.getANSICode());
	}

	public static void displayLine(Line line, int lineY) {
		System.out.print("\n" + "Attempt " + lineY);
		System.out.print("\t\t\t");
		for (Pawn pawn : line.getLinkedList()) {
			displayPawn(pawn);
		}
		System.out.print("\n");
	}

	public static void displayNewGameMessage(int maxAttempts, int solutionSize, boolean shuffle, boolean allowRepeat,
			int nbColors) {
		displayTopSeparation();
		System.out.print("\t\tYou have started a new game\n\n");
		System.out.println("\t\t\tMAX ATTEMPTS: " + maxAttempts);
		System.out.println("\t\t\tBOARD WIDTH: " + solutionSize);
		System.out.println("\t\t\tMARKERS SHUFFLED: " + shuffle);
		System.out.println("\t\t\tCOLORS REPEAT: " + allowRepeat);
		System.out.println("\t\t\tNB COLORS: " + nbColors);
		displayBottomSeparation();
	}

	public static void displayWinMessage() {
		System.out.println("\n\t**************************************");
		System.out.println("\t*                                    *");
		System.out.println("\t*   Congratulations, you have won!   *");
		System.out.println("\t*                                    *");
		System.out.println("\t**************************************\n");
	}

	public static void displayLoseMessage() {
		System.out.println("\n\t************************************");
		System.out.println("\t*                                  *");
		System.out.println("\t*       Oops, you have lost!       *");
		System.out.println("\t*                                  *");
		System.out.println("\t************************************\n");
	}

	public static void displaySolution(Line line) {
		displayTopSeparation();
		System.out.print("\n" + "The solution was" + ":");
		System.out.print("\t\t\t");
		for (Pawn pawn : line.getLinkedList()) {
			displayPawn(pawn);
		}
		System.out.print("\n\n");
		displayBottomSeparation();
	}

	public static void displayAttempt(ArrayList<Line> attempt, int lineY) {
		Line pawnsLine = attempt.get(0);
		Line markersLine = attempt.get(1);
		System.out.print("\n" + "Attempt " + lineY);
		System.out.print("\t\t\t");
		for (Pawn pawn : pawnsLine.getLinkedList()) {
			displayPawn(pawn);
		}
		System.out.print("\t");
		for (Pawn marker : markersLine.getLinkedList()) {
			displayPawn(marker);
		}
		System.out.print("\n");
	}

	public static void displayGrid(Grid playerAttempts) {
		displayTopSeparation();
		int count = 1;
		Iterator<ArrayList<Line>> attempts = playerAttempts.getAttempts();
		while (attempts.hasNext()) {
			ArrayList<Line> attempt = attempts.next();
			displayAttempt(attempt, count);
			count++;
		}
	}

	public static void displayTopSeparation() {
		System.out.println("_______________________________________________________________\n");
	}

	public static void displayBottomSeparation() {
		System.out.println("\n_______________________________________________________________\n");
	}

	public static void displayHeader() {
		displayName();
		displayGamePawns(8);
		displayMarkersHelp();
		displayHomeCommands();
	}

	// ################ Errors Display ################
	public static void displayInvalidConnectionInputs() {
		System.out.println(Color.RED.getANSICode() +
				"\nImpossible to connect to the server. Check the format of the IP address and connection port. If this does not resolve the issue, the server is currently not reachable at these addresses."
				+ Color.RESET.getANSICode());
		Display.displayBottomSeparation();
	}

	public static void displayNotInGame() {
		System.out.println(Color.RED.getANSICode() + "\tYou are in the main menu, enter /new to start a game.\n"
				+ Color.RESET.getANSICode());
	}

	public static void displayAlreadyInGame() {
		System.out.println(Color.RED.getANSICode() + "\tYou are in a game, to come back to home, enter /home.\n"
				+ Color.RESET.getANSICode());
	}

	public static void displayInvalidPawnsInput() {
		System.out
				.println(Color.RED.getANSICode() + "\tSome colors of your input are not allowed in the current game.\n"
						+ Color.RESET.getANSICode());
	}

	public static void displayInvalidCommand() {
		System.out
				.println(Color.RED.getANSICode()
						+ "\tThis command does not exist, please take a look to allowed commands by entering /help.\n"
						+ Color.RESET.getANSICode());
	}
}
