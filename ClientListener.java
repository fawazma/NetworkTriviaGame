
/**
 * ClientListener.java
 *
 * This class runs on the client end and just
 * displays any text received from the server.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.ArrayList;
import java.util.Scanner;

public class ClientListener implements Runnable {
	private Socket connectionSock = null;

	ClientListener(Socket sock) {
		this.connectionSock = sock;
	}

	/**
	 * Gets message from server and dsiplays it to the user.
	 */
	public void run() {
		try {

			Scanner keyboard = new Scanner(System.in);
			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());
			while (true) {
					String data = keyboard.nextLine();
					serverOutput.writeBytes(data + "\n");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
} // ClientListener for MtClient
