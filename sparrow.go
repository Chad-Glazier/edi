package edi

import (
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/search/mm"
	"github.com/Chad-Glazier/edi/state"
)

type SparrowAnalytics []mm.AlphaBetaAnalytics

// Sparrow uses a simple alpha-beta search with the k-mindist evaluation
// function and no move ordering.
type Sparrow struct {
	analytics []SparrowAnalytics
}

func NewSparrow() VI {
	return &Sparrow{}
}

func (arrow *Sparrow) Consult(
	board state.Board, timeLimit time.Duration,
) *state.Move {
	return mm.AlphaBeta(board, timeLimit, eval.KMinDist)
}

func (sparrow *Sparrow) ConsultWithAnalytics(
	board state.Board, timeLimit time.Duration,
) *state.Move {
	move, analytics := mm.AlphaBetaWithAnalytics(
		board, timeLimit, eval.KMinDist,
	)

	sparrow.analytics = append(sparrow.analytics, analytics)
	return move
}

func (sparrow *Sparrow) GetAnalytics() any {
	if len(sparrow.analytics) == 0 {
		return SparrowAnalytics{}
	}
	return sparrow.analytics[len(sparrow.analytics)-1]
}

func (sparrow *Sparrow) GetAllAnalytics() any {
	return sparrow.analytics
}

func (sparrow *Sparrow) Id() string {
	return "Sparrow"
}
