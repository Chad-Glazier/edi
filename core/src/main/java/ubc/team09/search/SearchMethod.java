package ubc.team09.search;

import ubc.team09.state.State;

public interface SearchMethod {
	
	public void setBoard(State board);

	public void setTimeLimit(int seconds);

	public void setTimeLimitMs(long milliseconds);

	public void setShowOutput(boolean show);

	public int search();
}
