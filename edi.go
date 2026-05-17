package edi

import (
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/search/mm"
	"github.com/Chad-Glazier/edi/state"
)

// EDI is the flagship VI for this project. At the time of writing she uses
// alpha-beta search with the History Heuristic for move ordering and the
// KMinDist function for leaf node evaluation.
type EDI struct {
	history *mm.HistoryTable
}

func NewEDI() VI[[]mm.AlphaBetaAnalytics] {
	return &EDI{}
}

func (edi *EDI) Consult(
	board state.Board, timeLimit time.Duration,
) *state.Move {

	if edi.history == nil {
		edi.history = &mm.HistoryTable{}
	}

	return mm.HistoricAlphaBeta(
		board,
		timeLimit,
		eval.KMinDist,
		edi.history,
	)
}

func (edi *EDI) ConsultWithAnalytics(
	board state.Board, timeLimit time.Duration,
) (*state.Move, []mm.AlphaBetaAnalytics) {

	if edi.history == nil {
		edi.history = &mm.HistoryTable{}
	}

	return mm.HistoricAlphaBetaWithAnalytics(
		board,
		timeLimit,
		eval.KMinDist,
		edi.history,
	)
}

func (edi *EDI) Id() string {
	return "edi"
}
