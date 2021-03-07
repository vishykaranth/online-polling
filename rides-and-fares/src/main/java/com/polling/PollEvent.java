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


import java.io.Serializable;
import java.time.Instant;


public class PollEvent implements Serializable {

	public long pollId;
	public boolean isStart = true;
	private String pollOption;
	private String userIdentity;
	private String poll;
	private Instant time;




	public PollEvent() {
		this.time = Instant.now();
	}

	/**
	 * Invents a TaxiFare.
	 */
	public PollEvent(long pollId) {
		PollDataGenerator g = new PollDataGenerator(pollId);

		this.pollId = pollId;
		this.time = g.time();
		this.userIdentity = g.userIdentity();
		this.poll = g.poll();
		this.pollOption = g.pollOption(poll);
	}



	@Override
	public String toString() {

		return pollId + "," +
				userIdentity + "," +
				poll + "," +
				time.toString() + "," +
				pollOption;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof PollEvent &&
				this.pollId == ((PollEvent) other).pollId;
	}

	@Override
	public int hashCode() {
		return (int) this.pollId;
	}


	public long getEventTime() {
		return time.toEpochMilli();
	}

}
