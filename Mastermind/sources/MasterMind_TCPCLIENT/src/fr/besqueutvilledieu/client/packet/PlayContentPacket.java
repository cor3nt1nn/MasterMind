package fr.besqueutvilledieu.client.packet;

/*
 * PACKET FOR DISPLAYED MESSAGES
 * 
 */
public class PlayContentPacket implements Deliverable {
	private String message;

	public PlayContentPacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.PLAY_CONTENT.toString() + "]";

	@Override
	public String description() {
		return "Packet to allow client to send his move";
	}

	@Override
	public PacketType type() {
		return PacketType.PLAY_CONTENT;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
