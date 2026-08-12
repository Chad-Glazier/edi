//go:build amd64 && goexperiment.simd

package bb

import "simd/archsimd"

type rays struct {
	Forward archsimd.Uint64x8
	Backward archsimd.Uint64x8
}

var RaysExc = [100]rays{}
var RaysInc = [100]rays{}

func init() {

	// Precompute the bitboards.
	for row := range 10 {
		for col := range 10 {
			
			forwardExc := [4]BitBoard{}
			forwardInc := [4]BitBoard{}
			for dir := W; dir < E; dir++ {
				forwardExc[dir] = exclusiveRay(row, col, dir)		
				forwardInc[dir] = inclusiveRay(row, col, dir)		
			}

			backwardExc := [4]BitBoard{}
			backwardInc := [4]BitBoard{}
			for dir := E; dir <= SW; dir++ {
				backwardExc[dir-4] = exclusiveRay(row, col, dir)
				backwardInc[dir-4] = inclusiveRay(row, col, dir)
			}

			RaysInc[row*10+col].Forward = archsimd.LoadUint64x8(&[8]uint64{
				forwardInc[0].lo, forwardInc[0].hi,
				forwardInc[1].lo, forwardInc[1].hi,
				forwardInc[2].lo, forwardInc[2].hi,
				forwardInc[3].lo, forwardInc[3].hi,
			})

			RaysExc[row*10+col].Forward = archsimd.LoadUint64x8(&[8]uint64{
				forwardExc[0].lo, forwardExc[0].hi,
				forwardExc[1].lo, forwardExc[1].hi,
				forwardExc[2].lo, forwardExc[2].hi,
				forwardExc[3].lo, forwardExc[3].hi,
			})

			RaysInc[row*10+col].Backward = archsimd.LoadUint64x8(&[8]uint64{
				backwardInc[0].lo, backwardInc[0].hi,
				backwardInc[1].lo, backwardInc[1].hi,
				backwardInc[2].lo, backwardInc[2].hi,
				backwardInc[3].lo, backwardInc[3].hi,
			})

			RaysExc[row*10+col].Backward = archsimd.LoadUint64x8(&[8]uint64{
				backwardExc[0].lo, backwardExc[0].hi,
				backwardExc[1].lo, backwardExc[1].hi,
				backwardExc[2].lo, backwardExc[2].hi,
				backwardExc[3].lo, backwardExc[3].hi,
			})
		}
	}
}
