import java.util.Hashtable;
import java.util.Map;

public class Game {
	static public final int POINTS = 10;
	Map<String, Integer> scoreBoard = null;

	public Game() {
		scoreBoard = new Hashtable<String, Integer>();
	}

	public void addNewPlayer(String name) {
		scoreBoard.put(name, 0);
	}

	public void givePoints(String name) {
		int v = scoreBoard.get(name);
		scoreBoard.put(name, POINTS + v);
	}

	public void print() {
		for (String name : scoreBoard.keySet()) {
			System.out.println(name + ":" + scoreBoard.get(name));
		}
	}
	public int count() {
		return scoreBoard.size();
	}
}
