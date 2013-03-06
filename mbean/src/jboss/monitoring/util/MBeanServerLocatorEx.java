package jboss.monitoring.util;

import javax.management.MBeanServer;
/**
 * This class can act as replacement for MBeanServerLocator.locateJBoss() which was compatible to older Jboss version
 * @author Manish Devraj
 *
 */
public class MBeanServerLocatorEx {
	/**
	 * Returns MbeanServer that is compatible to Jboss AS 7.1.1 JMXBeanServer
	 * @return
	 * @throws Exception
	 */
	public static MBeanServer locateJBoss() throws Exception{
		for(MBeanServer server: javax.management.MBeanServerFactory.findMBeanServer(null)) {
	        if("DefaultDomain".equals(server.getDefaultDomain())) return server;
	    }
	    throw new Exception("Failed to locate MBeanServer");
	}
	
}
