/**
 * 
 */
package jboss.monitoring;

import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * @author Manish Devraj
 * 
 */
public class MyServerMonitor implements MyServerMonitorMBean {

	boolean flag = true;
	public String frequency;
	private CollectorAcceptingThread thread;
	private int port;
	String bindAddress;

	public MyServerMonitor() {
		System.out
				.println("nnt ServiceMonitorMBean is activated...inside ServiceMonitor() constructor--setting default Frequency=10000 Miliseconds");
		;
		if (thread == null) {
			thread = new CollectorAcceptingThread();
			thread.setName("Collector MBean Accept-Thread");
			thread.setDaemon(true);
		}
	}

	@Override
	public void setFrequency(String frequency) {
		System.out.println("nt Server Watch Frequency is set to : " + frequency
				+ "-Milliseconds");
		this.frequency = frequency;
	}

	@Override
	public String getFrequency() {
		System.out.println("nt Server Watch Frequency is set to : " + frequency
				+ "-Milliseconds");
		return this.frequency;
	}

	/**
	 * Start MBean and use thread to open port for listening over binding
	 * address
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		System.out.println("nntStarting start() MyServerMonitor invoked");
		frequency = "3000";
		synchronized (thread) {
			if (thread.socket == null) {
				if (bindAddress != null && bindAddress.length() > 0) {
					thread.socket = new ServerSocket(port, -1,
							InetAddress.getByName(bindAddress));
				} else {
					thread.socket = new ServerSocket(port);
				}
			}
			thread.start();
		}
	}

	/**
	 * Stop thread implementation
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		System.out.println("nntStopping stop() MyServerMonitor  invoked");
		synchronized (thread) {
			thread.stopThread();
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

}
