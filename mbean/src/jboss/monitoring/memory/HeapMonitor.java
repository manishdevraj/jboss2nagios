/**
 * 
 */
package jboss.monitoring.memory;

/**
 * @author Manish Devraj
 * 
 */
public class HeapMonitor implements HeapMonitorMBean {
	public String frequency;
	HeapMgmtThread thread;

	public HeapMonitor() {
		System.out
				.println("nnt HeapMonitorMBean is activated...inside HeapMonitor() constructor--setting default Frequency=10000 Miliseconds");
		;
		if (thread == null) {
			thread = new HeapMgmtThread();
			thread.setName("HeapMonitor MBean Accept-Thread");
			thread.setDaemon(true);
		}
	}

	/**
	 * Start MBean and use thread to open port for listening over binding
	 * address
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		System.out.println("nntStarting start() HeapMonitor invoked");
		frequency = "3000";

		synchronized (thread) {
			thread.start();
		}
	}

	/**
	 * Stop thread implementation
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		System.out.println("nntStopping stop() HeapMonitor  invoked");
		synchronized (thread) {
			thread.stopThread();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jboss.monitoring.memory.HeapMonitorMBean#setFrequency(java.lang.String)
	 */
	@Override
	public void setFrequency(String frequency) {
		System.out.println("nt HeapMonitor Watch Frequency is set to : "
				+ frequency + "-Milliseconds");
		this.frequency = frequency;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jboss.monitoring.memory.HeapMonitorMBean#getFrequency()
	 */
	@Override
	public String getFrequency() {
		System.out.println("nt HeapMonitor Watch Frequency is set to : "
				+ frequency + "-Milliseconds");
		return this.frequency;
	}

}
