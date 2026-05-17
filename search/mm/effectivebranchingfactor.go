package mm

// Computes the effective branching factor of a depth-limited minimax search.
func effectiveBranchingFactor(nodes uint64, depth int) float64 {

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
