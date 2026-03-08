package ubc.cosc322;

import ubc.cosc322.eval.MinDist;
import ubc.cosc322.eval.Util;
import ubc.cosc322.search.AlphaBeta;
import ubc.cosc322.util.Move;

/**
 * AI Wrapper (Adapter)
 * Bridges 2D environment with the 1D BitBoard engine.
 */
public class GameAI {
    
    // Dynamic time limit passed from the Game Client
    private long allocatedTimeMs; 

    public GameAI(long allocatedTimeMs) {
        this.allocatedTimeMs = allocatedTimeMs;
    }

    public AmazonMove getBestMove(BoardState boardState, int myColor) {
        
        // 1. Translate color (1/2 to 0/1)
        byte engineColor = (myColor == 1) ? (byte) 0 : (byte) 1;

        // 2. Initialize engine with dynamic time limit
        AlphaBeta aiEngine = new AlphaBeta(new MinDist(), engineColor);
        aiEngine.setTimeLimitMs(this.allocatedTimeMs);
        aiEngine.showOutput(); 

        // 3. Feed the flattened board
        aiEngine.setBoard(Util.flatten(boardState.board));

        // 4. Execute search
        int encodedMove = aiEngine.execute();
        if (encodedMove == 0) return null; // Game over fallback

        // 5. Decode 1D coordinates
        byte startIdx = Move.start(encodedMove);
        byte endIdx   = Move.end(encodedMove);
        byte arrowIdx = Move.arrow(encodedMove);

        // 6. Convert to 2D coordinates [row, col]
        int[] startCoords = { startIdx / 10, startIdx % 10 };
        int[] endCoords   = { endIdx / 10, endIdx % 10 };
        int[] arrowCoords = { arrowIdx / 10, arrowIdx % 10 };

        return new AmazonMove(startCoords, endCoords, arrowCoords);
    }
}