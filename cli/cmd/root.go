package cmd

import (
	"os"

	"github.com/Chad-Glazier/edi/cli/cmd/analyze"
	"github.com/Chad-Glazier/edi/cli/cmd/run"
	"github.com/spf13/cobra"
)

// rootCmd represents the base command when called without any subcommands
var rootCmd = &cobra.Command{
	Use:   "edi",
	Short: "EDI is a project to investigate the programs that play Amazons",
	Long: `EDI is a project meant to explore and analyze the programs that play 
the Game of Amazons. This tool is a CLI interface for EDI, which you 
can use to run games between programs and collect data about their 
performance.`,
}

func Execute() {
	err := rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}

func init() {
	rootCmd.AddCommand(run.RunCommand())
	rootCmd.AddCommand(analyze.AnalyzeCommand())
}
