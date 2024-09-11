package fr.besqueutvilledieu.client.packet;

public class GameWaitPacket implements Deliverable {
	private String message;

	public GameWaitPacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.WAIT_FOR_TURN.toString() + "]";

	@Override
	public String description() {
		return "Packet that allows player to wait for all";
	}

	@Override
	public PacketType type() {
		return PacketType.WAIT_FOR_TURN;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
