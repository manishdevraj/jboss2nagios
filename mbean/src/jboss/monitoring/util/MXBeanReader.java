package jboss.monitoring.util;

import javax.management.openmbean.CompositeDataSupport;

/**
 * This class reads information from CompositeDataSupport returned from
 * org.jboss.as.jmx.PluggableMBeanServerImpl Information is being manipulated
 * for Heap usage and non Heap usage
 * 
 * @author Manish Devraj
 * 
 */
public class MXBeanReader {
	/**
	 * Retries Heap Memory usage in following format alerting monitoring service
	 * based on critical and warning level
	 * +----------------------------------------------+ +//////////////// | +
	 * +//////////////// | + +----------------------------------------------+
	 * 
	 * |--------| init |---------------| used |---------------------------|
	 * committed |----------------------------------------------| max
	 * 
	 * @param dataSenders
	 * @param critialCondition
	 * @param warningCondition
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer getHeapMemoryUsage(
			CompositeDataSupport dataSenders, String warningCondition,
			String critialCondition) throws Exception {
		StringBuffer buffer = new StringBuffer();
		if (dataSenders != null) {
			Long commited = (Long) dataSenders.get("committed");
			Long init = (Long) dataSenders.get("init");
			Long max = (Long) dataSenders.get("max");
			Long used = (Long) dataSenders.get("used");
			Long percentage = ((used * 100) / max);
			buffer = buffer.append(" commited   : " + commited / (1024 * 1024)
					+ " MB ");
			buffer = buffer.append(" init       : " + init / (1024 * 1024)
					+ " MB ");
			buffer = buffer.append(" max        : " + max / (1024 * 1024)
					+ " MB ");
			buffer = buffer.append(" used       : " + used / (1024 * 1024)
					+ " MB ");
			buffer = buffer.append(" percentage : " + percentage + " %  ");
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
		}
		return buffer;
	}
}
