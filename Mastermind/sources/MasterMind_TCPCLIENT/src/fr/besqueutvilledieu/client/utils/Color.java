package fr.besqueutvilledieu.client.utils;

import java.util.ArrayList;

public enum Color {
	RED("\033[38;2;255;0;0m", "r", PawnType.GAME),
	GREEN("\033[38;2;0;255;0m", "g", PawnType.GAME),
	YELLOW("\033[38;2;255;255;0m", "y", PawnType.GAME),
	BLUE("\033[38;2;0;0;255m", "b", PawnType.GAME),
	PINK("\033[38;2;255;51;153m", "p", PawnType.GAME),
	WHITE("\033[38;2;255;255;255m", "w", PawnType.BOTH),
	TURQUOISE("\033[38;2;102;255;204m", "t", PawnType.GAME),
	ORANGE("\033[38;2;255;133;51m", "o", PawnType.GAME),
	WHITEPOINT("\033[38;2;255;255;255m", "W", PawnType.MARKER),
	GREYBLUE("\033[38;2;102;102;153m", "GB", PawnType.MARKER),
	RESET("\033[0;97m", "", PawnType.BOTH);

	String ANSI;
	String key;
	PawnType type;

	Color(String ANSI, String key, PawnType type) {
		this.ANSI = ANSI;
		this.key = key;
		this.type = type;
	}

	public static ArrayList<Color> getGamePawnColors(int nbColors) {
		ArrayList<Color> colors = new ArrayList<>();
		int i = 0;
		for (Color color : Color.values()) {
			if ((color.getColorMembership() == PawnType.GAME
					|| color.getColorMembership() == PawnType.BOTH) && i < nbColors) {
				colors.add(color);
				i++;
			}
		}
		return colors;
	}

	public static ArrayList<Color> getMarkersPawnColors() {
		ArrayList<Color> colors = new ArrayList<>();
		for (Color color : Color.values()) {
			if (color.getColorMembership() == PawnType.MARKER || color.getColorMembership() == PawnType.BOTH) {
				colors.add(color);
			}
		}
		return colors;
	}

	public PawnType getColorMembership() {
		return type;
	}

	public String getColorKey() {
		return key;
	}

	public static String[] getAllKeys() {
		Color[] colors = Color.values();
		String[] keys = new String[colors.length];
		for (int i = 0; i < colors.length; i++) {
			if (colors[i].type == PawnType.GAME) {
				keys[i] = colors[i].key;
			}
		}
		return keys;
	}

	public String getANSICode() {
		return ANSI;
	}
}
