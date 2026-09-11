package vi

import (
	"os"
	"runtime/pprof"
	"testing"
	"time"

	"github.com/Chad-Glazier/edi/state"
)

func BenchmarkEDI(b *testing.B) {
	b.Run("10s", func(b *testing.B) {
		os.MkdirAll("profiles", 0755)
		f, err := os.Create("profiles/edi_initial_10s.pb.gz")
		if err != nil {
			b.Fatal("failed to create profile files")
		}
		defer f.Close()

		err = pprof.StartCPUProfile(f)
		if err != nil {
			b.Fatal("failed to create start profiling")
		}
		defer pprof.StopCPUProfile()

		edi := NewEDI()
		board := state.Initial()

		for b.Loop() {
			edi.Consult(board, 10 * time.Second)
		}
	})
}
