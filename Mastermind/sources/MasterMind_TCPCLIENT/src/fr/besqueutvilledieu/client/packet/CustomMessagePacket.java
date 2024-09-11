package fr.besqueutvilledieu.client.packet;

/*
 * PACKET FOR DISPLAYED MESSAGES
 * 
 */
public class CustomMessagePacket implements Deliverable {
	private String message;

	public CustomMessagePacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.CUSTOM_MESSAGE.toString() + "]";

	@Override
	public String description() {
		return "Packet to allow displayed communication";
	}

	@Override
	public PacketType type() {
		return PacketType.CUSTOM_MESSAGE;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
