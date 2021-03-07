package com.polling;/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.List;

public abstract class OnlinePollingTestBase<OUT> {
	public static class PollEventSource extends TestSource<PollEvent> implements ResultTypeQueryable<PollEvent> {
		public PollEventSource(Object ... eventsOrWatermarks) {
			this.testStream = eventsOrWatermarks;
		}

		@Override
		long getTimestamp(PollEvent pollEvent) {
			return pollEvent.getEventTime();
		}

		@Override
		public TypeInformation<PollEvent> getProducedType() {
			return TypeInformation.of(PollEvent.class);
		}
	}


	public static class TestStringSource extends TestSource<String> implements ResultTypeQueryable<String> {
		public TestStringSource(Object ... eventsOrWatermarks) {
			this.testStream = eventsOrWatermarks;
		}

		@Override
		long getTimestamp(String s) {
			return 0L;
		}

		@Override
		public TypeInformation<String> getProducedType() {
			return TypeInformation.of(String.class);
		}
	}

	public static class TestSink<OUT> implements SinkFunction<OUT> {

		// must be static
		public static final List VALUES = new ArrayList<>();

		@Override
		public void invoke(OUT value, Context context) {
			VALUES.add(value);
		}
	}

	public interface Testable {
		void main() throws Exception;
	}

	protected List<OUT> runApp(PollEventSource source, TestSink<OUT> sink, Testable exercise, Testable solution) throws Exception {
		ExerciseBase.pollEvents = source;

		return execute(sink, exercise, solution);
	}


	private List<OUT> execute(TestSink<OUT> sink, Testable exercise, Testable solution) throws Exception {
		sink.VALUES.clear();

		ExerciseBase.out = sink;
		ExerciseBase.parallelism = 1;

		try {
			exercise.main();
		} catch (Exception e) {
			if (ultimateCauseIsMissingSolution(e)) {
				sink.VALUES.clear();
				solution.main();
			} else {
				throw e;
			}
		}

		return sink.VALUES;
	}

	private List<OUT> execute(TestSink<OUT> sink, Testable solution) throws Exception {
		sink.VALUES.clear();

		ExerciseBase.out = sink;
		ExerciseBase.parallelism = 1;

		solution.main();

		return sink.VALUES;
	}

	private boolean ultimateCauseIsMissingSolution(Throwable e) {
		if (e instanceof Exception) {
			return true;
		} else if (e.getCause() != null) {
			return ultimateCauseIsMissingSolution(e.getCause());
		} else {
			return false;
		}
	}
}
