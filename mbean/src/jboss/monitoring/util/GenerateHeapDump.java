package jboss.monitoring.util;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Class to get heap dump This class is useful when Nagios reports problem with
 * memory we can get heap dump on-demand
 * 
 * @author manish
 * 
 */
public class GenerateHeapDump {
	/**
	 * Method to generate heap dump
	 * 
	 * @param mbeanServer
	 * @param fileName
	 * @throws InstanceNotFoundException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws MalformedObjectNameException
	 */
	public static void getHeapDump(MBeanServer mbeanServer, String fileName)
			throws InstanceNotFoundException, ReflectionException,
			MBeanException, MalformedObjectNameException {
		ObjectName hotDiagMXBean = new ObjectName(
				"com.sun.management:type=HotSpotDiagnostic");
		System.getProperty("HOME");
		if (null != hotDiagMXBean) {
			Object[] params = new Object[] {
					System.getProperty("user.home") + "/" + fileName,
					Boolean.TRUE };
			String[] signature = new String[] { String.class.getName(),
					boolean.class.getName() };
			System.out.println(" Generating Dump at "
					+ System.getProperty("user.home") + fileName);
			Object result = mbeanServer.invoke(hotDiagMXBean, "dumpHeap",
					params, signature);
			System.out.println(" Heap Dump Generated to " + fileName);
		}
	}
}
