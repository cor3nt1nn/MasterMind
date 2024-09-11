package fr.besqueutvilledieu.client.packet;

/*
 * PACKET FOR DISPLAYED MESSAGES
 * 
 */
public class EndConnectionPacket implements Deliverable {
	private String message;

	public EndConnectionPacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.END_CONNECTION.toString() + "]";

	@Override
	public String description() {
		return "Packet that allows users to be disconnected";
	}

	@Override
	public PacketType type() {
		return PacketType.END_CONNECTION;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
