import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
public class Main {
	static final int width = 8;
	static final int height = 8;
	static final char[] symb0 = new char[]{' ', '\u00b7', '\u205a', '\u2056', '\u2058'};
	static final char[] symb1 = new char[]{' ', '\u2606', '\u272f', '\u2605', '\u272a'};
	static final char[] symb2 = new char[]{' ', '-', '\u2591', '\u2592', '\u2588'};
	static final char[] symb3 = new char[]{' ', '-', '+', '\u256c', '\u2588'};
	static final char[] symb4 = new char[]{' ', '-', '+', '#', '\u2588'};
	static final char[] symb5 = new char[]{' ', '-', '+', 'W', '\u2588', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	static char[] dig = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	static char[] symb;
	static char[][] esc = new char[][]{new char[]{'\u001b', '[', '3', '7', 'm'}, new char[]{'\u001b', '[', '9', '1', 'm'}, new char[]{'\u001b', '[', '9', '4', 'm'}};
	static final int wm = width - 1;
	static final int hm = height - 1;
	static final BufferedWriter buffered = new BufferedWriter(new PrintWriter(System.out, false));
	static int[] board;
	enum Team {
		GREY, RED, BLUE
	}
	static Team[] teams = new Team[width * height];
	static Team on;
	static Team off;
	static int vP;
	static int vC;
	static int vL;
	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.print("\u001b[0m");
			}
		});
		symb = symb5;
		board = new int[width * height];
		Arrays.fill(board, 1);
		Arrays.fill(teams, Team.GREY);
		on = Team.BLUE;
		off = Team.RED;
		boolean k;
		String tS[];
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			display();
			if (on == Team.RED) {
				on = Team.BLUE;
				off = Team.RED;
				System.out.println("\u001b[94mBlue Player's Turn");
			}
			else {
				on = Team.RED;
				off = Team.BLUE;
				System.out.println("\u001b[91mRed Player's Turn");
			}
			k = false;
			while (!k) {
				tS = bReader.readLine().split(",");
				k = place(Integer.valueOf(tS[0]), Integer.valueOf(tS[1]));
			}
		}
	}
	static boolean place(int x, int y) {
		if (teams[(y * width) + x].equals(off)) {
			return false;
		}
		teams[(y * width) + x] = on;
		board[(y * width) + x]++;
		upd(x, y);
		return true;
	}
	static void set(int x, int y, int val) {
		board[(width * y) + x] = val;
	}
	static int max(int x , int y) {
		return (4 - (y == 0 ? 1 : 0) - (y == (hm) ? 1 : 0) - (x == 0 ? 1 : 0) - (x == (wm) ? 1 : 0));
	}
	static synchronized void upd(int x, int y) {
		int pos = (y * width) + x;
		boolean left = x == 0;
		boolean top = y == 0;
		boolean right = x == (width - 1);
		boolean bottom = y == (height - 1);
		boolean doT;
		boolean doL;
		boolean doR;
		boolean doB;
		int ab = pos - width;
		int lef = pos - 1;
		int rig = pos + 1;
		int bel = pos + width;
		int max = (4 - (top ? 1 : 0) - (bottom ? 1 : 0) - (left ? 1 : 0) - (right ? 1 : 0));
		while (board[pos] > max) {
			board[pos] -= max;
			if (!top) {
				board[ab]++;
				teams[ab] = on;
			}
			if (!left) {
				board[lef]++;
				teams[lef] = on;
			}
			if (!right) {
				board[rig]++;
				teams[rig] = on;
			}
			if (!bottom) {
				board[bel]++;
				teams[bel] = on;
			}
			doT = false;
			doL = false;
			doR = false;
			doB = false;
			if (!top && (board[ab] == (max(x, y - 1) + 1))) {
				doT = true;
			}
			if (!left && (board[lef] == (max(x - 1, y) + 1))) {
				doL = true;
			}
			if (!right && (board[rig] == (max(x + 1, y) + 1))) {
				doR = true;
			}
			if (!bottom && (board[bel] == (max(x, y + 1) + 1))) {
				doB = true;
			}
			if (doT) {
				upd(x, y - 1);
			}
			if (doL) {
				upd(x - 1, y);
			}
			if (doR) {
				upd(x + 1, y);
			}
			if (doB) {
				upd(x, y + 1);
			}
		}
	}
	static synchronized void display() throws Exception {
		vP = 0;
		vL = 0;
		buffered.write("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
		while (vL < height) {
			vC = 0;
			while (vC < width) {
				buffered.write(' ');
				buffered.write(esc[teams[vP].ordinal()]);
				buffered.write(symb[board[vP]]);
				vP++;
				vC++;
			}
			buffered.write("\r\n");
			vL++;
		}
		buffered.flush();
	}
}
