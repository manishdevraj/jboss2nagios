Installation
============

1. Copy the collector.sar (from the mbean/ directory) to your JBoss deploy directory. Port 5566 is then open for the plugin to access it.
2. Copy the plugin check_mbean_collector (from the plugin/ directory) to the Nagios plugin directory on the Nagios server.
3. Edit your Nagios config to use the check_mbean_collector to monitor any attributes of any MBean.

You might check that the plugin and the MBean is working properly by doing a test run on the Nagios server:

./check_mbean_collector -H jbossserver -p 5566 -m java.lang:type=Memory -a HeapMemoryUsage -w 70 -c 90
Please note that you need the nagios-plugins package installed and of course replace "jbossserver" above with you server name.

Please note 
a) you may need to 'chmod a+x' the plugin first
b) you need the nagios-plugins package installed and of course replace "jbossserver" above with you server name.


Configuration
=============

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

Nagios Integration
==================

Your command definition (e.g. in commands.cfg) could look like this:

# 'check_mbean_collector' command definition
define command{
  command_name	check_mbean_collector
  command_line 	$USER1$/check_mbean_collector -H $HOSTADDRESS$ -p 5566 -m $ARG1$ -a $ARG2$ -w $ARG3$ -c $ARG4$
  }


Now you can specify the services you want to monitor:

define service{
  use                  local-service         ; Name of service template to use
  host_name            localhost
  service_description  JBoss Active Threads
  check_command        check_mbean_collector!java.lang:type=Memory!HeapMemoryUsage!70!90
  }

Tips
====
 * How to integrate JBoss and JDK MBeans into one server to monitor all of them through jboss2nagios: http://community.jboss.org/wiki/JBossMBeansInJConsole
   Unfortunately this won't work in JBoss 5.x anymore due to an error in JBoss.
 * Suggestions for attributes of MBeans you might want to monitor / graph: http://sourceforge.net/apps/trac/jboss2nagios/wiki/MBeanSuggestions
 * Add your configuration to the list of known working configurations: http://sourceforge.net/apps/trac/jboss2nagios/wiki/VersionMatrix
 
