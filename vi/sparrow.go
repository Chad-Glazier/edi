package vi

import (
	"time"

	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/search/mm"
	"github.com/Chad-Glazier/edi/state"
)

// Sparrow uses a simple alpha-beta search with the k-mindist evaluation
// function and no move ordering.
type Sparrow struct {
	analytics []map[string]float64
}

func NewSparrow() VI {
	return &Sparrow{}
}

func (arrow *Sparrow) Id() string {
	return "Sparrow"
}

func (s *Sparrow) Consult(
	board state.Board,
	timeLimit time.Duration,
) (state.Move, error) {

	move, analytics, err := mm.AlphaBeta(board, timeLimit, eval.KMinDist)
	if err != nil {
		return state.Move{}, ErrNoMoves
	}
	s.analytics = append(s.analytics, analytics[len(analytics)-1].Map())

	return move, nil
}

func (s *Sparrow) Analytics() map[string]float64 {
	if len(s.analytics) == 0 {
		return nil
	}
	return s.analytics[len(s.analytics)-1]
}

func (s *Sparrow) AllAnalytics() []map[string]float64 {
	return s.analytics
}
