package jboss.monitoring.util;

public class MXBeanConstants {
	// Codes
	public static final String RESULT_WARNING = "WARNING ";
	public static final String RESULT_CRITICAL = "CRITICAL ";
	public static final String RESULT_OK = "OK ";
	public static final String RESULT_UNKNOWN = "UNKNOWN ";
	public static final String RESULT_NOTFOUND = "NOTFOUND";

	public static final String MEMORY_OBJ = "java.lang:type=Memory";
	public static final String HEAP_MEM_ATTRIBUTE = "HeapMemoryUsage";
	public static final String NON_HEAP_MEM_ATTRIBUTE = "NonHeapMemoryUsage";
	public static final String HOTSPOT_DIAGNOSTIC = "com.sun.management:type=HotSpotDiagnostic";
	public static final String OS_OBJ = "java.lang:type=OperatingSystem";
	public static final String CPU_ATTRIBUTE = "CPUUsage";

}
