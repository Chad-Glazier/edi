//go:build amd64 && goexperiment.simd

package bb

import (
	"testing"
)

func BenchmarkIsEmptyx2(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		IsEmpty(x)
	}
}

func BenchmarkIsNotEmptyx2(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		IsNotEmpty(x)
	}
}

func BenchmarkBitwiseOrx2(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		Or(x, y)
	}
}

func BenchmarkBitwiseXorx2(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		Xor(x, y)
	}
}

func BenchmarkBitwiseAndx2(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		And(x, y)
	}
}

func BenchmarkBitwiseAndNotx2(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		And(x, y)
	}
}

func BenchmarkBitwiseNotx2(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Not(x)
	}
}

func BenchmarkCountx2(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Count(x)
	}
}
