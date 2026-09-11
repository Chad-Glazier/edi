package vi

import (
	"encoding/csv"
	"errors"
	"io"
	"strconv"
	"time"

	"github.com/Chad-Glazier/edi/state"
)

var (
	ErrNoMoves = errors.New("vi: no moves are possible from the given position")
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
	// 
	// If an error is returned, it will be [ErrNoMoves].
	Consult(board state.Board, timeLimit time.Duration) (state.Move, error)
	
	// Returns the analytics from the most recent call to [Consult]. The 
	// specific fields depends on the VI model. E.g., a VI using alpha-beta
	// might describe its effective branching factor and the number of cutoffs,
	// while a Monte Carlo model would have entirely different metrics. If 
	// [Consult] has not yet been successfully called, this function will 
	// return nil.
	Analytics() map[string]float64
	
	// Returns all analytics from past calls to ConsultWithAnalytics, ordered
	// so that the last element is from the most recent call. This value will
	// be a slice of the same type that GetAnalytics() returns.
	AllAnalytics() []map[string]float64
	
	// Returns a string that represents the VI's model.
	Id() string

}

func DumpAnalyticsCsv(vi VI, w io.Writer) error {
	analytics := vi.AllAnalytics()
	if len(analytics) == 0 {
		return nil
	}

	csvWriter := csv.NewWriter(w)
	defer csvWriter.Flush()

	// Write the headers.
	keys := make([]string, 0)
	for key := range analytics[0] {
		keys = append(keys, key)
	}
	if err := csvWriter.Write(keys); err != nil {
		return err
	}

	// Write the rows.
	for _, a := range analytics {
		record := make([]string, len(keys))
		for i, key := range keys {
			record[i] = strconv.FormatFloat(a[key], 'f', 6, 64)
		}
		if err := csvWriter.Write(record); err != nil {
			return err
		}
	}

	return nil
}
