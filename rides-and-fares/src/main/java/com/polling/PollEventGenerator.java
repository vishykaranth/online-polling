package com.polling;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;


public class PollEventGenerator implements SourceFunction<PollEvent> {

	private volatile boolean running = true;
	public static final int SLEEP_MILLIS_PER_EVENT = 1000;

	@Override
	public void run(SourceContext<PollEvent> ctx) throws Exception {

		long id = 1;

		while (running) {
			PollEvent pollEvent = new PollEvent(id);
			id += 1;

			ctx.collectWithTimestamp(pollEvent, pollEvent.getEventTime());
			ctx.emitWatermark(new Watermark(pollEvent.getEventTime()));

			Thread.sleep(PollEventGenerator.SLEEP_MILLIS_PER_EVENT);
		}
	}

	@Override
	public void cancel() {
		running = false;
	}
}
