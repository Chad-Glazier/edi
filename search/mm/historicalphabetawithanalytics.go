package mm

import (
	"math"
	"time"

	"github.com/Chad-Glazier/edi/bb"
	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/state"
)

type historicAlphaBetaWithAnalytics struct {
	heuristic eval.EvalFunc
	history   *HistoryTable
	analytics AlphaBetaAnalytics
}

// Conducts an alpha-beta search enhanced with the History Heuristic and
// collects analytics as it goes. This implementation involves more overhead
// than the regular HistoricAlphaBeta function so you should only use this
// version if the analytics are important.
//
// The returned search analytics slice contains analytics for each depth-
// limited search conducted during the iterative deepening process.
func HistoricAlphaBetaWithAnalytics(
	board state.Board,
	timeLimit time.Duration,
	heuristic eval.EvalFunc,
	history *HistoryTable,
) (*state.Move, []AlphaBetaAnalytics) {

	turn := uint8(bb.Count(board.Occupancy) - 8)
	maxDepth := 100 - bb.Count(board.Occupancy)
	complete := make(chan bool)

	s := &historicAlphaBetaWithAnalytics{
		heuristic: heuristic,
		history:   history,
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
func (s *historicAlphaBetaWithAnalytics) depthLimitedSearch(
	board *state.Board, depth int,
) *state.Board {

	successors := state.SuccessorSlice{}
	board.Successors(&successors)

	if successors.Len == 0 {
		return nil
	}

	s.history.Sort(&successors)

	var color float64
	if board.Player == state.WHITE {
		color = +1
	} else {
		color = -1
	}

	alpha := math.Inf(-1)
	beta := math.Inf(+1)
	var bestChild state.Board

	for i := range successors.Len {

		score := -s.alphaBeta(successors.Arr[i], -beta, -alpha, depth-1, -color)

		if score > alpha {
			alpha = score
			bestChild = successors.Arr[i]
		}

	}

	return &bestChild
}

// Conducts a recursive search to find the minimax score of a state.
func (s *historicAlphaBetaWithAnalytics) alphaBeta(
	board state.Board,
	alpha, beta float64,
	depth int, color float64,
) float64 {

	// We use the standard negamax implementation, with an added check to
	// update the history table.

	if depth == 0 {
		return color * s.heuristic(board)
	}

	successors := state.SuccessorSlice{}
	board.Successors(&successors)

	if successors.Len == 0 {
		return color * s.heuristic(board)
	}

	s.history.Sort(&successors)

	score := math.Inf(-1)
	for i := range successors.Len {
		result := -s.alphaBeta(
			successors.Arr[i],
			-beta, -alpha,
			depth-1,
			-color,
		)
		if result > score {
			score = result
		}
		if score >= beta {
			s.history.IncreaseScore(&successors.Arr[i], depth)
			s.analytics.Cutoffs[depth]++
			break
		}
		alpha = max(alpha, score)
	}

	s.analytics.InteriorNodes++
	return score
}
