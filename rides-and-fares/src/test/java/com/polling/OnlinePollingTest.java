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

import org.apache.flink.api.java.tuple.Tuple2;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class OnlinePollingTest extends OnlinePollingTestBase<Tuple2<PollEvent, PollEvent>> {

    static final OnlinePollingTestBase.Testable JAVA_EXERCISE = () -> OnlinePolling.main(new String[]{});

    final PollEvent pollEvent1 = testPollEvent(1);
    final PollEvent pollEvent2 = testPollEvent(1);


    @Test
    public void testInOrder() throws Exception {
        PollEventSource pollEvents = new PollEventSource(pollEvent1, pollEvent2);
        List<PollEvent> expected = Arrays.asList(pollEvent1, pollEvent2);

        List<?> results = results(pollEvents);
        assertThat("Join results don't match", results, containsInAnyOrder(expected.toArray()));
    }

    private PollEvent testPollEvent(long pollId) {
        return new PollEvent(pollId);
    }

    protected List<?> results(PollEventSource pollEvents) throws Exception {
        Testable javaSolution = () -> OnlinePolling.main(new String[]{});
        return runApp(pollEvents, new TestSink<>(), JAVA_EXERCISE, javaSolution);
    }

}
