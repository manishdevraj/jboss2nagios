/**
 * 
 */
package jboss.monitoring;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

class CollectorAcceptingThread extends Thread {

	private boolean keepRunning;

	ServerSocket socket;

	// use a *synchronized* Map since multiple Threads may access it
	private Map<String, Long> lastValueMap = new java.util.Hashtable<String, Long>();

	@Override
	public void run() {
		keepRunning = true;
		while (keepRunning) {
			acceptRequests();
		}
	}

	private void acceptRequests() {
		try {
			Socket reqSocket = socket.accept();
			CollectorRequestHandlingThread thread = new CollectorRequestHandlingThread(reqSocket,lastValueMap);
			thread.setName("Collector request "+reqSocket.getRemoteSocketAddress());
			thread.setDaemon(true);
			thread.start();
		} catch (IOException e) {
			// the socket could have been closed -> ok
		}
	}

	

	public void stopThread() {
		keepRunning = false;
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				socket = null;
			}
		}
	}
}