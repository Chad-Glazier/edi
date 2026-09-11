package vi

import (
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/search/mm"
	"github.com/Chad-Glazier/edi/state"
)

// EDI is the flagship VI for this project. At the time of writing it uses
// alpha-beta search with the History Heuristic for move ordering and the
// KMinDist function for leaf node evaluation.
type EDI struct {
	history   *mm.HistoryTable
	analytics []map[string]float64
}

func NewEDI() VI {
	return &EDI{}
}

func (e *EDI) Id() string {
	return "EDI"
}

func (e *EDI) Consult(
	board state.Board, 
	timeLimit time.Duration,
) (state.Move, error) {

	if e.history == nil {
		e.history = &mm.HistoryTable{}
	}

	move, analytics, err := mm.HistoricAlphaBeta(
		board,
		timeLimit,
		eval.KMinDist,
		e.history,
	)
	if err != nil {
		return state.Move{}, ErrNoMoves
	}
	e.analytics = append(e.analytics, analytics[len(analytics)-1].Map())

	return move, nil
}

func (e *EDI) Analytics() map[string]float64 {
	if len(e.analytics) == 0 {
		return nil
	}
	return e.analytics[len(e.analytics)-1]
}

func (e *EDI) AllAnalytics() []map[string]float64 {
	return e.analytics
}
