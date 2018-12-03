
/**
 * GameServer.java
 *
 * This program implements a simple multithreaded chat server.  Every client that
 * connects to the server can broadcast data to all other clients.
 * The server stores an ArrayList of sockets to perform the broadcast.
 *
 * The GameServer uses a ClientHandler whose code is in a separate file.
 * When a client connects, the MTServer starts a ClientHandler in a separate thread
 * to receive messages from the client.
 *
 * To test, start the server first, then start multiple clients and type messages
 * in the client windows.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;

public class GameServer implements Runnable {
	// Maintain list of all client sockets for broadcast
	private ArrayList<Socket> socketList;
	Game game = null;
	String[] questions = { "What is the largest city in California?" };
	String[] answers = { "Los Angeles" };
	int curr_question_index = 0;
	int curr_answer_index = 0;
	volatile boolean answered = false;
	private final int MIN_PLAYERS = 2;
	private final String GOODBYEMSG = "Thanks for playing. See you again.";
	private final String STARTMSG =  "Trivia game is starting in 5(s)";



	private void broadcastMsg(String msg) {
		for (Socket s : socketList) {
			DataOutputStream clientOutput;
			try {
				clientOutput = new DataOutputStream(s.getOutputStream());
				clientOutput.writeBytes(msg + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getNextQuestion() {
		if (curr_question_index >= questions.length) {
			this.curr_question_index = 0;
		}
		curr_answer_index = curr_question_index;
		return questions[curr_question_index++];
	}

	private String getAnswer() {
		return answers[curr_answer_index];
	}

	public void repond(String answer, String name) {
		if (answer.equals(this.getAnswer())) {
			game.givePoints(name);
			System.out.println("Answer correct");
		}
		String msg = "User "+name+" answered first and got "+Integer.toString(Game.POINTS)+ " points.";
		broadcastMsg(msg);
		answered = true;
		game.print();

	}

	public void run() {
		/* wait for atleast two players for trivia to start */
		while (game.count() < this.MIN_PLAYERS) {
			try {
				System.out.println("****Waiting for atleast two player. Currently num of players="+game.count()+"***");
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int count = 0;
		broadcastMsg(this.STARTMSG);
			try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while ( count++ < questions.length) {
			String currQuestion = getNextQuestion();
			answered = false;
			broadcastMsg(currQuestion);
			while(answered==false);
		}
		broadcastMsg(GOODBYEMSG);
		game.print();

	}

	public GameServer() {
		socketList = new ArrayList<Socket>();
		game = new Game();
	}

	private void getConnection() {
		// Wait for a connection from the client
		try {
			System.out.println("Waiting for client connections on port 7654.");
			ServerSocket serverSock = new ServerSocket(7654);
			// This is an infinite loop, the user will have to shut it down
			// using control-c
			while (true) {
				Socket connectionSock = serverSock.accept();
				// Add this socket to the list
				socketList.add(connectionSock);
				// Send to ClientHandler the socket and arraylist of all sockets
				ClientHandler handler = new ClientHandler(connectionSock, this);
				Thread theThread = new Thread(handler);
				theThread.start();
			}
			// Will never get here, but if the above loop is given
			// an exit condition then we'll go ahead and close the socket
			// serverSock.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws InterruptedException {
		GameServer server = new GameServer();
		Thread thread = new Thread(server);
		thread.start();
		server.getConnection();
		thread.join();
	}
}
