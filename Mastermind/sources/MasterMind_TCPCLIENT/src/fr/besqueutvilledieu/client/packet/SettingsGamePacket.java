package fr.besqueutvilledieu.client.packet;

/*
 * PACKET FOR DISPLAYED MESSAGES
 * 
 */
public class SettingsGamePacket implements Deliverable {
	private String message;

	public SettingsGamePacket(String message) {
		this.message = message;
	}

	public static String prefix = "[" + PacketType.SETTINGS_GAME.toString() + "]";

	@Override
	public String description() {
		return "Packet to allow client to receive game settings";
	}

	@Override
	public PacketType type() {
		return PacketType.SETTINGS_GAME;
	}

	@Override
	// SPLIT USING PIPE
	public String toString() {
		return prefix + message;
	}

	public String getMessage() {
		return message;
	}

}
