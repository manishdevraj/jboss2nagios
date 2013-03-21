jboss2nagios
============

Jboss SAR MBean and Perl plug-in for Nagios compatible with Jboss 7.1.1

Description

Integrate JBoss into Nagios monitoring through a small Collector MBean and a perl based Nagios plugin. Lets you read you and monitor JMX values from JBoss servers very efficiently. On the Nagios server no JDK or JBoss installation is needed.
It allows you to monitor following 

1. HeapMemoryUsage
2. Non heap memory usage
3. CPU usage
4. Take heap dump

Installation:

1. Copy the collector.sar (from the mbean/ directory) to your JBoss deploy directory. Port 5566 is then open for the plugin to access it.
2. Copy the plugin check_mbean_collector (from the plugin/ directory) to the Nagios plugin directory on the Nagios server.
3. Edit your Nagios config to use the check_mbean_collector to monitor any attributes of any MBean.

You might check that the plugin and the MBean is working properly by doing a test run on the Nagios server:

./check_mbean_collector -H jbossserver -p 5566 -m java.lang:type=Memory -a HeapMemoryUsage -w 70 -c 90
Please note that you need the nagios-plugins package installed and of course replace "jbossserver" above with you server name.
Plugin usage:

Retrieve some MBean attribute value from a JBoss server through the collector MBean:
  check_mbean_collector -H host -p port -m mbean_name -a attribute_name -w warning_level -c critical_level
 
Usage: 
 check_mbean_collector -H host[,host,..] -p port -m mbean-name -a attribute-name -w warning-level -c critical-level
 check_mbean_collector [-h | --help]
 check_mbean_collector [-V | --version]
 
1. [host] The server running JBoss. Giving a comma separated list of hosts switches to a check for a singleton in a cluster.
2. [port] The port the deployed collector MBean is listening to
3. [mbean_name] The JMX name of the MBean that includes the attribute, e.g. ava.lang:type=Memory Use the ${some.env} notation to refer to a JVM environment variable on the server. In Nagios config files this must be escaped like this: $$\\{some.env}
4. [attribute_name] The name of the MBean attribute to retrieve, e.g. HeapMemoryUsage Prefix with * to get the difference between two calls (delta). ${...} can be used.
5. [warning_level] The level as a number from which on the WARNING status should be set
6. [critical_level] The level as a number from which on the CRITICAL status should be set

Commands
# cd /usr/local/nagios/libexec/

1. HeapMemoryUsage : ./check_mbean_collector -H jbossserver -p 5566 -m java.lang:type=Memory -a HeapMemoryUsage -w 70 -c 90
2. Non heap memory usage : ./check_mbean_collector -H jbossserver -p 5566 -m java.lang:type=Memory -a NonHeapMemoryUsage -w 70 -c 90
3. CPU usage : ./check_mbean_collector -H jbossserver -p 5566 -m java.lang:type=OperatingSystem -a CPUUsage -w 70 -c 90
4. Take heap dump : ./heap_util  -H jbossserver -p 5566  -m com.sun.management:type=HotSpotDiagnostic

SourceForge Project

The project is hosted on SoruceForge and has it's own project homepage https://sourceforge.net/projects/jboss2nagios/

Reference : http://sourceforge.net/projects/jboss2nagios
