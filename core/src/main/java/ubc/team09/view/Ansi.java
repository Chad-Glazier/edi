package ubc.team09.view;

public class Ansi {
	public static final String RESET = "\u001B[0m";

	public static final String FG_BLACK = "\u001B[30m";
	public static final String FG_RED = "\u001B[31m";
	public static final String FG_GREEN = "\u001B[32m";
	public static final String FG_YELLOW = "\u001B[33m";
	public static final String FG_BLUE = "\u001B[34m";
	public static final String FG_MAGENTA = "\u001B[35m";
	public static final String FG_CYAN = "\u001B[36m";
	public static final String FG_WHITE = "\u001B[37m";

	public static final String FG_BRIGHT_BLACK = "\u001B[90m";
	public static final String FG_BRIGHT_RED = "\u001B[91m";
	public static final String FG_BRIGHT_GREEN = "\u001B[92m";
	public static final String FG_BRIGHT_YELLOW = "\u001B[93m";
	public static final String FG_BRIGHT_BLUE = "\u001B[94m";
	public static final String FG_BRIGHT_MAGENTA = "\u001B[95m";
	public static final String FG_BRIGHT_CYAN = "\u001B[96m";
	public static final String FG_BRIGHT_WHITE = "\u001B[97m";

	public static final String BG_BLACK = "\u001B[40m";
	public static final String BG_RED = "\u001B[41m";
	public static final String BG_GREEN = "\u001B[42m";
	public static final String BG_YELLOW = "\u001B[43m";
	public static final String BG_BLUE = "\u001B[44m";
	public static final String BG_MAGENTA = "\u001B[45m";
	public static final String BG_CYAN = "\u001B[46m";
	public static final String BG_WHITE = "\u001B[47m";

	public static final String BG_BRIGHT_BLACK = "\u001B[100m";
	public static final String BG_BRIGHT_RED = "\u001B[101m";
	public static final String BG_BRIGHT_GREEN = "\u001B[102m";
	public static final String BG_BRIGHT_YELLOW = "\u001B[103m";
	public static final String BG_BRIGHT_BLUE = "\u001B[104m";
	public static final String BG_BRIGHT_MAGENTA = "\u001B[105m";
	public static final String BG_BRIGHT_CYAN = "\u001B[106m";
	public static final String BG_BRIGHT_WHITE = "\u001B[107m";

	public static final String BOLD = "\u001B[1m";
	public static final String DIM = "\u001B[2m";
	public static final String ITALIC = "\u001B[3m";
	public static final String UNDERLINE = "\u001B[4m";
	public static final String BLINK = "\u001B[5m";
	public static final String REVERSE = "\u001B[7m";
	public static final String HIDDEN = "\u001B[8m";
	public static final String STRIKETHROUGH = "\u001B[9m";

	public static final String ERASE_SCREEN = "\u001B[2J";
	public static final String RESET_CURSOR = "\u001B[H";
	public static final String MOVE_CURSOR_TO_LINE_START = "\u001B[1G";

	public static void clear(int line) {
		System.out.print("\u001b[" + Integer.toString(line) + "d");
		System.out.print(Ansi.MOVE_CURSOR_TO_LINE_START);
		System.out.print("\u001b[2K");
	}

	public static void saveCursor() {
		System.out.print("\u001B[s");
	}

	public static void restoreCursor() {
		System.out.print("\u001B[u");
	}

	public static void moveCursor(int line, int col) {
		System.out.print(String.format("\u001B[%d;%dH", line, col));
	}
}
