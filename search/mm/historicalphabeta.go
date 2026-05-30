package mm

import (
	"math"
	"time"

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

	maxDepth := 100 - board.Occupancy.Count()
	complete := make(chan bool)
	var bestMove *state.Move

	s := &historicAlphaBetaState{
		heuristic: heuristic,
		history:   history,
	}

	go func() {
		for depth := 1; depth <= maxDepth; depth++ {
			bestChildAtDepth := s.depthLimitedSearch(&board, depth)

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
	board *state.Board, depth int,
) *state.Board {

	children := board.Successors()
	if len(children) == 0 {
		return nil
	}

	s.history.Sort(children)

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
func (s *historicAlphaBetaState) alphaBeta(
	board *state.Board,
	alpha, beta float64,
	depth int, color float64,
) float64 {

	// We use the standard negamax implementation, with an added check to
	// update the history table.

	if depth == 0 {
		return color * s.heuristic(board)
	}

	children := board.Successors()
	if len(children) == 0 {
		return color * s.heuristic(board)
	}

	s.history.Sort(children)
	score := math.Inf(-1)
	for _, child := range children {
		result := -s.alphaBeta(&child, -beta, -alpha, depth-1, -color)
		if result > score {
			score = result
		}
		if score >= beta {
			s.history.IncreaseScore(&child, depth)
			break
		}
		alpha = max(alpha, score)
	}

	return score
}
