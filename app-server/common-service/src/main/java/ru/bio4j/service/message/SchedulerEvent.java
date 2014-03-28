package ru.bio4j.service.message;

public class SchedulerEvent {
	
	private final long delayInSec;

	public SchedulerEvent(long delayInSec) {
		this.delayInSec = delayInSec;
	}

	public long getDelayInSec() {
		return delayInSec;
	}
}
