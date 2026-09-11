package mm

import "time"

type AlphaBetaAnalytics struct {
	// The depth limit of the search.
	Depth int
	// The number of leaf nodes that were evaluated.
	LeafNodes uint64
	// The number of interior nodes that were expanded.
	InteriorNodes uint64
	// The time it took to complete the search at this depth.
	Duration time.Duration
	// The number of cutoffs made at each depth.
	Cutoffs []uint64
	// The turn that the search begins from. This is important because later
	// turns have more arrows, which significantly reduces the branching
	// factor.
	Turn uint8
}

// Returns the effective branching factor for the search.
func (a AlphaBetaAnalytics) Ebf() float64 {
	nodes := a.InteriorNodes + a.LeafNodes
	return ebf(nodes, a.Depth)
}

func (a AlphaBetaAnalytics) Map() map[string]float64 {

	m := make(map[string]float64, 6)

	m["depth"] = float64(a.Depth)
	m["leaf nodes"] = float64(a.LeafNodes)
	m["interior nodes"] = float64(a.InteriorNodes)
	m["duration (ms)"] = float64(a.Duration.Milliseconds())
	m["ebf"] = a.Ebf()
	m["turn"] = float64(a.Turn)

	return m
}

// Computes the effective branching factor of a depth-limited minimax search.
func ebf(nodes uint64, depth int) float64 {

	//
	// The effective branching factor of a minimax search is defined as the b*
	// which satisfies
	//
	//                 N + 1 = 1 + b* + b*^2 + ... + b*^d,
	//
	// where N is the number of visited nodes and d is the maximum depth. In
	// order to solve for b*, we will use a simple binary search with a fixed
	// number of iterations.
	//

	lo, hi := 1.0, 3000.0 // Amazons states never have more than 3000 children.
	for range 100 {

		// Our current guess for b*.
		b := (lo + hi) / 2

		sum := 0.0   // A value to hold the sum 1 + b* + b*^2 + ... + b*^d.
		power := 1.0 // Holds the current term in the sum.
		for range depth + 1 {
			sum += power
			power *= b
		}

		if sum > float64(nodes)+1 {
			hi = b
		} else {
			lo = b
		}
	}

	return (lo + hi) / 2
}
