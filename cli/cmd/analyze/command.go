package analyze

import (
	"log"
	"time"

	"github.com/Chad-Glazier/edi/cli/cmd/flags"
	"github.com/Chad-Glazier/edi/cli/ui"

	tea "charm.land/bubbletea/v2"
	"github.com/spf13/cobra"
)

func AnalyzeCommand() *cobra.Command {

	var turnTimer time.Duration
	var vi flags.VI
	var output string

	cmd := &cobra.Command{
		Use:   "analyze",
		Short: "Have a program play against itself to collect analytics",
		RunE: func(cmd *cobra.Command, args []string) error {

			ui.ClearScreen()

			_, err := tea.NewProgram(NewGameModel(vi, turnTimer)).Run()
			if err != nil {
				log.Fatal(err.Error())
			}

			return nil
		},
	}

	cmd.Flags().DurationVarP(
		&turnTimer, "time", "t", 0, "time limit per turn")
	cmd.Flags().VarP(
		&vi, "vi", "v", flags.VI_USAGE)
	cmd.Flags().StringVarP(
		&output, "output", "o", "analytics.csv", "the name of the file to write analytics to",
	)

	return cmd
}
