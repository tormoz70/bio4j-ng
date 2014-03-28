package ru.bio4j.service.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageAccumulatorImpl implements MessageAccumulator {
	
	private final BlockingQueue<Serializable> accumulator[];
	
	private final int upperMessageFetchLimitCount;
	
	@SuppressWarnings("unchecked")
	public MessageAccumulatorImpl(int keysCount, int upperMessageFetchLimitCount, int accumulatorCapacity) {
		accumulator = new BlockingQueue[keysCount];
		this.upperMessageFetchLimitCount = upperMessageFetchLimitCount;
		fillAccumulator(keysCount, accumulatorCapacity);
	}
	
	private void fillAccumulator(int keysCount, int accumulatorCapacity) {
		for (int i = 0; i < keysCount; i++) {
			accumulator[i] = new ArrayBlockingQueue<>(accumulatorCapacity);
		}
	}

	@Override
	public void storeMessage(int key, Serializable message) {
		getQueue(key).add(message);
	}

	@Override
	public Serializable drain(int key) { //if smthg changed we will send it in next time
		BlockingQueue<Serializable> queue = getQueue(key);
		int size = queue.size();
		if (size > 1) {
			return getList(queue);
		} else if (size == 1) {
			return getElement(queue);
		} else {
			return null;
		}
	}

	private Serializable getElement(BlockingQueue<Serializable> queue) {
		return queue.poll();
	}

	private Serializable getList(BlockingQueue<Serializable> queue) {
		ArrayList<Serializable> list = new ArrayList<>(queue.size());
		queue.drainTo(list, upperMessageFetchLimitCount);
		return list;
	}

	@Override
	public int getMessageFetchLimitCount() {
		return upperMessageFetchLimitCount;
	}

	private BlockingQueue<Serializable> getQueue(int priority) {
		return accumulator[priority];
	}
}
