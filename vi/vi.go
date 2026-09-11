package vi

import (
	"time"

	"github.com/Chad-Glazier/edi/state"
)

// We use VI, short for "virtual intelligence," to refer to a program that can
// recommend a move from a given board state within a certain amount of time.
// In contrast with a search function, which is defined to be stateless, a VI
// may "remember" certain things between searches.
//
// The term VI is borrowed from a videogame:
// https://masseffect.fandom.com/wiki/Virtual_Intelligence. Traditionally we
// would call such a program "AI," but that term has been diluted in recent
// years.
type VI interface {

	// Determines the best move and returns it within the given time limit.
	Consult(board state.Board, timeLimit time.Duration) *state.Move
	
	// Returns the analytics from the most recent call to [Consult]. The 
	// concrete type depends on the VI.
	Analytics() any
	
	// Returns all analytics from past calls to ConsultWithAnalytics, ordered
	// so that the last element is from the most recent call. This value will
	// be a slice of the same type that GetAnalytics() returns.
	AllAnalytics() any
	
	// Returns a string that represents the VI's model.
	Id() string
}
