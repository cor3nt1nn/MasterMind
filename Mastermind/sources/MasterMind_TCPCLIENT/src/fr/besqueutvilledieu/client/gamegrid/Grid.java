package fr.besqueutvilledieu.client.gamegrid;

import java.util.ArrayList;
import java.util.Iterator;

import fr.besqueutvilledieu.client.utils.Structurable;

public class Grid implements Structurable {
    private final ArrayList<ArrayList<Line>> gameAttemptsGrid;
    private int maxSize;

    public Grid(int maxSize) {
        this.maxSize = maxSize;
        this.gameAttemptsGrid = new ArrayList<ArrayList<Line>>();
    }

    public ArrayList<ArrayList<Line>> getGameAttemptsGrid() {
        return gameAttemptsGrid;
    }

    public void addAttempt(Line gamePawns, Line markersPawns) {
        if (this.getSize() < this.maxSize) {
            ArrayList<Line> attempt = new ArrayList<>();
            attempt.add(0, gamePawns);
            attempt.add(1, markersPawns);
            this.gameAttemptsGrid.add(attempt);
        }
    }

    public Iterator<ArrayList<Line>> getAttempts() {
        Iterator<ArrayList<Line>> it = this.getGameAttemptsGrid().iterator();
        return it;
    }

    public int getSize() {
        return this.gameAttemptsGrid.size();
    }

    public boolean isFull() {
        return getSize() == maxSize();
    }

    public boolean isEmpty() {
        return gameAttemptsGrid.isEmpty();
    }

    public ArrayList<Line> getLastAttempt() {
        return this.gameAttemptsGrid.get(getSize() - 1);
    }

    @Override
    public int maxSize() {
        return maxSize;
    }
}
