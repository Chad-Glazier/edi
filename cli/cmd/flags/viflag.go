package flags

import (
	"fmt"

	"github.com/Chad-Glazier/edi"
)

const VI_USAGE = "a VI player: edi, arrow, or random"

type VI struct {
	Name string
	New  func() edi.VI
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
		v.New = edi.NewEDI
		v.Name = "edi"
	case "arrow":
		v.New = edi.NewArrow
		v.Name = "arrow"
	case "random":
		v.New = edi.NewRandom
		v.Name = "random"
	case "sparrow":
		v.New = edi.NewSparrow
		v.Name = "sparrow"
	default:
		return fmt.Errorf(`must be one of "edi", "arrow", "sparrow" or "random"`)
	}
	return nil
}

func (v *VI) Type() string {
	return "VI"
}
