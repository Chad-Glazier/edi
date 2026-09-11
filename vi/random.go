package vi

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

func (r *Random) Id() string {
	return "Random"
}

func (r *Random) Consult(
	board state.Board, timeLimit time.Duration,
) (state.Move, error) {
	successors := state.SuccessorSlice{}
	board.Successors(&successors)

	if successors.Length == 0 {
		return state.Move{}, nil
	}
	return successors.Array[rand.IntN(int(successors.Length))].Move, nil
}

func (r *Random) Analytics() map[string]float64 {
	return nil
}

func (r *Random) AllAnalytics() []map[string]float64 {
	return nil
}
