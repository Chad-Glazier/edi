package ui

import "fmt"

//
// This file includes a bunch of ANSI escape codes and utility functions that
// use them.
//

const (
	fgBlack   = "\u001B[30m"
	fgRed     = "\u001B[31m"
	fgGreen   = "\u001B[32m"
	fgYellow  = "\u001B[33m"
	fgBlue    = "\u001B[34m"
	fgMagenta = "\u001B[35m"
	fgCyan    = "\u001B[36m"
	fgWhite   = "\u001B[37m"

	fgBrightBlack   = "\u001B[90m"
	fgBrightRed     = "\u001B[91m"
	fgBrightGreen   = "\u001B[92m"
	fgBrightYellow  = "\u001B[93m"
	fgBrightBlue    = "\u001B[94m"
	fgBrightMagenta = "\u001B[95m"
	fgBrightCyan    = "\u001B[96m"
	fgBrightWhite   = "\u001B[97m"

	bgBlack   = "\u001B[40m"
	bgRed     = "\u001B[41m"
	bgGreen   = "\u001B[42m"
	bgYellow  = "\u001B[43m"
	bgBlue    = "\u001B[44m"
	bgMagenta = "\u001B[45m"
	bgCyan    = "\u001B[46m"
	bgWhite   = "\u001B[47m"

	bgBrightBlack   = "\u001B[100m"
	bgBrightRed     = "\u001B[101m"
	bgBrightGreen   = "\u001B[102m"
	bgBrightYellow  = "\u001B[103m"
	bgBrightBlue    = "\u001B[104m"
	bgBrightMagenta = "\u001B[105m"
	bgBrightCyan    = "\u001B[106m"
	bgBrightWhite   = "\u001B[107m"

	bold = "\u001B[1m"
	dim  = "\u001B[2m"

	italic             = "\u001B[3m"
	italicReset        = "\u001B[23m"
	underline          = "\u001B[4m"
	underlineReset     = "\u001b[24m"
	blink              = "\u001B[5m"
	blinkReset         = "\u001b[25m"
	reverse            = "\u001B[7m"
	reverseReset       = "\u001b[27m"
	hidden             = "\u001B[8m"
	hiddenReset        = "\u001b[28m"
	strikethrough      = "\u001B[9m"
	strikethroughReset = "\u001b[29m"

	eraseScreen           = "\u001B[2J"
	resetCursor           = "\u001B[H"
	moveCursorToLineStart = "\u001B[1G"

	reset          = "\u001B[0m"
	resetFg        = "\u001B[39m"
	resetBg        = "\u001b[49m"
	resetIntensity = "\u001b[22m"

	hideCursor = "\u001B[?25l"
	showCursor = "\u001B[?25h"
)

func FgBlack(s string) string   { return fgBlack + s + resetFg }
func FgRed(s string) string     { return fgRed + s + resetFg }
func FgGreen(s string) string   { return fgGreen + s + resetFg }
func FgYellow(s string) string  { return fgYellow + s + resetFg }
func FgBlue(s string) string    { return fgBlue + s + resetFg }
func FgMagenta(s string) string { return fgMagenta + s + resetFg }
func FgCyan(s string) string    { return fgCyan + s + resetFg }
func FgWhite(s string) string   { return fgWhite + s + resetFg }

func FgBrightBlack(s string) string   { return fgBrightBlack + s + resetFg }
func FgBrightRed(s string) string     { return fgBrightRed + s + resetFg }
func FgBrightGreen(s string) string   { return fgBrightGreen + s + resetFg }
func FgBrightYellow(s string) string  { return fgBrightYellow + s + resetFg }
func FgBrightBlue(s string) string    { return fgBrightBlue + s + resetFg }
func FgBrightMagenta(s string) string { return fgBrightMagenta + s + resetFg }
func FgBrightCyan(s string) string    { return fgBrightCyan + s + resetFg }
func FgBrightWhite(s string) string   { return fgBrightWhite + s + resetFg }

func BgBlack(s string) string   { return bgBlack + s + resetBg }
func BgRed(s string) string     { return bgRed + s + resetBg }
func BgGreen(s string) string   { return bgGreen + s + resetBg }
func BgYellow(s string) string  { return bgYellow + s + resetBg }
func BgBlue(s string) string    { return bgBlue + s + resetBg }
func BgMagenta(s string) string { return bgMagenta + s + resetBg }
func BgCyan(s string) string    { return bgCyan + s + resetBg }
func BgWhite(s string) string   { return bgWhite + s + resetBg }

func BgBrightBlack(s string) string   { return bgBrightBlack + s + resetBg }
func BgBrightRed(s string) string     { return bgBrightRed + s + resetBg }
func BgBrightGreen(s string) string   { return bgBrightGreen + s + resetBg }
func BgBrightYellow(s string) string  { return bgBrightYellow + s + resetBg }
func BgBrightBlue(s string) string    { return bgBrightBlue + s + resetBg }
func BgBrightMagenta(s string) string { return bgBrightMagenta + s + resetBg }
func BgBrightCyan(s string) string    { return bgBrightCyan + s + resetBg }
func BgBrightWhite(s string) string   { return bgBrightWhite + s + resetBg }

func Bold(s string) string { return bold + s + resetIntensity }
func Dim(s string) string  { return dim + s + resetIntensity }

func Italic(s string) string        { return italic + s + italicReset }
func Underline(s string) string     { return underline + s + underlineReset }
func Blink(s string) string         { return blink + s + blinkReset }
func Reverse(s string) string       { return reverse + s + reverseReset }
func Hidden(s string) string        { return hidden + s + hiddenReset }
func Strikethrough(s string) string { return strikethrough + s + strikethroughReset }

func ClearScreen() { fmt.Print(eraseScreen + resetCursor) }

func SetCursor(row, col int) { fmt.Printf("\u001B[%d;%dH", row, col) }

func MoveCursorUp(rows int)    { fmt.Printf("\u001B[%dA", rows) }
func MoveCursorDown(rows int)  { fmt.Printf("\u001B[%dB", rows) }
func MoveCursorLeft(cols int)  { fmt.Printf("\u001B[%dD", cols) }
func MoveCursorRight(cols int) { fmt.Printf("\u001B[%dC", cols) }

func HideCursor() { fmt.Printf(hideCursor) }
func ShowCursor() { fmt.Printf(showCursor) }
