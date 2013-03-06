package jboss.monitoring.memory;

import java.util.Collection;
import java.util.LinkedList;

public class MemTest {
	public static void main(String[] args) {
		MemoryWarningSystem.setPercentageUsageThreshold(0.6);

		MemoryWarningSystem mws = MemoryWarningSystem.getMemoryWarnSystem();
		mws.addListener(new MemoryWarningSystem.Listener() {
			@Override
			public void memoryUsageLow(long usedMemory, long maxMemory) {
				System.out.println("Memory usage low!!!");
				double percentageUsed = ((double) usedMemory) / maxMemory;
				System.out.println("percentageUsed = " + percentageUsed);
				MemoryWarningSystem.setPercentageUsageThreshold(0.8);
			}
		});

		Collection<Double> numbers = new LinkedList<Double>();
		while (true) {
			numbers.add(Math.random());
		}
	}
}
