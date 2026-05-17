package edi

import (
	"math/rand/v2"
	"time"

	"github.com/Chad-Glazier/edi/state"
)

// This VI picks moves completely at random.
type Random struct{}

type RandomAnalytics struct{}

func NewRandom() VI[RandomAnalytics] {
	return &Random{}
}

func (r *Random) Consult(
	board state.Board, timeLimit time.Duration,
) *state.Move {

	children := board.Successors()

	if len(children) == 0 {
		return nil
	}
	return &children[rand.IntN(len(children))].Move
}

func (r *Random) ConsultWithAnalytics(
	board state.Board, timeLimit time.Duration,
) (*state.Move, RandomAnalytics) {

	children := board.Successors()

	if len(children) == 0 {
		return nil, RandomAnalytics{}
	}
	return &children[rand.IntN(len(children))].Move, RandomAnalytics{}
}

func (r *Random) Id() string {
	return "random"
}
