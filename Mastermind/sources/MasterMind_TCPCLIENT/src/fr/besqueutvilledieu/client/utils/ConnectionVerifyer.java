package fr.besqueutvilledieu.client.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class ConnectionVerifyer{

	public static boolean isValidConnection(String address, int port) {
		boolean reached = true;
	
		try {
			SocketAddress testSocket = new InetSocketAddress(address, port);
			Socket checker = new Socket();
			checker.connect(testSocket, 8000);
			checker.close();
		} catch (SocketTimeoutException e) {
			System.out.println("Error the address used !");
			reached = false;
		} catch (IOException e) {
			System.out.println("Error while trying to connect: verify the address and the port");
			reached = false;
		}
		
		return reached;

	}
}
