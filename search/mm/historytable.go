package mm

import (
	"sort"

	"github.com/Chad-Glazier/edi/state"
)

const maxHistory int32 = 2 << 13

// A history table is used to track which moves have produced cutoffs in the
// past and increments their score accordingly. When searching the game tree,
// the history table can be used to order child states. Since this approach
// only involves table lookups, it's much faster than just about any other
// ordering method, and the quality of the ordering has been shown to be far
// more effective than ordering by evaluation scores.
type HistoryTable struct {
	scores [2][100][100][100]int32
}

func (history *HistoryTable) score(board *state.Board) *int32 {
	return &history.
		scores[board.Player][board.Move.From][board.Move.To][board.Move.Arrow]
}

// Retrieves the history score of the current state's move.
func (history *HistoryTable) GetScore(state *state.Board) int32 {
	return *history.score(state)
}

// Increases the history score of a state's move. You should call this whenever
// a move produces a cutoff.
func (history *HistoryTable) IncreaseScore(state *state.Board, depth int) {
	score := history.score(state)

	bonus := min(int32(depth*depth), maxHistory)
	initial := *score

	*score = bonus - initial*bonus/maxHistory
}

type stateSorter struct {
	states  *state.SuccessorSlice
	history *HistoryTable
}

func (s *stateSorter) Len() int {
	return int(s.states.Len)
}

func (s *stateSorter) Less(i, j int) bool {
	return *s.history.score(&s.states.Arr[i]) > *s.history.score(&s.states.Arr[j])
}

func (s *stateSorter) Swap(i, j int) {
	s.states.Arr[i], s.states.Arr[j] = s.states.Arr[j], s.states.Arr[i]
}

// Sorts a slice of states in-place, in descending order by their history
// scores.
func (history *HistoryTable) Sort(successors *state.SuccessorSlice) {
	sort.Sort(&stateSorter{
		states:  successors,
		history: history,
	})
}
