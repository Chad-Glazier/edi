package ubc.cosc322.search;

import java.util.concurrent.TimeoutException;

public class TimeConstrained {
	private long endTimeMs = 0;
	private long timeLimitMs = 10 * 1000;

	public void setTimeLimit(int seconds) {
		this.timeLimitMs = seconds * 1000;
	}

	public void setTimeLimitMs(long milliseconds) {
		this.timeLimitMs = milliseconds;
	}

	protected void startTimer() {
		this.endTimeMs = timeLimitMs + System.currentTimeMillis();
	}

	protected void checkTime() throws TimeoutException {
		if (System.currentTimeMillis() > endTimeMs) {
			throw new TimeoutException();
		}
	}

	private int callsSinceLastCheck = 0;
	protected void checkTimeOccasionally() throws TimeoutException {
		if ((++callsSinceLastCheck & 1023) == 0) {
			if (System.currentTimeMillis() > endTimeMs) {
				throw new TimeoutException();
			}
		}
	}
}
