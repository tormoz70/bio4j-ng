package ru.bio4j.service.pool;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessorThreadPool extends AbstractExecutorService {

	private final Object stateLock = new Object();

	private volatile ThreadPoolExecutor poolExecutor;

    protected ProcessorThreadPool(ThreadPoolConfig poolConfig) {
		if (poolConfig == null) {
			throw new IllegalArgumentException("Thread Pool Config is not set");
		}
		setImpl(poolConfig);
	}

	private void setImpl(ThreadPoolConfig poolConfig) {
		this.poolExecutor = new ThreadPoolExecutor(poolConfig.getCorePoolSize(),
                poolConfig.getMaxPoolSize(),
                poolConfig.getKeepAliveTimeMillis(), TimeUnit.MICROSECONDS,
				(BlockingQueue<Runnable>) poolConfig.getQueue(),
                poolConfig.getThreadFactory());
	}
	
	
	public static ProcessorThreadPool create(ThreadPoolConfig poolConfig) {
		return new ProcessorThreadPool(poolConfig);
	}

	public ProcessorThreadPool reconfigure(ThreadPoolConfig poolConfig) {
		if (poolConfig == null) {
			throw new IllegalArgumentException("Poll config is not set");
		}
		synchronized (stateLock) {
			final ThreadPoolExecutor oldPoolExecutor = this.poolExecutor;
			if (poolConfig.getQueue() == oldPoolExecutor.getQueue()) {
				poolConfig.setQueue(null);
			}
			setImpl(poolConfig);
			drain(oldPoolExecutor.getQueue(), this.poolExecutor.getQueue());
			oldPoolExecutor.shutdown();
		}
		return this;
	}

	private void drain(Queue<Runnable> from, Queue<Runnable> too) {
		boolean cont = true;
		while (cont) {
			Runnable r = from.poll();
			if (cont = r != null) {
				too.add(r);
			}
		}
	}

	public int getActiveThreads() {
		return poolExecutor.getActiveCount();
	}
	
	@Override
	public void execute(Runnable command) {
		poolExecutor.execute(command);
	}

	@Override
	public void shutdown() {
		poolExecutor.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return poolExecutor.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return poolExecutor.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return poolExecutor.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return poolExecutor.awaitTermination(timeout, unit);
	}

}
