package fr.besqueutvilledieu.server.packet;

public class LoginPacket implements Deliverable {
	private String username;

	public LoginPacket(String name) {
		this.setUsername(name);
	}

	public static String prefix = "[" + PacketType.LOGIN.toString() + "]";

	@Override
	public String description() {
		return "Packet that allow client to connect with username to server";
	}

	@Override
	public PacketType type() {
		return PacketType.LOGIN;
	}

	@Override
	public String toString() {
		return prefix + username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
