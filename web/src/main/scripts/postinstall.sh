#!/bin/sh
# Title: post-install script
# Description: the post-install script is responsible for finishing up the installation
#  including starting up the Web application
#
echo ""
echo "Starting post-install script..."

if [ -f /opt/tomcat6/current/eurekawebapps/ROOT.war ]; then
  echo "    Found the ROOT.war Web archive file in the directory..."
  # if the ROOT.war Web archive file exists, create the ROOT directory and explode 
  #  the .WAR into it
  cd /opt/tomcat6/current/eurekawebapps
  echo "        creating the ROOT directory..."
      mkdir ROOT
      cd ROOT
  echo "        exploding the ROOT.war Web archive file..."
      /usr/java/latest/bin/jar -xf ../ROOT.war
  echo "        explosion complete."
else
  echo "    Did not find the ROOT.war Web archive file in the directory... halting the script with error code."
  # if the ROOT.war Web archive file was not present, something has gone wrong.
  # exit the install script with an error code
      exit 3
fi

    if [ -f /etc/init.d/memcached ]; then
	    echo "    Restarting memcached."
	    service memcached restart
	fi

# Obtain the PID for the running Tomcat server, if it exists
TOMCAT_PID=`ps -ef  |grep tomcat | grep java | grep -v grep | awk -F" " '{print $2}'`

# Check PID for value, if no value then Tomcat must not be running.
if [ "$TOMCAT_PID" == "" ]; then
   echo "    Tomcat is not running... starting the service"
   # start the Tomcat process
       service tomcat6 start
else
   echo "    Tomcat found to be running"
   exit 3
fi

# sleep 15 seconds while Tomcat starts up; if a failure occurs to halt Tomcat, it usually happens
#  within 15 seconds
sleep 15

# Obtain the PID for the running Tomcat server, if it exists
TOMCAT_PID=`ps -ef  |grep tomcat | grep -v grep | awk -F" " '{print $2}'`

# Check if the TOMCAT_PID has no value, this means the process is not running
if [ "$TOMCAT_PID" == "" ]; then
   echo "        Expected Tomcat to be running after service start, but Tomcat process was not found."
   # Exit the post-install script with an error code
       exit 4
else
   echo "        Tomcat found to be running... complete."
fi

echo "Ending post-install script"

# exit with a successful exit code
exit 0
