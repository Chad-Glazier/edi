package mm

import (
	"math"
	"time"

	"github.com/Chad-Glazier/edi/bb"
	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/state"
)

type historicAlphaBetaState struct {
	heuristic eval.EvalFunc
	history   *HistoryTable
}

// Creates a new search function using the Minimax algorithm with alpha-beta
// pruning and the history heuristic for move ordering. The history table will
// be mutated.
func HistoricAlphaBeta(
	board state.Board,
	timeLimit time.Duration,
	heuristic eval.EvalFunc,
	history *HistoryTable,
) *state.Move {

	maxDepth := 100 - bb.Count(board.Occupancy)
	complete := make(chan bool)
	var bestMove *state.Move

	s := &historicAlphaBetaState{
		heuristic: heuristic,
		history:   history,
	}

	go func() {
		for depth := 1; depth <= maxDepth; depth++ {
			bestChildAtDepth := s.depthLimitedSearch(board, depth)

			if bestChildAtDepth == nil {
				break
			}

			bestMove = &bestChildAtDepth.Move
		}
		complete <- true
	}()

	select {
	case <-time.After(timeLimit):
		return bestMove
	case <-complete:
		return bestMove
	}
}

// Conducts a depth-limited search from the specified state and returns the
// immediate child which has the best minimax score.
func (s *historicAlphaBetaState) depthLimitedSearch(
	board state.Board, depth int,
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

		score := -s.alphaBeta(
			successors.Arr[i],
			-beta, -alpha,
			depth-1,
			-color,
		)

		if score > alpha {
			alpha = score
			bestChild = successors.Arr[i]
		}

	}

	return &bestChild
}

// Conducts a recursive search to find the minimax score of a state.
func (s *historicAlphaBetaState) alphaBeta(
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
			break
		}
		alpha = max(alpha, score)
	}

	return score
}
