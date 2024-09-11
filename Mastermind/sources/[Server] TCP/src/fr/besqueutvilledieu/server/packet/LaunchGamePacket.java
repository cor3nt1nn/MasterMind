package fr.besqueutvilledieu.server.packet;

public class LaunchGamePacket implements Deliverable {
	private String message;

	public LaunchGamePacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.LAUNCH_GAME.toString() + "]";

	@Override
	public String description() {
		return "Packet that allows player to launch the game";
	}

	@Override
	public PacketType type() {
		return PacketType.LAUNCH_GAME;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
