package threadPoolImpl;

import java.util.concurrent.Callable;

public class TestTask implements Callable {
	 
    private double outcome;
 
    public TestTask(double x) {
    	outcome = x;
    }
    
	@Override
	public Double call() throws Exception { 
		System.out.println("Called" + outcome);
		try {
			Thread.sleep((long) 1e3);
			System.out.println("Done sleeping " + outcome);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return outcome;
	}
}