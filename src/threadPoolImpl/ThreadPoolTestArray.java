package threadPoolImpl;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import Lab1.Utils;
import threadPoolImpl.ThreadPool;
 
public class ThreadPoolTestArray {

	public static class Summer implements Callable<Double> {
		
		public int offset, nElements;
		double sum = 0;
		double array[];
		
		public Summer(double[] array, int offset, int nElements) {
			this.offset = offset;
			this.nElements = nElements;
			this.array = array;
		}

		@Override
		public Double call() throws Exception {
			return Utils.sumLogRange(array, offset, nElements);
		}	
	}
	
    public static void main(String[] args) throws InterruptedException, ExecutionException {
    	  
        int nSizes = 17;
		int reps = 2000;
		
		int sizes[] = new int[nSizes];
		double avgs[] = new double[nSizes];
		double times[] = new double[nSizes];
		for (int i = 0; i < sizes.length; ++i) {
			sizes[i] = 1 << i + 3;
			avgs[i] = 0.;
		}
		
		int cores = Runtime.getRuntime().availableProcessors();
		ThreadPool threadPool = new ThreadPool(cores);
        for (int i = 0; i < sizes.length; ++i) {
			double arr[] = new double[sizes[i]];
			Arrays.fill(arr, Math.E);
			for (int j = 0; j < reps; ++j) {
				long start = System.nanoTime();		
				LinkedList<Future<Double>> futures = new LinkedList<Future<Double>>();
				double sum = 0;
				int offset = 0;
				int nElements = arr.length / cores;
				for(int t = 0; t < cores; ++t) {
					futures.add(threadPool.submit(new Summer(arr, offset, nElements)));
					offset += nElements;
				}
				
				for(Future<Double> f: futures) 
					sum += f.get();
				
				long end = System.nanoTime();	
				avgs[i] += sum;
				times[i] += end - start;
			}
			
			avgs[i] /= reps;
			times[i] /= reps;
		}
        
		threadPool.shutdown();
		
		try {
			PrintWriter pw = new PrintWriter("ThreadPoolImpl.csv");
			pw.println("ArraySize, Time, Average, Reps");
			for (int i = 0; i < nSizes; ++i) {
				pw.println(sizes[i] + "," + times[i] + "," + avgs[i] + "," + reps);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
}