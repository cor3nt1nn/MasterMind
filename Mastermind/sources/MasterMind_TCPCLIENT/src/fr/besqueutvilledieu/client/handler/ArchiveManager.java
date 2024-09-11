package fr.besqueutvilledieu.client.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fr.besqueutvilledieu.client.gamegrid.Grid;
import fr.besqueutvilledieu.client.gamegrid.Line;
import fr.besqueutvilledieu.client.gamegrid.Pattern;
import fr.besqueutvilledieu.client.pawn.GamePawn;
import fr.besqueutvilledieu.client.pawn.MarkerPawn;
import fr.besqueutvilledieu.client.utils.Color;

public class ArchiveManager {

    public ArchiveManager() {
    }

    public void saveGame(Grid playerAttempts, Pattern solution, String name) {
        int solutionSize = solution.maxSize();
        int maxAttempts = playerAttempts.maxSize();
        String filename = ArchiveManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (filename.endsWith("/bin/")) {
            filename += "../";
        } else if (filename.endsWith("Client.jar")) {
            filename = filename.substring(0, filename.length() - "Client.jar".length());
        }
        filename += "saves/";

        File directorySaves = new File(filename);
        if (!directorySaves.exists()) {
            directorySaves.mkdir();
        }

        try {
            if (!name.endsWith(".txt")) {
                if (!name.contains(".")) {
                    name += ".txt";
                } else {
                    name = "temp.txt";
                }
            }
            filename += name;
            FileWriter writer = new FileWriter(filename);
            writer.write(String.valueOf(solutionSize) + "\n");
            writer.write(String.valueOf(maxAttempts) + "\n");
            for (int i = 0; i < solutionSize; i++) {
                writer.write(solution.getLinkedList().get(i).getColor().name());
                if (i < solutionSize - 1) {
                    writer.write(":");
                }
            }
            writer.write("\n");
            Iterator<ArrayList<Line>> it = playerAttempts.getAttempts();
            while (it.hasNext()) {
                ArrayList<Line> attempt = it.next();
                int markerQuantity = attempt.get(1).getLinkedList().size();
                for (int i = 0; i < solutionSize; i++) {
                    writer.write(attempt.get(0).getLinkedList().get(i).getColor().name());
                    if (i < solutionSize - 1) {
                        writer.write(":");
                    }
                    if (markerQuantity != 0 && i == solutionSize - 1) {
                        writer.write(":");
                    }
                }
                for (int i = 0; i < markerQuantity; i++) {
                    writer.write(attempt.get(1).getLinkedList().get(i).getColor().name());
                    if (i < markerQuantity - 1) {
                        writer.write(":");
                    }
                }
                writer.write("\n");
            }
            writer.close();
            System.out.println("\tData successfully saved in the file " + name + "\n");
        } catch (IOException e) {
            System.out.println("\tError while saving data: " + e.getMessage() + "\n");
        }
    }

    public Object[] loadGame(String name) {
        Object[] gameData = new Object[4];
        String filename = ArchiveManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (filename.endsWith("/bin/")) {
            filename += "../";
        } else if (filename.endsWith("Client.jar")) {
            filename = filename.substring(0, filename.length() - "Client.jar".length());
        }
        filename += "saves/";
        try {
            if (!name.endsWith(".txt")) {
                if (!name.contains(".")) {
                    name += ".txt";
                } else {
                    name = "temp.txt";
                }
            }
            filename += name;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            int solutionSize = Integer.parseInt(reader.readLine());
            int maxAttempts = Integer.parseInt(reader.readLine());

            String[] solutionColors = reader.readLine().split(":");
            Pattern solution = new Pattern(solutionSize);
            for (int i = 0; i < solutionSize; i++) {
                solution.add(new GamePawn(Color.valueOf(solutionColors[i]), i));
            }

            Grid playerAttempts = new Grid(maxAttempts);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] colors = line.split(":");
                Line attempt = new Line(solutionSize);
                Line markers = new Line(solutionSize);
                for (int i = 0; i < colors.length; i++) {
                    if (i < solutionSize) {
                        attempt.add(new GamePawn(Color.valueOf(colors[i]), i));
                    } else {
                        if (Color.valueOf(colors[i]) == Color.WHITEPOINT) {
                            markers.add(new MarkerPawn(Color.valueOf(colors[i]), i - solutionSize, 46));
                        } else {
                            markers.add(new MarkerPawn(Color.valueOf(colors[i]), i - solutionSize, 120));
                        }
                    }
                }
                playerAttempts.addAttempt(attempt, markers);
            }
            reader.close();

            gameData[0] = solutionSize;
            gameData[1] = maxAttempts;
            gameData[2] = playerAttempts;
            gameData[3] = solution;

            System.out.println("\tGame successfully loaded from the file " + filename + "\n");
        } catch (IOException e) {
            System.out.println("\tError loading the game: " + e.getMessage() + "\n");
        }
        return gameData;
    }

}
