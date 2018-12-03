
/**
 * MtClient.java
 *
 * This program implements a simple multithreaded chat client.  It connects to the
 * server (assumed to be localhost on port 7654) and starts two threads:
 * one for listening for data sent from the server, and another that waits
 * for the user to type something in that will be sent to the server.
 * Anything sent to the server is broadcast to all clients.
 *
 * The MtClient uses a ClientListener whose code is in a separate file.
 * The ClientListener runs in a separate thread, recieves messages form the server,
 * and displays them on the screen.
 *
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.Scanner;

public class MtClient implements Runnable{
	/**
	 * main method.
	 *
	 * @throws InterruptedException
	 * @params not used.
	 */

	private Socket connectionSock = null;

	MtClient(Socket s) {
		connectionSock = s;
	}

	public void run() {
		/* listen to server messsage(s) and prints on the console */
		BufferedReader serverInput;
		try {
			serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
			while(true) {
				String serverText;
				try {
					serverText = serverInput.readLine();
					if (serverInput != null) {
						serverText = serverText.replace("\n", " ");
						System.out.println(serverText);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException {

		try {
			String hostname = "localhost";
			int port = 7654;

			System.out.println("Connecting to server on port " + port);
			Socket connectionSock = new Socket(hostname, port);

			System.out.println("Connection made.");
			MtClient mt = new MtClient(connectionSock);

			// Start a thread to listen and display data sent by the server
			ClientListener listener = new ClientListener(connectionSock);
			Thread theThread = new Thread(listener);
			theThread.start();
			Thread readerThread = new Thread(mt);
			readerThread.start();

			// Read input from the keyboard and send it to everyone else.
			// The only way to quit is to hit control-c, but a quit command
			// could easily be added.

			theThread.join();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
} // MtClient
