package fr.besqueutvilledieu.client.packet;

public class GameContinuePacket implements Deliverable {
	private String message;

	public GameContinuePacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.CONTINUE_GAME.toString() + "]";

	@Override
	public String description() {
		return "Packet that allows player to continue the running game";
	}

	@Override
	public PacketType type() {
		return PacketType.CONTINUE_GAME;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
