package ubc.cosc322;

import java.util.Scanner;

import ubc.cosc322.eval.MinDist;
import ubc.cosc322.eval.Util;
import ubc.cosc322.search.AlphaBeta;
import ubc.cosc322.util.Move;

/**
 * Local Offline Runner
 * Allows testing of AI logic without connecting to the server.
 * Supports configurable dynamic time allocation for both players.
 */
public class LocalGameRunner {

    private BoardState boardState;
    
    // Independent time banks for White and Black (in milliseconds)
    private long whiteTimeBankMs;
    private long blackTimeBankMs;
    private long hardLimitPerMoveMs;

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

        // Prompt for Time Bank and Hard Limit if AI is involved
        if (mode == 2 || mode == 3) {
            System.out.print("Enter Total Time Bank in seconds (e.g., 1800 for 30m): ");
            long timeBankSec = scanner.nextLong();
            runner.whiteTimeBankMs = timeBankSec * 1000L;
            runner.blackTimeBankMs = timeBankSec * 1000L;

            System.out.print("Enter Hard Limit per move in seconds (e.g., 30): ");
            runner.hardLimitPerMoveMs = scanner.nextLong() * 1000L;
            
            System.out.println("\n[Setup Complete] Starting game...\n");
        }

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

    // MODE 1: Unit Testing
    private void runUnitTesting() {
        System.out.println("\n--- RUNNING UNIT TEST ---");
        initStandardBoard();
        printBoard();

        System.out.println("Board initialized.");
        
        int[] flatBoard = Util.flatten(this.boardState.board);
        
        MinDist evaluator = new MinDist();
        evaluator.setBoard(flatBoard);
        
        System.out.println("\n>> Tomato-Paste's Territory Visualization:");
        evaluator.visualize();
        
        // 0 is White, 1 is Black in MinDist
        System.out.printf("\n>> Current Score for White: %.2f%%\n", evaluator.evaluate((byte)0) * 100);
        System.out.printf(">> Current Score for Black: %.2f%%\n", evaluator.evaluate((byte)1) * 100);

        System.out.println("\nTotal White Legal Moves: " + boardState.generateLegalMoves(1, boardState.board).size());
    }

    // MODE 2: Human vs AI
    private void runHumanVsAI(Scanner scanner) {
        System.out.println("\n--- HUMAN (White) vs AI (Black) ---");
        initStandardBoard();
        
        while (true) {
            printBoard();
            
            MinDist evaluator = new MinDist();
            evaluator.setBoard(Util.flatten(this.boardState.board));
            System.out.printf("[Live Eval] White: %.1f%% | Black: %.1f%%\n\n", 
                    evaluator.evaluate((byte)0) * 100, 
                    evaluator.evaluate((byte)1) * 100);
            
            // Human Turn (White)
            System.out.println("Your turn (White). Enter your move as 6 numbers (qStartR qStartC qEndR qEndC arrR arrC) in 1-10 format:");
            System.out.print("> ");
            int sr = scanner.nextInt(); int sc = scanner.nextInt();
            int er = scanner.nextInt(); int ec = scanner.nextInt();
            int ar = scanner.nextInt(); int ac = scanner.nextInt();
            
            AmazonMove humanMove = new AmazonMove(
                new int[]{sr - 1, sc - 1}, 
                new int[]{er - 1, ec - 1}, 
                new int[]{ar - 1, ac - 1}
            );
            
            applyMoveLocally(humanMove, 1);
            
            if (isGameOver(2)) {
                printBoard();
                System.out.println("Congratulations! You beat the AI.");
                break;
            }

            // AI Turn (Black)
            System.out.println("\nAI is thinking...");
            AmazonMove aiMove = getAIMove(2);
            if (aiMove == null) {
                System.out.println("AI is out of moves! You win.");
                break;
            }
            
            applyMoveLocally(aiMove, 2);
            System.out.println("AI played.");
            
            if (isGameOver(1)) {
                printBoard();
                System.out.println("Game Over! The AI wins.");
                break;
            }
        }
    }

    // MODE 3: AI vs AI
    private void runAIVsAI() {
        System.out.println("\n--- AI vs AI STRESS TEST ---");
        initStandardBoard();
        
        int turnCount = 0;
        int currentPlayer = 1;

        while (true) {
            AmazonMove move = getAIMove(currentPlayer);
            
            if (move == null) {
                printBoard();
                String winner = (currentPlayer == 1) ? "Black (2)" : "White (1)";
                System.out.println("Game Over! " + winner + " wins after " + turnCount + " total moves.");
                
                // Print final time usage
                System.out.printf("\n[Final Time Remaining] White: %ds | Black: %ds\n", 
                        whiteTimeBankMs/1000, blackTimeBankMs/1000);
                break;
            }

            applyMoveLocally(move, currentPlayer);
            turnCount++;
            
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    private void initStandardBoard() {
        int[][] b = this.boardState.board;
        for (int r = 0; r < 10; r++) 
            for (int c = 0; c < 10; c++) 
                b[r][c] = 0;
        
        // White (1)
        b[0][3] = 1; b[0][6] = 1; b[3][0] = 1; b[3][9] = 1;
        // Black (2)
        b[9][3] = 2; b[9][6] = 2; b[6][0] = 2; b[6][9] = 2;
    }

    private void applyMoveLocally(AmazonMove move, int color) {
        boardState.board[move.qStart[0]][move.qStart[1]] = 0;
        boardState.board[move.qEnd[0]][move.qEnd[1]] = color;
        boardState.board[move.arrow[0]][move.arrow[1]] = 3;
    }

    /**
     * Time Manager and Engine execution for local testing
     */
    private AmazonMove getAIMove(int color) {
        String colorName = (color == 1) ? "White" : "Black";
        long remainingBank = (color == 1) ? whiteTimeBankMs : blackTimeBankMs;
        
        // 1. Calculate dynamic time allocation (10% of remaining time)
        long allocatedMs = remainingBank / 10;
        
        // 2. Cap at hard limit (Local testing doesn't need network buffers)
        allocatedMs = Math.min(allocatedMs, hardLimitPerMoveMs);
        allocatedMs = Math.max(allocatedMs, 1000L); // Minimum 1 second

        System.out.printf("[Local Time Manager] %s Bank: %ds | Allocated: %ds\n", 
                colorName, remainingBank/1000, allocatedMs/1000);

        long startTime = System.currentTimeMillis();

        // 3. Setup and Run AI Engine
        byte teammateColor = (color == 1) ? (byte) 0 : (byte) 1;
        AlphaBeta aiEngine = new AlphaBeta(new MinDist(), teammateColor);
        aiEngine.setTimeLimitMs(allocatedMs); 
        aiEngine.showOutput(); 
        aiEngine.setBoard(Util.flatten(this.boardState.board));

        int encodedMove = aiEngine.execute();
        
        // 4. Update the respective player's Time Bank
        long elapsedMs = System.currentTimeMillis() - startTime;
        if (color == 1) {
            whiteTimeBankMs = Math.max(0, whiteTimeBankMs - elapsedMs);
        } else {
            blackTimeBankMs = Math.max(0, blackTimeBankMs - elapsedMs);
        }

        if (encodedMove == 0) return null; 

        // 5. Decode results
        byte startIdx = Move.start(encodedMove);
        byte endIdx   = Move.end(encodedMove);
        byte arrowIdx = Move.arrow(encodedMove);

        int[] startCoords = { startIdx / 10, startIdx % 10 };
        int[] endCoords   = { endIdx / 10, endIdx % 10 };
        int[] arrowCoords = { arrowIdx / 10, arrowIdx % 10 };

        return new AmazonMove(startCoords, endCoords, arrowCoords);
    }

    private boolean isGameOver(int color) {
        return boardState.generateLegalMoves(color, boardState.board).isEmpty();
    }

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