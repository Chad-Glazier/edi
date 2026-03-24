/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import ubc.team09.eval.HeuristicMethod;
import ubc.team09.eval.KMinDist;
import ubc.team09.eval.QMinDist;
import ubc.team09.eval.X;
import ubc.team09.player.Util;
import ubc.team09.state.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(time = 400, timeUnit = TimeUnit.MILLISECONDS)
public class HeuristicEvaluations {

	private static final State initial = Util.initialBoard();
	private static final State sparse = Util.randomBoard(0.10);
	private static final State dense = Util.randomBoard(0.40);

	private static final HeuristicMethod qmindist = new QMinDist();
	private static final HeuristicMethod kmindist = new KMinDist();
	private static final HeuristicMethod x = new X();

	@Benchmark
	public void QMinDistInitial(Blackhole bh) {
		bh.consume(qmindist.evaluate(initial));
	}

	@Benchmark
	public void QMinDistSparse(Blackhole bh) {
		bh.consume(qmindist.evaluate(sparse));
	}

	@Benchmark
	public void QMinDistDense(Blackhole bh) {
		bh.consume(qmindist.evaluate(dense));
	}

	@Benchmark
	public void KMinDistInitial(Blackhole bh) {
		bh.consume(kmindist.evaluate(initial));
	}

	@Benchmark
	public void KMinDistSparse(Blackhole bh) {
		bh.consume(kmindist.evaluate(sparse));
	}

	@Benchmark
	public void KMinDistDense(Blackhole bh) {
		bh.consume(kmindist.evaluate(dense));
	}

	@Benchmark
	public void XInitial(Blackhole bh) {
		bh.consume(x.evaluate(initial));
	}

	@Benchmark
	public void XSparse(Blackhole bh) {
		bh.consume(x.evaluate(sparse));
	}

	@Benchmark
	public void XDense(Blackhole bh) {
		bh.consume(x.evaluate(dense));
	}
}
