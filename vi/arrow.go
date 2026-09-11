package vi

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
type Arrow struct {
	analytics []map[string]float64
}

func NewArrow() VI {
	return &Arrow{}
}

func (arrow *Arrow) Id() string {
	return "Arrow"
}

func (a *Arrow) Consult(
	board state.Board,
	timeLimit time.Duration,
) (state.Move, error) {

	move, analytics, err := mm.AlphaBeta(board, timeLimit, eval.QMinDist)
	if err != nil {
		return state.Move{}, ErrNoMoves
	}
	a.analytics = append(a.analytics, analytics[len(analytics)-1].Map())

	return move, nil
}

func (a *Arrow) Analytics() map[string]float64 {
	if len(a.analytics) == 0 {
		return nil
	}
	return a.analytics[len(a.analytics)-1]
}

func (a *Arrow) AllAnalytics() []map[string]float64 {
	return a.analytics
}
