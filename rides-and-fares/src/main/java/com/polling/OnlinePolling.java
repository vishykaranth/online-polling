/*
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

package com.polling;


import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;


public class OnlinePolling extends ExerciseBase {

    public static void main(String[] args) throws Exception {
        triggerPollingEvents();
    }

    private static void triggerPollingEvents() throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.backend", "filesystem");
        conf.setString("state.savepoints.dir", "file:///tmp/savepoints");
        conf.setString("state.checkpoints.dir", "file:///tmp/checkpoints");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(parallelism);

        env.enableCheckpointing(10000L);
        CheckpointConfig config = env.getCheckpointConfig();
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        DataStream<PollEvent> pollEvents = env
                .addSource(pollEventSourceOrTest(new PollEventGenerator()))
                .filter((PollEvent pollEvent) -> pollEvent.isStart)
                .keyBy((PollEvent pollEvent) -> pollEvent.pollId);

        DataStream<Tuple2<PollEvent, PollEvent>> enrichedRides = pollEvents
                .connect(pollEvents)
                .flatMap(new EnrichmentFunction())
                .uid("enrichment");

        env.execute("Poll Events");
    }


    public static class EnrichmentFunction extends RichCoFlatMapFunction<PollEvent, PollEvent, Tuple2<PollEvent, PollEvent>> {
        // keyed, managed state
        @Override
        public void open(Configuration config) {

        }

        @Override
        public void flatMap1(PollEvent event, Collector<Tuple2<PollEvent, PollEvent>> out) throws Exception {
            System.out.println("flatMap1 :: " + event);
        }

        @Override
        public void flatMap2(PollEvent event, Collector<Tuple2<PollEvent, PollEvent>> out) throws Exception {
//            System.out.println("flatMap2 :: " + event);
        }
    }
}
