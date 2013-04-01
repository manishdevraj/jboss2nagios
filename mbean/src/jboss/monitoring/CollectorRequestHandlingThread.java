package jboss.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import jboss.monitoring.util.CPUUsage;
import jboss.monitoring.util.GenerateHeapDump;
import jboss.monitoring.util.MXBeanConstants;
import jboss.monitoring.util.MXBeanReader;

import org.jboss.util.StringPropertyReplacer;

/**
 * Class that handles request sent to thread
 * 
 * @author manish
 * 
 */
public class CollectorRequestHandlingThread extends Thread {

	private static final String SPACE_REGEX = "\\+"; // ein Plus-Zeichen
	private static final String SEP_REGEX = " +"; // 1 bis n Leerzeichen

	private final Map<String, Long> lastValueMap;
	private final Socket reqSocket;

	public CollectorRequestHandlingThread(Socket socket,
			Map<String, Long> lastValueMap) {
		this.reqSocket = socket;
		this.lastValueMap = lastValueMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					reqSocket.getInputStream()));
			PrintWriter out = new PrintWriter(reqSocket.getOutputStream());
			String request = in.readLine();
			if (validRequest(request)) {
				String[] params = request.split(SEP_REGEX);
				boolean bHeapUtil = false;

				try {
					String mbeanName = restoreWhitspaceAndReplaceVariables(params[0]);
					// check if its heap dump call
					if (mbeanName.equals(MXBeanConstants.HOTSPOT_DIAGNOSTIC)) {
						bHeapUtil = true;
					}
					String attributeName = null;
					String warningCondition = null;
					String critialCondition = null;
					if (!bHeapUtil) {
						attributeName = restoreWhitspaceAndReplaceVariables(params[1]);
						warningCondition = params[2];
						critialCondition = params[3];
						boolean diff = false;
						if (attributeName.startsWith("*")
								&& attributeName.length() > 1) {
							attributeName = attributeName.substring(1);
							diff = true;
						}

						boolean size = false;
						if (attributeName.endsWith("[]")
								&& attributeName.length() > 2) {
							attributeName = attributeName.substring(0,
									attributeName.length() - 2);
							size = true;
						}
					}

					boolean singleton = false;
					if (mbeanName.startsWith("?") && mbeanName.length() > 1) {
						mbeanName = mbeanName.substring(1);
						singleton = true;
					}

					ObjectName objectName = new ObjectName(mbeanName);
					MBeanServer mbeanServer = java.lang.management.ManagementFactory
							.getPlatformMBeanServer();

					// Compatible with older version of Jboss
					// MBeanServer mbeanServer =
					// MBeanServerLocator.locateJBoss();
					// invoking getMBeanInfo() works around a bug in
					// getAttribute() that fails to
					// refetch the domains from the platform (JDK) bean server
					try {
						mbeanServer.getMBeanInfo(objectName);
					} catch (InstanceNotFoundException ex) {
						if (singleton) {
							// this is a check for a cluster singleton, which is
							// not present on this node
							sendResult(out, attributeName,
									MXBeanConstants.RESULT_NOTFOUND, null, null);
						} else {
							sendResult(out, attributeName,
									MXBeanConstants.RESULT_UNKNOWN,
									"MBean " + mbeanName
											+ " could not be found", null);
						}
						out.flush();
						return;
					}
					Object attribute = null;
					boolean bcheckAttribute = true;

					if (objectName.toString().equals(MXBeanConstants.OS_OBJ)) {
						bcheckAttribute = false;
					}

					if (bcheckAttribute && null != attributeName) {
						attribute = mbeanServer.getAttribute(objectName,
								attributeName);
					}

					StringBuffer outBuffer = new StringBuffer();
					if (objectName.toString()
							.equals(MXBeanConstants.MEMORY_OBJ)
							&& attributeName
									.equals(MXBeanConstants.HEAP_MEM_ATTRIBUTE)) {
						outBuffer = MXBeanReader.getHeapMemoryUsage(
								(CompositeDataSupport) attribute,
								warningCondition, critialCondition);
						String code = MXBeanConstants.RESULT_OK;
						if (outBuffer.toString().contains(
								MXBeanConstants.RESULT_CRITICAL)) {
							code = MXBeanConstants.RESULT_CRITICAL;
						} else if (outBuffer.toString().contains(
								MXBeanConstants.RESULT_WARNING)) {
							code = MXBeanConstants.RESULT_WARNING;
						}
						sendResult(out, attributeName, code, attributeName
								+ " is " + outBuffer.toString(), null);
					} else if (objectName.toString().equals(
							MXBeanConstants.MEMORY_OBJ)
							&& attributeName
									.equals(MXBeanConstants.NON_HEAP_MEM_ATTRIBUTE)) {
						outBuffer = MXBeanReader.getHeapMemoryUsage(
								(CompositeDataSupport) attribute,
								warningCondition, critialCondition);
						String code = MXBeanConstants.RESULT_OK;
						if (outBuffer.toString().contains(
								MXBeanConstants.RESULT_CRITICAL)) {
							code = MXBeanConstants.RESULT_CRITICAL;
						} else if (outBuffer.toString().contains(
								MXBeanConstants.RESULT_WARNING)) {
							code = MXBeanConstants.RESULT_WARNING;
						}
						sendResult(out, attributeName, code, attributeName
								+ " is " + outBuffer.toString(), null);
					} else if (objectName.toString().equals(
							MXBeanConstants.OS_OBJ)
							&& attributeName
									.equals(MXBeanConstants.CPU_ATTRIBUTE)) {
						outBuffer = CPUUsage.getCpuUsage(warningCondition,
								critialCondition);
						String code = MXBeanConstants.RESULT_OK;
						if (outBuffer.toString().contains(
								MXBeanConstants.RESULT_CRITICAL)) {
							code = MXBeanConstants.RESULT_CRITICAL;
						} else if (outBuffer.toString().contains(
								MXBeanConstants.RESULT_WARNING)) {
							code = MXBeanConstants.RESULT_WARNING;
						}
						sendResult(out, attributeName, code, attributeName
								+ " details: " + outBuffer.toString(), null);

					} else if (objectName.toString().equals(
							MXBeanConstants.HOTSPOT_DIAGNOSTIC)) {
						String fileName = "HDump" + getProcessId("<PID>") + "-"
								+ System.currentTimeMillis() + ".hprof";
						GenerateHeapDump.getHeapDump(mbeanServer, fileName);
						String code = MXBeanConstants.RESULT_OK;
						sendResult(
								out,
								MXBeanConstants.HOTSPOT_DIAGNOSTIC,
								code,
								MXBeanConstants.HOTSPOT_DIAGNOSTIC + " is "
										+ "Dump created at "
										+ System.getProperty("user.home") + "/"
										+ fileName, null);
					}
				} catch (Exception e) {
					sendResult(
							out,
							"?",
							MXBeanConstants.RESULT_UNKNOWN,
							"MBeanServer responded: "
									+ e.getClass().getSimpleName() + " "
									+ e.getMessage(), null);
				}
			} else {
				sendResult(out, "?", MXBeanConstants.RESULT_UNKNOWN,
						"request not valid", null);
			}
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reqSocket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void sendResult(PrintWriter out, String attributeName, String code,
			String message, String perfData) {
		String result = code + attributeName.toUpperCase() + " " + code;
		if (message != null) {
			result += "- " + message;
		}
		if (perfData != null) {
			result += "|" + perfData;
		}
		out.print(result);
	}

	private String restoreWhitspaceAndReplaceVariables(String mbeanName) {
		return StringPropertyReplacer.replaceProperties(mbeanName.replaceAll(
				SPACE_REGEX, " "));
	}

	private boolean validRequest(String request) {
		if (request == null) {
			return false;
		}
		String[] strings = request.split(SEP_REGEX);
		if (strings.length == 1) {
			return strings.length == 1;
		}
		return strings.length == 4;
	}

	private static String getProcessId(final String fallback) {
		// Note: may fail in some JVM implementations
		// therefore fallback has to be provided

		// something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');

		if (index < 1) {
			// part before '@' empty (index = 0) / '@' not found (index = -1)
			return fallback;
		}

		try {
			return Long.toString(Long.parseLong(jvmName.substring(0, index)));
		} catch (NumberFormatException e) {
			// ignore
		}
		return fallback;
	}

}