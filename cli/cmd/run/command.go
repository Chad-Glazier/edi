package run

import (
	"log"
	"time"

	"github.com/Chad-Glazier/edi/cli/cmd/flags"
	"github.com/Chad-Glazier/edi/cli/ui"
	"github.com/spf13/cobra"

	tea "charm.land/bubbletea/v2"
)

func RunCommand() *cobra.Command {

	var turnTimer time.Duration
	var white, black flags.VI

	cmd := &cobra.Command{
		Use:   "run",
		Short: "Start a game between two programs",
		RunE: func(cmd *cobra.Command, args []string) error {

			ui.ClearScreen()
			p := tea.NewProgram(
				NewGameModel(white, black, turnTimer),
			)
			_, err := p.Run()
			if err != nil {
				log.Fatal(err.Error())
			}

			return nil
		},
	}

	cmd.Flags().DurationVarP(
		&turnTimer, "time", "t", 0, "time limit per turn")
	cmd.Flags().VarP(
		&white, "white", "w", flags.VI_USAGE)
	cmd.Flags().VarP(
		&black, "black", "b", flags.VI_USAGE)

	return cmd
}
