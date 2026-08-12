//go:build amd64 && goexperiment.simd

package bb

import (
	"fmt"
	"strings"
	"testing"
)

func TestRaysEquivalence(t *testing.T) {

	t.Run("forwards inclusive", func(t *testing.T) {
		
		for i := range 100 {

			var bbs [8]uint64
			RaysInc[i].Forward.Store(&bbs)

			for dir := W; dir < E; dir++ {
				ray := BitBoard{
					lo: bbs[dir*2+0],
					hi: bbs[dir*2+1],
				}
				if ray != RayInc[i][dir] {
					s := strings.Builder{}
					fmt.Fprint(&s, "\nWanted:\n")
					Print(&s, RayInc[i][dir])
					fmt.Fprint(&s, "\nGot:\n")
					Print(&s, ray)
					t.Fatal(s.String())
				}
			}
		}

	})

	t.Run("backwards inclusive", func(t *testing.T) {
		
		for i := range 100 {

			var bbs [8]uint64
			RaysInc[i].Backward.Store(&bbs)

			for dir := E; dir <= SW; dir++ {
				ray := BitBoard{
					lo: bbs[(dir-4)*2+0],
					hi: bbs[(dir-4)*2+1],
				}
				if ray != RayInc[i][dir] {
					s := strings.Builder{}
					fmt.Fprint(&s, "\nWanted:\n")
					Print(&s, RayInc[i][dir])
					fmt.Fprint(&s, "\nGot:\n")
					Print(&s, ray)
					t.Fatal(s.String())
				}
			}
		}

	})

	t.Run("forwards exclusive", func(t *testing.T) {
		
		for i := range 100 {

			var bbs [8]uint64
			RaysExc[i].Forward.Store(&bbs)

			for dir := W; dir < E; dir++ {
				ray := BitBoard{
					lo: bbs[dir*2+0],
					hi: bbs[dir*2+1],
				}
				if ray != RayExc[i][dir] {
					s := strings.Builder{}
					fmt.Fprint(&s, "\nWanted:\n")
					Print(&s, RayInc[i][dir])
					fmt.Fprint(&s, "\nGot:\n")
					Print(&s, ray)
					t.Fatal(s.String())
				}
			}
		}

	})

	t.Run("backwards exclusive", func(t *testing.T) {
		
		for i := range 100 {

			var bbs [8]uint64
			RaysExc[i].Backward.Store(&bbs)

			for dir := E; dir <= SW; dir++ {
				ray := BitBoard{
					lo: bbs[(dir-4)*2+0],
					hi: bbs[(dir-4)*2+1],
				}
				if ray != RayExc[i][dir] {
					s := strings.Builder{}
					fmt.Fprint(&s, "\nWanted:\n")
					Print(&s, RayInc[i][dir])
					fmt.Fprint(&s, "\nGot:\n")
					Print(&s, ray)
					t.Fatal(s.String())
				}
			}
		}

	})

}
