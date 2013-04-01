package jboss.monitoring.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Class to manage CPU usage
 * 
 * @author manish
 * 
 */
public class CPUUsage {
	/**
	 * Get CPU usage based on CPU time between a time difference
	 * 
	 * @param warningCondition
	 * @param critialCondition
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer getCpuUsage(String warningCondition,
			String critialCondition) throws Exception {
		StringBuffer buffer = new StringBuffer();
		OperatingSystemMXBean osbean = ManagementFactory
				.getOperatingSystemMXBean();
		RuntimeMXBean runbean = ManagementFactory.getRuntimeMXBean();
		int nCPUs = osbean.getAvailableProcessors();
		long prevUpTime = runbean.getUptime();
		long prevProcessCpuTime = getProcessCpuTime(osbean);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}

		osbean = ManagementFactory.getOperatingSystemMXBean();
		long upTime = runbean.getUptime();
		long processCpuTime = getProcessCpuTime(osbean);
		double javacpu;
		if (prevUpTime > 0L && upTime > prevUpTime) {
			long elapsedCpu = processCpuTime - prevProcessCpuTime;
			long elapsedTime = upTime - prevUpTime;
			javacpu = Math
					.min(99F, elapsedCpu / (elapsedTime * 10000F * nCPUs));
		} else {
			javacpu = 0.001;
		}

		int max = osbean.getAvailableProcessors() * 100;
		Long percentage = (long) ((javacpu * 100) / max);
		buffer = buffer.append(" Available Processors : "
				+ osbean.getAvailableProcessors());
		buffer = buffer.append(" CPU used : " + percentage + " %  ");
		Long warning = Long.valueOf(warningCondition);
		Long critical = Long.valueOf(critialCondition);
		if (percentage >= critical) {
			buffer = buffer.append(MXBeanConstants.RESULT_CRITICAL + " : "
					+ critical + " %");
		} else if (percentage >= warning) {
			buffer = buffer.append(MXBeanConstants.RESULT_WARNING + " : "
					+ warning + " %");
		} else {
			buffer = buffer.append(MXBeanConstants.RESULT_OK);
		}

		return buffer;

	}

	/**
	 * Using reflection to get CPU time as method 'getProcessCpuTime' is
	 * unavailable in java.lang.OperatingSystemMXBean which is present in Sun's
	 * implementation
	 * 
	 * @param osbean
	 * @return
	 * @throws Exception
	 */
	private static long getProcessCpuTime(OperatingSystemMXBean osbean)
			throws Exception {
		long value = -1;
		for (Method method : osbean.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().equals("getProcessCpuTime")
					&& Modifier.isPublic(method.getModifiers())) {

				try {
					value = (Long) method.invoke(osbean);
				} catch (Exception e) {
					throw e;
				} // try
				System.out.println(method.getName() + " = " + value);
			} // if
		} // for
		return value;
	}
}