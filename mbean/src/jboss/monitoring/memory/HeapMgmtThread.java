/**
 * 
 */
package jboss.monitoring.memory;


/**
 * @author Manish Devraj
 * 
 */
public class HeapMgmtThread extends Thread {
	private boolean keepRunning;

	@Override
	public void run() {
		keepRunning = true;
		MemoryWarningSystem.setPercentageUsageThreshold(0.1);

		MemoryWarningSystem mws = MemoryWarningSystem.getMemoryWarnSystem();
		mws.addListener(new MemoryWarningSystem.Listener() {
			@Override
			public void memoryUsageLow(long usedMemory, long maxMemory) {
				System.out.println("Memory usage low!!!");
				double percentageUsed = ((double) usedMemory) / maxMemory;
				System.out.println("percentageUsed = " + percentageUsed);
				MemoryWarningSystem.setPercentageUsageThreshold(0.2);
			}
		});
		// Collection<Double> numbers = new LinkedList<Double>();
		while (keepRunning) {
			// numbers.add(Math.random());
		}
	}

	public void stopThread() {
		keepRunning = false;
	}

}
