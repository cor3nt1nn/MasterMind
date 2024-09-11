package fr.besqueutvilledieu.client.gamegrid;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.pawn.GamePawn;
import fr.besqueutvilledieu.client.pawn.Pawn;
import fr.besqueutvilledieu.client.utils.Structurable;

class FullLineException extends IndexOutOfBoundsException {
	private static final long serialVersionUID = 1L;

	public FullLineException() {
		super("Line is full");
	}
}

class FullByCollectionException extends IndexOutOfBoundsException {
	private static final long serialVersionUID = 1L;

	public FullByCollectionException() {
		super("Collection too big to be added");
	}

}

public class Line implements Structurable {
	private final LinkedList<Pawn> gridLine = new LinkedList<>();
	private int maxSize;

	public Line(int size) {
		this.maxSize = size;
	}

	public LinkedList<Pawn> getLinkedList() {
		return gridLine;
	}

	// temporary
	// public boolean fillRandomly(boolean withRepeat) {
	// if (gridLine.size() > maxSize)
	// throw new FullLineException();
	// else {
	// LinkedList<Pawn> pawns = new LinkedList<>();
	// ArrayList<Color> c = Color.getGamePawnColors();
	// Random r = new Random();
	// for (int i = gridLine.size(); i < this.maxSize(); i++) {
	// GamePawn p = new GamePawn(c.get(r.nextInt(c.size())), i);
	// pawns.add(p);
	// }
	// return gridLine.addAll(pawns);
	// }
	// }
	public boolean fillRandomly(boolean allowRepeat, int nbColors) {
		if (gridLine.size() > maxSize)
			throw new FullLineException();
		else {
			LinkedList<Pawn> pawns = new LinkedList<>();
			ArrayList<Color> availableColors = new ArrayList<>(Color.getGamePawnColors(nbColors));

			Random r = new Random();
			for (int i = gridLine.size(); i < this.maxSize(); i++) {
				Color chosenColor;
				if (allowRepeat) {
					chosenColor = availableColors.get(r.nextInt(availableColors.size()));
				} else {
					int index = r.nextInt(availableColors.size());
					chosenColor = availableColors.remove(index);
				}
				GamePawn p = new GamePawn(chosenColor, i);
				pawns.add(p);
			}
			return gridLine.addAll(pawns);
		}
	}

	public void addAllColors(Collection<? extends Color> c) {
		int xPos = 0;
		for (Color color : c) {
			gridLine.add(new GamePawn(color, xPos));
			xPos++;
		}
	}

	public boolean addAll(Collection<? extends Pawn> c) {
		if (c.size() + gridLine.size() > maxSize)
			throw new FullByCollectionException();
		return gridLine.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Pawn> c) {
		if (c.size() + gridLine.size() > maxSize)
			throw new FullByCollectionException();
		return gridLine.addAll(index, c);
	}

	public boolean add(Pawn e) {
		if (gridLine.size() > maxSize)
			throw new FullLineException();
		return gridLine.add(e);
	}

	public void add(int index, Pawn element) {
		if (gridLine.size() > maxSize)
			throw new FullLineException();
		gridLine.add(index, element);
	}

	public void remove(int index) {
		gridLine.remove(index);
	}

	public void shuffle() {
		Collections.shuffle(gridLine);
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

}
