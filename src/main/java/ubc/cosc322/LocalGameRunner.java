package ubc.cosc322;

import java.util.ArrayList;
import java.util.Scanner;

// Import teammate's evaluation module
import ubc.cosc322.eval.MinDist;
import ubc.cosc322.eval.Util;

/**
 * Local Offline Test Runner
 * Allows testing of AI logic and legal move generation without connecting to the server.
 * Provides three different testing modes.
 */
public class LocalGameRunner {

    // Use the refactored board model
    private BoardState boardState;
    
    // Initialize teammate's conversion utility (2D -> 1D)
    private Util util = new Util();

    public LocalGameRunner() {
        this.boardState = new BoardState();
    }

    public static void main(String[] args) {
        LocalGameRunner runner = new LocalGameRunner();

        System.out.println("===================================");
        System.out.println("   AMAZONS LOCAL TEST RUNNER");
        System.out.println("===================================");
        System.out.println("1. Unit Test (Evaluate a specific board & territory)");
        System.out.println("2. Human vs AI (Play in console with Live Evaluation)");
        System.out.println("3. AI vs AI (Stress test generation)");
        System.out.print("Select a mode (1-3): ");

        Scanner scanner = new Scanner(System.in);
        int mode = scanner.nextInt();

        switch (mode) {
            case 1:
                runner.runUnitTesting();
                break;
            case 2:
                runner.runHumanVsAI(scanner);
                break;
            case 3:
                runner.runAIVsAI();
                break;
            default:
                System.out.println("Invalid mode selected.");
        }
        scanner.close();
    }

    // MODE 1: Unit Testing (used to test teammate's evaluation function)
    private void runUnitTesting() {
        System.out.println("\n--- RUNNING UNIT TEST ---");
        // Initialize a standard board as baseline
        initStandardBoard();
        printBoard();

        System.out.println("Board initialized.");
        
        // --- Connect to teammate's evaluation function ---
        // 1. Convert 2D array to 1D
        int[] flatBoard = util.convertBoard(this.boardState.board);
        
        // 2. Initialize MinDist evaluator
        MinDist evaluator = new MinDist();
        evaluator.setBoard(flatBoard);
        
        System.out.println("\n>> Tomato-Paste's Territory Visualization:");
        // 3. Print territory control visualization
        evaluator.visualize();
        
        // 4. Get score (playerIsWhite = true for white score, false for black score)
        System.out.printf("\n>> Current Score for White: %.2f%%\n", evaluator.evaluate(true) * 100);
        System.out.printf(">> Current Score for Black: %.2f%%\n", evaluator.evaluate(false) * 100);

        System.out.println("\nTotal White Legal Moves: " + boardState.generateLegalMoves(1, boardState.board).size());
    }

    // MODE 2: Human vs AI (Console interaction)
    private void runHumanVsAI(Scanner scanner) {
        System.out.println("\n--- HUMAN (White) vs AI (Black) ---");
        initStandardBoard();
        
        while (true) {
            printBoard();
            
            // --- Print live evaluation ---
            MinDist evaluator = new MinDist();
            evaluator.setBoard(util.convertBoard(this.boardState.board));
            System.out.printf("[Live Eval] White: %.1f%% | Black: %.1f%%\n\n", 
                    evaluator.evaluate(true) * 100, 
                    evaluator.evaluate(false) * 100);
            
            // 1. Human turn (White)
            System.out.println("Your turn (White). Enter your move as 6 numbers (qStartR qStartC qEndR qEndC arrR arrC) in 1-10 format:");
            System.out.print("> ");
            int sr = scanner.nextInt(); int sc = scanner.nextInt();
            int er = scanner.nextInt(); int ec = scanner.nextInt();
            int ar = scanner.nextInt(); int ac = scanner.nextInt();
            
            // Convert to 0-9 index and wrap into move object
            AmazonMove humanMove = new AmazonMove(
                new int[]{sr - 1, sc - 1}, 
                new int[]{er - 1, ec - 1}, 
                new int[]{ar - 1, ac - 1}
            );
            
            applyMoveLocally(humanMove, 1); // 1 = White
            
            if (isGameOver(2)) { // If Black has no moves left
                printBoard();
                System.out.println("Congratulations! You beat the AI.");
                break;
            }

            // 2. AI turn (Black)
            System.out.println("\nAI is thinking...");
            AmazonMove aiMove = getRandomMove(2); // Temporary random AI
            if (aiMove == null) {
                System.out.println("AI is out of moves! You win.");
                break;
            }
            
            applyMoveLocally(aiMove, 2); // 2 = Black
            System.out.println("AI played.");
            
            if (isGameOver(1)) {
                printBoard();
                System.out.println("Game Over! The AI wins.");
                break;
            }
        }
    }

    // MODE 3: AI vs AI (Stress test)
    private void runAIVsAI() {
        System.out.println("\n--- AI vs AI STRESS TEST ---");
        initStandardBoard();
        
        int turnCount = 0;
        int currentPlayer = 1; // White starts

        while (true) {
            AmazonMove move = getRandomMove(currentPlayer);
            
            if (move == null) {
                printBoard();
                String winner = (currentPlayer == 1) ? "Black (2)" : "White (1)";
                System.out.println("Game Over! " + winner + " wins after " + turnCount + " total moves.");
                break;
            }

            applyMoveLocally(move, currentPlayer);
            turnCount++;
            
            // Switch player (1 -> 2 -> 1)
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
    }

    // UTILITY METHODS

    /**
     * Manually set up the standard Amazons starting position
     */
    private void initStandardBoard() {
        int[][] b = this.boardState.board;
        // Clear board
        for (int r = 0; r < 10; r++) 
            for (int c = 0; c < 10; c++) 
                b[r][c] = 0;
        
        // White (1)
        b[0][3] = 1; b[0][6] = 1; b[3][0] = 1; b[3][9] = 1;
        // Black (2)
        b[9][3] = 2; b[9][6] = 2; b[6][0] = 2; b[6][9] = 2;
    }

    /**
     * Apply a move to the local board state
     */
    private void applyMoveLocally(AmazonMove move, int color) {
        boardState.board[move.qStart[0]][move.qStart[1]] = 0;     // Remove queen
        boardState.board[move.qEnd[0]][move.qEnd[1]] = color;     // Place queen
        boardState.board[move.arrow[0]][move.arrow[1]] = 3;       // Shoot arrow
    }

    /**
     * A very naive random AI (placeholder for testing)
     */
    private AmazonMove getRandomMove(int color) {
        ArrayList<AmazonMove> legalMoves = boardState.generateLegalMoves(color, boardState.board);
        if (legalMoves.isEmpty()) {
            return null; // No moves available
        }
        int randomIndex = (int) (Math.random() * legalMoves.size());
        return legalMoves.get(randomIndex);
    }

    /**
     * Check whether a player has no legal moves
     */
    private boolean isGameOver(int color) {
        return boardState.generateLegalMoves(color, boardState.board).isEmpty();
    }

    /**
     * Print a formatted 10x10 board to the console
     */
    private void printBoard() {
        System.out.println("\n   a b c d e f g h i j");
        System.out.println("   -------------------");
        for (int r = 9; r >= 0; r--) {
            System.out.print(String.format("%2d|", r + 1));
            for (int c = 0; c < 10; c++) {
                int val = boardState.board[r][c];
                if (val == 0) System.out.print(". ");
                else if (val == 1) System.out.print("W ");
                else if (val == 2) System.out.print("B ");
                else if (val == 3) System.out.print("X ");
            }
            System.out.println("|" + (r + 1));
        }
        System.out.println("   -------------------");
        System.out.println("   a b c d e f g h i j\n");
    }
}