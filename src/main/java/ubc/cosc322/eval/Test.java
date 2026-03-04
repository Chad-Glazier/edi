package ubc.cosc322.eval;

public class Test {
	public static void main() {
		RandomBoard board = new RandomBoard(0.30);

		MinDist heuristic = new MinDist();
		heuristic.setBoard(board.arr);

		heuristic.visualize();

		System.out.printf(
			"\tHeuristic evaluation for White: %.2f%%\n", 
			heuristic.evaluate(true) * 100
		);
		System.out.printf(
			"\tHeuristic evaluation for Black: %.2f%%\n\n", 
			heuristic.evaluate(false) * 100
		);
	}
}
