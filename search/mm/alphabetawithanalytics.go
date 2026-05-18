package mm

import (
	"math"
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/state"
)

type alphaBetaWithAnalytics struct {
	heuristic eval.EvalFunc
	analytics AlphaBetaAnalytics
}

// Conducts a simple alpha-beta search and collects analytics as it goes. This
// implementation involves more overhead than the regular AlphaBeta function so
// you should only use this version if the analytics are important.
//
// The returned search analytics slice contains analytics for each depth-
// limited search conducted during the iterative deepening process.
func AlphaBetaWithAnalytics(
	board state.Board,
	timeLimit time.Duration,
	heuristic eval.EvalFunc,
) (*state.Move, []AlphaBetaAnalytics) {

	turn := uint8(board.Occupancy.Count() - 8)
	maxDepth := 100 - board.Occupancy.Count()
	complete := make(chan bool)

	s := &alphaBetaWithAnalytics{
		heuristic: heuristic,
	}

	var bestMove *state.Move
	analytics := make([]AlphaBetaAnalytics, 1, maxDepth)

	go func() {
		for depth := 1; depth <= maxDepth; depth++ {

			s.analytics = AlphaBetaAnalytics{
				Depth:   depth,
				Cutoffs: make([]uint64, depth+1),
				Turn:    turn,
			}

			start := time.Now()
			bestChildAtDepth := s.depthLimitedSearch(&board, depth)
			s.analytics.Duration = time.Since(start)

			if bestChildAtDepth == nil {
				break
			}

			s.analytics.Ebf = effectiveBranchingFactor(
				s.analytics.InteriorNodes+s.analytics.LeafNodes,
				depth,
			)

			bestMove = &bestChildAtDepth.Move
			analytics = append(analytics, s.analytics)
		}
		complete <- true
	}()

	select {
	case <-time.After(timeLimit):
		return bestMove, analytics
	case <-complete:
		return bestMove, analytics
	}
}

// Conducts a depth-limited search from the specified state and returns the
// immediate child which has the best minimax score.
func (s *alphaBetaWithAnalytics) depthLimitedSearch(
	board *state.Board, depth int,
) *state.Board {

	children := board.Successors()
	if len(children) == 0 {
		return nil
	}

	var color float64
	if board.Player == state.WHITE {
		color = +1
	} else {
		color = -1
	}

	alpha := math.Inf(-1)
	beta := math.Inf(+1)
	var bestChild *state.Board

	for _, child := range children {

		score := -s.alphaBeta(&child, -beta, -alpha, depth-1, -color)

		if score > alpha {
			alpha = score
			bestChild = &child
		}

	}

	return bestChild
}

// Conducts a recursive search to find the minimax score of a state.
func (s *alphaBetaWithAnalytics) alphaBeta(
	board *state.Board,
	alpha, beta float64,
	depth int, color float64,
) float64 {

	// We use the standard negamax implementation, with an added check to
	// update the history table.

	if depth == 0 {
		s.analytics.LeafNodes++
		return color * s.heuristic(board)
	}

	children := board.Successors()
	if len(children) == 0 {
		s.analytics.LeafNodes++
		return color * s.heuristic(board)
	}

	score := math.Inf(-1)
	for _, child := range children {
		result := -s.alphaBeta(&child, -beta, -alpha, depth-1, -color)
		if result > score {
			score = result
		}
		if score >= beta {
			s.analytics.Cutoffs[depth]++
			break
		}
		alpha = max(alpha, score)
	}

	s.analytics.InteriorNodes++
	return score
}
