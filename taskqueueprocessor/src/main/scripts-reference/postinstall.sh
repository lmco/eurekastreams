#!/bin/sh
# Title: post-install script
# Description: 
#
echo ""
echo "Starting post-install script..."

    echo "Creating the symbolic link to the system start-up script"
	if [ -e /etc/rc.d/init.d/taskqueueprocessor ];
	then
	    rm /etc/rc.d/init.d/taskqueueprocessor
	fi
    ln -s /opt/taskqueueprocessor/bin/taskqueueprocessor /etc/rc.d/init.d/taskqueueprocessor
    
    echo "Adding the system startup script to the run-level scripts and turning it on"
    chkconfig --add taskqueueprocessor
    chkconfig taskqueueprocessor on
    
    echo "Creating the logs directory."
    if [ ! -d /opt/taskqueueprocessor/logs ]; 
    then
	    mkdir /opt/taskqueueprocessor/logs
	fi

    echo "Starting the service..."
    service taskqueueprocessor start
    if [ $? -ne "0" ]; then
        echo "An error has occured starting the process."
    else
        echo "The service has been started."
    fi
    
echo "Ending post-install script"

# exit with a successful exit code
exit 0
