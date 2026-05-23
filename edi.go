package edi

import (
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/search/mm"
	"github.com/Chad-Glazier/edi/state"
)

type EDIAnalytics []mm.AlphaBetaAnalytics

// EDI is the flagship VI for this project. At the time of writing she uses
// alpha-beta search with the History Heuristic for move ordering and the
// KMinDist function for leaf node evaluation.
type EDI struct {
	history   *mm.HistoryTable
	analytics []EDIAnalytics
}

func NewEDI() VI {
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
) *state.Move {

	if edi.history == nil {
		edi.history = &mm.HistoryTable{}
	}

	move, analytics := mm.HistoricAlphaBetaWithAnalytics(
		board,
		timeLimit,
		eval.KMinDist,
		edi.history,
	)

	edi.analytics = append(edi.analytics, analytics)
	return move
}

func (edi *EDI) GetAnalytics() any {
	if len(edi.analytics) == 0 {
		return EDIAnalytics{}
	}
	return edi.analytics[len(edi.analytics)-1]
}

func (edi *EDI) GetAllAnalytics() any {
	return edi.analytics
}

func (edi *EDI) Id() string {
	return "EDI"
}
