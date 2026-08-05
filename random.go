package edi

import (
	"math/rand/v2"
	"time"

	"github.com/Chad-Glazier/edi/state"
)

// This VI picks moves completely at random.
type Random struct{}

func NewRandom() VI {
	return &Random{}
}

func (r *Random) Consult(
	board state.Board, timeLimit time.Duration,
) *state.Move {
	successors := state.SuccessorSlice{}
	board.Successors(&successors)

	if successors.Len == 0 {
		return nil
	}
	return &successors.Arr[rand.IntN(int(successors.Len))].Move
}

func (r *Random) ConsultWithAnalytics(
	board state.Board, timeLimit time.Duration,
) *state.Move {
	return r.Consult(board, timeLimit)
}

func (r *Random) GetAnalytics() any {
	return nil
}

func (r *Random) GetAllAnalytics() any {
	return nil
}

func (r *Random) Id() string {
	return "Random"
}
