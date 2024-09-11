package fr.besqueutvilledieu.client.gamegrid;

import java.util.Collection;

import fr.besqueutvilledieu.client.pawn.Pawn;

public class Pattern extends Line {

	public Pattern(int size) {
		super(size);
	}

	public void fillPattern(boolean allowRepeat, int nbColors) {
		this.fillRandomly(allowRepeat, nbColors);
	}

	public void fillByCollection(Collection<? extends Pawn> c) {
		this.addAll(c);
	}
}
