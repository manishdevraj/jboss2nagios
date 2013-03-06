Installation
============

1. Copy the collector.sar (from the mbean/ directory) to your JBoss deploy directory. Port 5566 is then open for the plugin to access it.
2. Copy the plugin check_mbean_collector (from the plugin/ directory) to the Nagios plugin directory on the Nagios server (/usr/lib/nagios/plugins/ on Fedora).
3. Edit your Nagios config to use the check_mbean_collector to monitor any attributes of any MBean. 

Check the plugin's help for more options.

You might check, if the plugin and the MBean is working properly by doing a test run on the Nagios server:

./check_mbean_collector -H jbossserver -p 5566 -m jboss.system:type=ServerInfo -a ActiveThreadCount -w 200 -c 400

Please note 
a) you may need to 'chmod a+x' the plugin first
b) you need the nagios-plugins package installed and of course replace "jbossserver" above with you server name.


Configuration
=============

 check_mbean_collector -H host[,host,..] -p port -m mbean-name -a attribute-name -w warning-level -c critical-level
 check_mbean_collector [-h | --help]
 check_mbean_collector [-V | --version]

  <host>           The server running JBoss.
                   Giving a comma separated list of hosts switches to a check for a singleton in a cluster.
  <port>           The port the deployed collector MBean is listening to
  <mbean_name>     The JMX name of the MBean that includes the attribute, e.g. jboss.system:type=ServerInfo
                   Use the ${some.env} notation to refer to a JVM system property on the server.
                   In Nagios config files this must be escaped like this: $$\\{some.env}
  <attribute_name> The name of the MBean attribute to retrieve, e.g. ActiveThreadCount
                   For attributes which are collections the number of entries can be checked by
                   appending [] to the attribute name.
                   Prefix the attribute name with a * to get the difference between two calls (delta).
                   ${some.env} can be used (see mbean_name).
  <warning_level>  The level as a number from which on the WARNING status should be set
  <critical_level> The level as a number from which on the CRITICAL status should be set
                   If you are checking an attribute that is not a Number the specified text will raise the
                   specific level if it can be found in the textual representation of the attribute
                   For specifying ranges or 'less than' triggers use the Nagios range notation
                   (see http://nagiosplug.sourceforge.net/developer-guidelines.html#THRESHOLDFORMAT)


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
  check_command        check_mbean_collector!jboss.system:type=ServerInfo!ActiveThreadCount!200!400
  }

        

Tips
====
 * How to integrate JBoss and JDK MBeans into one server to monitor all of them through jboss2nagios: http://community.jboss.org/wiki/JBossMBeansInJConsole
   Unfortunately this won't work in JBoss 5.x anymore due to an error in JBoss.
 * Suggestions for attributes of MBeans you might want to monitor / graph: http://sourceforge.net/apps/trac/jboss2nagios/wiki/MBeanSuggestions
 * Add your configuration to the list of known working configurations: http://sourceforge.net/apps/trac/jboss2nagios/wiki/VersionMatrix

