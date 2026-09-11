package flags

import (
	"fmt"

	"github.com/Chad-Glazier/edi/vi"
)

const VI_USAGE = "a VI player: edi, arrow, sparrow or random"

type VI struct {
	Name string
	New  func() vi.VI
}

//
// Satisfy the flag interface for Cobra.
//
// https://pkg.go.dev/github.com/spf13/pflag#Value
//

func (v *VI) String() string {
	return v.Name
}

func (v *VI) Set(s string) error {
	switch s {
	case "edi":
		v.New = vi.NewEDI
		v.Name = "edi"
	case "arrow":
		v.New = vi.NewArrow
		v.Name = "arrow"
	case "random":
		v.New = vi.NewRandom
		v.Name = "random"
	case "sparrow":
		v.New = vi.NewSparrow
		v.Name = "sparrow"
	default:
		return fmt.Errorf(`must be one of "edi", "arrow", "sparrow" or "random"`)
	}
	return nil
}

func (v *VI) Type() string {
	return "VI"
}
