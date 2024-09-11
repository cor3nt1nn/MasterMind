package fr.besqueutvilledieu.client.packet;

public class GameStartPacket implements Deliverable {
	private String message;

	public GameStartPacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.START_GAME.toString() + "]";

	@Override
	public String description() {
		return "Packet that allows player to start the game";
	}

	@Override
	public PacketType type() {
		return PacketType.START_GAME;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
