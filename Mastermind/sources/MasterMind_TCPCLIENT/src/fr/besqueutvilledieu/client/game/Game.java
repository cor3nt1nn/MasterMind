package fr.besqueutvilledieu.client.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import fr.besqueutvilledieu.client.gamegrid.Grid;
import fr.besqueutvilledieu.client.gamegrid.Line;
import fr.besqueutvilledieu.client.gamegrid.Pattern;
import fr.besqueutvilledieu.client.handler.ArchiveManager;
import fr.besqueutvilledieu.client.pawn.MarkerPawn;
import fr.besqueutvilledieu.client.pawn.Pawn;
import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.utils.Command;
import fr.besqueutvilledieu.client.utils.Display;

public class Game {
    Grid playerAttempts;
    Pattern solution;
    Display d;
    Scanner sc;
    Command cd;
    boolean continueGame;
    ArchiveManager am;

    /* Game Settings */
    int solutionSize;
    int maxAttempts;
    boolean shuffle;
    int nbColors;
    boolean allowRepeat;

    public Game(int maxAttempts, int solutionSize, boolean shuffle, boolean allowRepeat, int nbColors) {
        this.solutionSize = solutionSize;
        this.maxAttempts = maxAttempts;
        this.nbColors = nbColors;
        this.shuffle = shuffle;
        this.allowRepeat = allowRepeat;
        this.playerAttempts = new Grid(maxAttempts);
        this.solution = new Pattern(solutionSize);
        this.solution.fillPattern(allowRepeat, nbColors);
        this.d = new Display();
        this.sc = new Scanner(System.in);
        this.cd = new Command();
        this.continueGame = false;
        this.am = new ArchiveManager();

    }

    public Game() {
        this.solutionSize = 4;
        this.maxAttempts = 8;
        this.nbColors = 8;
        this.shuffle = false;
        this.allowRepeat = true;
        this.playerAttempts = new Grid(maxAttempts);
        this.solution = new Pattern(solutionSize);
        this.solution.fillPattern(allowRepeat, nbColors);
        this.d = new Display();
        this.sc = new Scanner(System.in);
        this.cd = new Command();
        this.continueGame = false;
        this.am = new ArchiveManager();
    }

    public Grid getPlayerAttempts() {
        return this.playerAttempts;
    }

    public int getSolutionSize() {
        return this.solutionSize;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    public Pattern getSolution() {
        return this.solution;
    }

    public void setSolution(Pattern solution) {
        this.solution = solution;
    }

    public void setSolutionSize(int solutionSize) {
        this.solutionSize = solutionSize;
    }

    public void setPlayerAttempts(Grid playerAttempts) {
        this.playerAttempts = playerAttempts;
    }

    public boolean getShuffle() {
        return this.shuffle;
    }

    public boolean getAllowRepeat() {
        return this.allowRepeat;
    }

    public int getNbColors() {
        return this.nbColors;
    }

    /* Game Loop */
    public void playGame() throws IOException, InterruptedException {
        Display.displayNewGameMessage(getMaxAttempts(), getSolutionSize(), getShuffle(), getAllowRepeat(),
                getNbColors());
        Display.displayGrid(playerAttempts);
        this.continueGame = true;
        while (continueGame) {
            String[] commands = cd.getCommandsFromUser();
            String[] colorsFromCommands = Command.retrieveColors(commands, getNbColors());
            if (colorsFromCommands != null) {
                Line attempt = new Line(solutionSize);
                ArrayList<Color> colors = cd.getColorsListFromColorKeys(colorsFromCommands);
                if (!(colors.size() == attempt.maxSize())) {
                    Display.displayInvalidCommand();
                } else {
                    attempt.addAllColors(colors);
                    playAttempt(attempt, getShuffle());
                    if (hasLost()) {
                        Display.displayLoseMessage();
                        Display.displaySolution(solution);
                        continueGame = false;
                    } else if (hasWon()) {
                        Display.displayWinMessage();
                        continueGame = false;
                    }
                }
            } else {
                String[] query = cd.retrieveCommand(commands);
                if (query != null) {
                    continueGame = cd.executeCommand(query, this);
                } else {
                    Display.displayInvalidCommand();
                }
            }
        }
    }

    private boolean hasWon() {
        if (!playerAttempts.isEmpty()) {
            Line lastAttemptMarkers = playerAttempts.getLastAttempt().get(1);
            if (lastAttemptMarkers.getLinkedList().size() == 0) {
                return false;
            } else {
                if (lastAttemptMarkers.getLinkedList().size() == getSolutionSize()) {
                    for (Pawn mp : lastAttemptMarkers.getLinkedList()) {
                        if (mp.getColor() != Color.GREYBLUE) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private boolean hasLost() {
        return !hasWon() && playerAttempts.isFull();
    }

    public Line getMarkersForAttempt(Line gamePawns, boolean shuffle) {
        Line markersLine = new Line(getSolutionSize());
        HashMap<Color, Integer> solutionColorCounts = new HashMap<>();
        LinkedList<Pawn> solutionLine = solution.getLinkedList();
        LinkedList<Pawn> guessedLine = gamePawns.getLinkedList();
        Color currentColor;
        Color guessedColor;
        for (int xPos = 0; xPos < getSolutionSize(); xPos++) {
            currentColor = solutionLine.get(xPos).getColor();
            solutionColorCounts.put(currentColor,
                    solutionColorCounts.getOrDefault(currentColor, 0) + 1);
        }
        for (int xPos = 0; xPos < getSolutionSize(); xPos++) {
            guessedColor = guessedLine.get(xPos).getColor();
            currentColor = solutionLine.get(xPos).getColor();
            if (guessedColor == currentColor) {
                markersLine.add(new MarkerPawn(Color.GREYBLUE, xPos, 120));
                solutionColorCounts.put(guessedColor, solutionColorCounts.get(guessedColor) - 1);
            } else {
                markersLine.add(new MarkerPawn(Color.WHITEPOINT, xPos, 46));
            }
        }
        for (int xPos = 0; xPos < getSolutionSize(); xPos++) {
            guessedColor = guessedLine.get(xPos).getColor();
            currentColor = solutionLine.get(xPos).getColor();
            if (solutionColorCounts.containsKey(guessedColor) && solutionColorCounts.get(guessedColor) > 0
                    && markersLine.getLinkedList().get(xPos).getColor() != Color.GREYBLUE) {
                markersLine.remove(xPos);
                markersLine.add(xPos, new MarkerPawn(Color.WHITE, xPos, 120));
                solutionColorCounts.put(guessedColor, solutionColorCounts.get(guessedColor) - 1);
            }
        }
        if (shuffle) {
            markersLine.shuffle();
        }
        return markersLine;
    }

    public void playAttempt(Line gamePawns, boolean shuffle) {
        this.playerAttempts.addAttempt(gamePawns, getMarkersForAttempt(gamePawns, shuffle));
        Display.displayAttempt(this.playerAttempts.getLastAttempt(),
                this.playerAttempts.getSize());
    }
}
