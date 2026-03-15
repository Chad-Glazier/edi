package ubc.cosc322.search;

import ubc.cosc322.state.State;

public interface SearchMethod {
	public void setBoard(State board);
	public void setTimeLimit(int seconds);
	public void setTimeLimitMs(long milliseconds);
	public int search();
}
