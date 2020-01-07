package threadPoolImpl;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import threadPoolImpl.TestTask;
import threadPoolImpl.ThreadPool;
 
public class ThreadPoolTest {
 
    public static void main(String[] args) throws InterruptedException, ExecutionException {
    	
    	int cores = Runtime.getRuntime().availableProcessors();
        ThreadPool threadPool = new ThreadPool(cores);
        LinkedList<Future<Double>> futures = new LinkedList<Future<Double>>();
		
        for(int i = 0; i < 2; ++i) {
            Callable task = new TestTask(i);
            futures.add(threadPool.submit(task));
        }
        
        threadPool.shutdown();
        
        for (Future<Double> f : futures) {
        	System.out.println("Get" + f.get());		
        }
    }
}