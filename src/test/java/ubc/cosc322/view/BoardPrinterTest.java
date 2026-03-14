package ubc.cosc322.view;

import org.junit.jupiter.api.Test;

public class BoardPrinterTest {
	@Test
	public void testDisplay() {
		BoardPrinter.printBoard(Util.initialBoard());
	}
}
