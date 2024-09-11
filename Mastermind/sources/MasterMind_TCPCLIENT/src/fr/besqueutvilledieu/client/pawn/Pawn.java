package fr.besqueutvilledieu.client.pawn;

import fr.besqueutvilledieu.client.utils.Color;

public abstract class Pawn {
	private int carCode;
	private Color color;

	public Pawn(int carCode) {
		this.carCode = carCode;
	}

	protected void setColor(Color color) {
		this.color = color;
	}

	public int getCarCode() {
		return this.carCode;
	}

	public Color getColor() {
		return this.color;
	}
}
