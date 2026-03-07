package ubc.cosc322.search;

public interface SearchMethod {
	void setBoard(long[] empty, byte[] white, byte[] black);
	int execute();
	void setTimeLimit(int seconds);
	void setTimeLimitMs(long milliseconds);
}
