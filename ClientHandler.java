
/**
 * ClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {
	private Socket connectionSock = null;

	private GameServer server;
	private String initialQuestion = "Please enter your name: ";
	private String name = null;

	ClientHandler(Socket sock, GameServer server) {
		this.connectionSock = sock;
		this.server = server;
	}

	public void run() {
		try {
			System.out.println("Connection made with socket " + connectionSock);
			DataOutputStream clientOutput = new DataOutputStream(connectionSock.getOutputStream());
			clientOutput.writeBytes(initialQuestion + "\n");

			BufferedReader clientInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
			name = clientInput.readLine();
			System.out.println("New player is " + name);
			server.game.addNewPlayer(name);
			while (true) {
				// Get into question answer loop for game
				String clientText = clientInput.readLine();
				if (clientText != null) {
					System.out.println("Answer from : " + name + " text: "+clientText);
					server.repond(clientText, name);
				}
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
}
