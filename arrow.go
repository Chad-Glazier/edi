package edi

import (
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/search/mm"
	"github.com/Chad-Glazier/edi/state"
)

// Arrow is a program that was created by Martin Müller and Theodore Tegos,
// described in their paper "Experiments in Computer Amazons." They describe
// two versions of Arrow, one which is non-selective (no move ordering) and
// another which is selective (a beam search where moves are ordered by the
// same evaluation function used for leaf nodes). This implementation
// is non-selective. That is, it's just normal alpha-beta search which uses
// QMinDist to evaluate leaf nodes.
type Arrow struct{}

func NewArrow() VI {
	return &Arrow{}
}

func (arrow *Arrow) Consult(
	board state.Board, timeLimit time.Duration,
) *state.Move {
	return mm.AlphaBeta(board, timeLimit, eval.QMinDist)
}

func (arrow *Arrow) ConsultWithAnalytics(
	board state.Board, timeLimit time.Duration,
) (*state.Move, []mm.AlphaBetaAnalytics) {
	return mm.AlphaBetaWithAnalytics(board, timeLimit, eval.QMinDist)
}

func (arrow *Arrow) Id() string {
	return "arrow"
}
