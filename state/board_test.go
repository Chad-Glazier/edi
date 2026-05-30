package state

import "testing"

func TestIsTerminal(t *testing.T) {
	for range 100 {
		b := RandomBoard(10)
		children := SuccessorsArray{}
		childCount := b.Successors(&children)
		expected := childCount == 0
		actual := b.IsTerminal()
		if expected != actual {
			t.Fatalf("IsTerminal expected %v but got %v", expected, actual)
		}
	}
}
