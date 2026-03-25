package org.sample;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ubc.team09.eval.KMinDist;
import ubc.team09.eval.QMinDist;
import ubc.team09.eval.X;
import ubc.team09.player.Util;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.State;

public class SearchProfiling {

	private static final State[] sparse = new State[]{
		Util.randomBoard(0.15),
		Util.randomBoard(0.15),
		Util.randomBoard(0.15),
		Util.randomBoard(0.15),
		Util.randomBoard(0.15),
	};
	private static final State[] medium = new State[]{
		Util.randomBoard(0.30),
		Util.randomBoard(0.30),
		Util.randomBoard(0.30),
		Util.randomBoard(0.30),
		Util.randomBoard(0.30),
	};
	private static final State[] dense = new State[]{
		Util.randomBoard(0.45),
		Util.randomBoard(0.45),
		Util.randomBoard(0.45),
		Util.randomBoard(0.45),
		Util.randomBoard(0.45),
	};
	private static final State[] veryDense = new State[]{
		Util.randomBoard(0.60),
		Util.randomBoard(0.60),
		Util.randomBoard(0.60),
		Util.randomBoard(0.60),
		Util.randomBoard(0.60),
	};
	
	@org.openjdk.jmh.annotations.State(Scope.Thread)
	@Warmup(iterations = 1, batchSize = 1)
	@Measurement(iterations = 3, batchSize = 1)
	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
	@OperationsPerInvocation(1)
	@Fork(1)
	public static class AB {

		private static final KMinDist kmindist = new KMinDist();
		private static final QMinDist qmindist = new QMinDist();
		private static final X x = new X();

		@Benchmark
		public void KDepth3SparseBoard(Blackhole bh) {
			for (State s : sparse) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(kmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(3);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void KDepth4MediumBoard(Blackhole bh) {
			for (State s : medium) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(kmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(4);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void KDepth5DenseBoard(Blackhole bh) {
			for (State s : dense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(kmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(5);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void KDepth7VeryDenseBoard(Blackhole bh) {
			for (State s : veryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(kmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(7);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void QDepth3SparseBoard(Blackhole bh) {
			for (State s : sparse) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(qmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(3);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void QDepth4MediumBoard(Blackhole bh) {
			for (State s : medium) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(qmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(4);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void QDepth5DenseBoard(Blackhole bh) {
			for (State s : dense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(qmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(5);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void QDepth7VeryDenseBoard(Blackhole bh) {
			for (State s : veryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(qmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(7);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void XDepth3SparseBoard(Blackhole bh) {
			for (State s : sparse) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(x);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(3);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void XDepth4MediumBoard(Blackhole bh) {
			for (State s : medium) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(x);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(4);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void XDepth5DenseBoard(Blackhole bh) {
			for (State s : dense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(x);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(5);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void XDepth7VeryDenseBoard(Blackhole bh) {
			for (State s : veryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(x);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(7);
				bh.consume(ab.search());				
			}
		}

		public static void main(String[] args) throws RunnerException {
			Options opt = new OptionsBuilder()
				.include(SearchProfiling.AB.class.getSimpleName())
				.addProfiler(StackProfiler.class)
				// .addProfiler(GCProfiler.class)
				.build();

			new Runner(opt).run();
		}
	}
}
