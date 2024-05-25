import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
public class Main {
	static int width = 8;
	static int height = 8;
	static final char[] symb0 = new char[]{' ', '\u00b7', '\u205a', '\u2056', '\u2058'};
	static final char[] symb1 = new char[]{' ', '\u2606', '\u272f', '\u2605', '\u272a'};
	static final char[] symb2 = new char[]{' ', '-', '\u2591', '\u2592', '\u2588'};
	static final char[] symb3 = new char[]{' ', '-', '+', '\u256c', '\u2588'};
	static final char[] symb4 = new char[]{' ', '-', '+', '#', '\u2588'};
	static final char[] symb5 = new char[]{' ', '-', '+', 'W', '\u2588', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	static char[] dig = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	static char[] symb;
	static char[][] esc = new char[][]{new char[]{'\u001b', '[', '3', '7', 'm'}, new char[]{'\u001b', '[', '9', '1', 'm'}, new char[]{'\u001b', '[', '9', '4', 'm'}, new char[]{'\u001b', '[', '9', '2', 'm'}};
	static int wm = width - 1;
	static int hm = height - 1;
	static final BufferedWriter buffered = new BufferedWriter(new PrintWriter(System.out, false));
	static int[] board;
	static int[] teams;
	static int numTeams = 2;
	static int vP;
	static int vC;
	static int vL;
	static int ct = 2;
	static boolean teamOff[];
	static boolean tt[];
	static boolean win = false;
	public static void main(String[] args) throws Exception {
		if (args.length >= 1) {
			ct = numTeams = Integer.valueOf(args[0]);
		}
		teamOff = new boolean[numTeams];
		tt = new boolean[numTeams];
		if (args.length >= 3) {
			width = Integer.valueOf(args[1]);
			height = Integer.valueOf(args[2]);
			wm = width - 1;
			hm = height - 1;
		}
		teams = new int[width * height];
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.print("\u001b[0m");
			}
		});
		symb = symb5;
		board = new int[width * height];
		Arrays.fill(board, 1);
		Arrays.fill(teams, 0);
		boolean k;
		String tS[];
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			ct = (ct % numTeams) + 1;
			if (teamOff[ct - 1]) {
				continue;
			}
			display();
			if (win) {
				System.exit(0);
			}
			buffered.write(esc[ct]);
			buffered.flush();
			System.out.print("Player ");
			System.out.print(ct);
			System.out.println("\'s turn");
			k = false;
			while (!k) {
				int i;
				int j;
				while (true) {
					tS = bReader.readLine().split(",");
					if (tS.length != 2) {
						continue;
					}
					try {
						i = Integer.valueOf(tS[0]);
						j = Integer.valueOf(tS[1]);
					}
					catch (NumberFormatException E) {
						continue;
					}
					if ((i < 0) || (i >= width) || (j < 0) || (j >= height)) {
						continue;
					}
					break;
				}
				k = place(i, j);
			}
		}
	}
	static boolean place(int x, int y) {
		int pt = teams[(y * width) + x];
		if ((pt != 0) && (pt != ct)) {
			return false;
		}
		teams[(y * width) + x] = ct;
		board[(y * width) + x]++;
		upd(x, y);
		for (int i = 0; i < tt.length; i++) {
			tt[i] = true;
		}
		for (int t : teams) {
			if (t == 0) {
				return true;
			}
			tt[t - 1] = false;
		}
		for (int i = 0; i < teamOff.length; i++) {
			if (tt[i]) {
				teamOff[i] = true;
			}
		}
		return true;
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
		if (board[pos] >  max) {
			if (!top) {
				teams[ab] = ct;
			}
			if (!left) {
				teams[lef] = ct;
			}
			if (!right) {
				teams[rig] = ct;
			}
			if (!bottom) {
				teams[bel] = ct;
			}
		}
		boolean won = true;
		for (int i : teams) {
			if (i != ct) {
				won = false;
			}
		}
		if (won) {
			win = true;
			return;
		}
		while (board[pos] > max) {
			board[pos] -= max;
			if (!top) {
				board[ab]++;
			}
			if (!left) {
				board[lef]++;
			}
			if (!right) {
				board[rig]++;
			}
			if (!bottom) {
				board[bel]++;
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
				if (win) {
					return;
				}
			}
			if (doL) {
				upd(x - 1, y);
				if (win) {
					return;
				}
			}
			if (doR) {
				upd(x + 1, y);
				if (win) {
					return;
				}
			}
			if (doB) {
				upd(x, y + 1);
				if (win) {
					return;
				}
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
				buffered.write(esc[teams[vP]]);
				if (board[vP] >= symb.length) {
					buffered.write((int) 'X');
				}
				else {
					buffered.write(symb[board[vP]]);
				}
				vP++;
				vC++;
			}
			buffered.write("\r\n");
			vL++;
		}
		buffered.flush();
	}
}
