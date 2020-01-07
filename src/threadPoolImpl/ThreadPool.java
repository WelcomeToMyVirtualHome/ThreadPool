package threadPoolImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool {

	private final int nThreads;
	private List<Executor> threads;
	private final BlockingQueue<FutureTask> queue;
	private AtomicBoolean isShutdown;

	public ThreadPool(int nThreads) {
		this.nThreads = nThreads;
		isShutdown = new AtomicBoolean(false);
		queue = new LinkedBlockingQueue<>();
		threads = new ArrayList<>(nThreads);

		for (int i = 0; i < this.nThreads; ++i) {
			Executor executor = new Executor(queue, this);
			executor.start();
			threads.add(executor);
		}
	}

	public FutureTask submit(Callable task) {
		FutureTask future = new FutureTask(task); 
		if (!isShutdown.get()) {
			try {
				queue.put(future);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return future;
		}
		return null;
	}

	public void shutdown() {
		isShutdown = new AtomicBoolean(true);
	}

	private class Executor extends Thread {

		private BlockingQueue<FutureTask> queue;
		private ThreadPool threadPool;
		
		public Executor(BlockingQueue<FutureTask> queue, ThreadPool threadPool) {
			this.queue = queue;
			this.threadPool = threadPool;
		}
		
		@Override
		public void run() {
			try {
				while (!threadPool.isShutdown.get() || !queue.isEmpty()) {
					FutureTask f;
					while ((f = queue.poll()) != null) {
						f.run();
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}
