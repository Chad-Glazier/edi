/*
Package mm implements minimax-based game tree search algorithms.
*/
package mm

import (
	"errors"
	"math"
	"time"

	"github.com/Chad-Glazier/edi/bb"
	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/state"
)

var (
	ErrNoMoves   = errors.New("mm: no moves are possible from the given position")
	ErrOutOfTime = errors.New("mm: the search was not completed in time")
)

type alphaBetaContext struct {
	heuristic eval.EvalFunc
	analytics AlphaBetaAnalytics
	deadline  time.Time
}

// Conducts a simple alpha-beta search.
//
// The returned search analytics slice contains analytics for each
// depth-limited search conducted during the iterative deepening process.
//
// If an error is returned, it will be [ErrNoMoves].
func AlphaBeta(
	board state.Board,
	timeLimit time.Duration,
	heuristic eval.EvalFunc,
) (state.Move, []AlphaBetaAnalytics, error) {

	if board.IsTerminal() {
		return state.Move{}, nil, ErrNoMoves
	}

	ctx := alphaBetaContext{
		heuristic: heuristic,
		deadline:  time.Now().Add(timeLimit),
	}

	var (
		turn      = uint8(bb.Count(board.Occupancy)-8) + 1
		maxDepth  = 100 - bb.Count(board.Occupancy)
		bestMove  state.Move
		analytics = make([]AlphaBetaAnalytics, 1, maxDepth)
	)

	for depth := 1; depth <= maxDepth; depth++ {

		ctx.analytics = AlphaBetaAnalytics{
			Depth:         depth,
			Cutoffs:       make([]uint64, depth+1),
			Turn:          turn,
			InteriorNodes: 0,
			LeafNodes:     0,
		}
		start := time.Now()

		bestChildAtDepth, err := ctx.depthLimitedSearch(board, depth)
		if err != nil {
			break
		}
		ctx.analytics.Duration = time.Since(start)

		analytics = append(analytics, ctx.analytics)
		bestMove = bestChildAtDepth.Move
	}

	return bestMove, analytics, nil
}

// Conducts a depth-limited search from the specified state and returns the
// immediate child which has the best minimax score.
//
// If an error is returned, it will be [ErrNoMoves] or [ErrOutOfTime].
func (ctx *alphaBetaContext) depthLimitedSearch(
	board state.Board,
	depth int,
) (state.Board, error) {

	successors := state.SuccessorSlice{}
	board.Successors(&successors)

	if successors.Length == 0 {
		return state.Board{}, ErrNoMoves
	}

	var (
		α         = math.Inf(-1)
		β         = math.Inf(+1)
		bestChild state.Board
	)

	for i := range successors.Length {

		score, err := ctx.alphaBeta(
			successors.Array[i],
			-β, -α,
			depth-1,
			color(board),
		)
		if err != nil {
			return state.Board{}, ErrOutOfTime
		}

		if score > α {
			α = score
			bestChild = successors.Array[i]
		}

	}

	return bestChild, nil
}

// Conducts a recursive search to find the minimax score of a state.
//
// If an error is returned, it will be [ErrOutOfTime].
func (ctx *alphaBetaContext) alphaBeta(
	board state.Board,
	α, β float64,
	depth int,
	color float64,
) (float64, error) {

	if ctx.outOfTime() {
		return 0.0, ErrOutOfTime
	}

	if depth == 0 {
		ctx.analytics.LeafNodes++
		return color * ctx.heuristic(board), nil
	}

	successors := state.SuccessorSlice{}
	board.Successors(&successors)

	if successors.Length == 0 {
		ctx.analytics.LeafNodes++
		return color * ctx.heuristic(board), nil
	}

	value := math.Inf(-1)
	for i := range successors.Length {

		result, err := ctx.alphaBeta(
			successors.Array[i],
			-β, -α,
			depth-1,
			-color,
		)
		if err != nil {
			return 0.0, err
		}

		value = max(value, -result)
		α = max(α, value)

		if α >= β {
			ctx.analytics.Cutoffs[depth]++
			break
		}
	}

	ctx.analytics.InteriorNodes++
	return value, nil
}

//
// Helper functions
//

const callsPerCheck = 1 << 10

var callsSinceLastCheck = 0

func (ctx *alphaBetaContext) outOfTime() bool {
	callsSinceLastCheck = (callsSinceLastCheck + 1) % callsPerCheck
	if callsSinceLastCheck == 0 {
		return time.Now().After(ctx.deadline)
	}
	return false
}

func color(board state.Board) float64 {
	if board.Player == state.White {
		return +1.0
	} else {
		return -1.0
	}
}
