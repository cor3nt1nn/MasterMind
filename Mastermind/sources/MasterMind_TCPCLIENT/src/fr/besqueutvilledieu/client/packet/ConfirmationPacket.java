package fr.besqueutvilledieu.client.packet;

/*
 * PACKET FOR DISPLAYED MESSAGES
 * 
 */
public class ConfirmationPacket implements Deliverable {
	private String message;

	public ConfirmationPacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.CONFIRMATION_PACKET.toString() + "]";

	@Override
	public String description() {
		return "Packet that allows server to be sure that no packet has been dropped";
	}

	@Override
	public PacketType type() {
		return PacketType.CONFIRMATION_PACKET;
	}

	@Override
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
