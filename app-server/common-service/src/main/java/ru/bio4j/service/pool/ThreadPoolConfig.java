package ru.bio4j.service.pool;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;

import ru.bio4j.util.NamedThreadFactory;

public class ThreadPoolConfig {

	private int corePoolSize;
	
	private int maxPoolSize;
	
	private int queueLimit = -1;
	
	private long keepAliveTimeMillis;
	
	private int priority;
	
	private ThreadFactory threadFactory;
	
	private Queue<Runnable> queue;

	public ThreadPoolConfig() {
		this(0, 0, 0, 0, 0);
	}

	public ThreadPoolConfig(
			int corePoolSize, 
			int maxPoolSize,
			int queueLimit, 
			long keepAliveTimeMillis,
			int priority) {
		super();
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
		this.queueLimit = queueLimit;
		this.keepAliveTimeMillis = keepAliveTimeMillis;
		this.priority = priority;
		this.threadFactory = new NamedThreadFactory("Pricing kernel Worker - ");
		this.queue = (queueLimit == -1) ? 
				new ArrayBlockingQueue<Runnable>(Integer.MAX_VALUE) : new ArrayBlockingQueue<Runnable>(queueLimit);
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public int getQueueLimit() {
		return queueLimit;
	}

	public long getKeepAliveTimeMillis() {
		return keepAliveTimeMillis;
	}

	public int getPriority() {
		return priority;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory= threadFactory ;
	}
	
	public Queue<Runnable> getQueue() {
		return queue;
	}

	public void setQueue(Queue<Runnable> queue) {
		this.queue = queue;
	}

	@Override
	public String toString() {
		return " corePoolSize=" + corePoolSize + 
				", maxPoolSize=" + maxPoolSize + 
				", queueLimit=" + queueLimit + 
				", keepAliveTimeMillis=" + keepAliveTimeMillis + 
				", priority=" + priority + 
				", queue=" + queue;
	}

}
