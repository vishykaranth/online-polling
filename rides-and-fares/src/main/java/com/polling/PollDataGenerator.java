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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class PollDataGenerator {

	private static final int SECONDS_BETWEEN_RIDES = 20;
	private static final int NUMBER_OF_DRIVERS = 200;
	private static final Instant beginTime = Instant.parse("2020-01-01T12:00:00.00Z");

	private transient long rideId;
	static String[] users = {"user1", "user2", "user3"};
	static String[] polls = {"PollA", "PollB", "PollC"};
	static Map<String, String[]> pollOptions = new HashMap();

	static {
		String[] pollAOptions = {"OptionA1", "OptionA2", "OptionA3", "InvalidOption"};
		String[] pollBOptions = {"OptionB1", "OptionB2", "OptionB3", "InvalidOption"};
		String[] pollCOptions = {"OptionC1", "OptionC2", "OptionC3", "InvalidOption"};


		pollOptions.put("PollA", pollAOptions);
		pollOptions.put("PollB", pollBOptions);
		pollOptions.put("PollC", pollCOptions);
	}


	public PollDataGenerator(long rideId) {

		this.rideId = rideId;
	}

	public Instant time() {
		return beginTime.plusSeconds(SECONDS_BETWEEN_RIDES * rideId);
	}

	public long driverId() {
		Random rnd = new Random(rideId);
		return 2013000000 + rnd.nextInt(NUMBER_OF_DRIVERS);
	}


	public String userIdentity() {
		int rnd = new Random().nextInt(users.length);
		return users[rnd];
	}

	public String poll() {
		int rnd = new Random().nextInt(polls.length);
		return polls[rnd];
	}

	public String pollOption(String poll) {
		String[] options = pollOptions.get(poll);
		int rnd = new Random().nextInt(options.length);
		return options[rnd];
	}

}
