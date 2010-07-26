#!/bin/sh
# Title: pre-install script
# Description: 
#
echo " "
echo "Starting pre-install script..."
    
    echo "    Checking the status of the service..."
    service taskqueueprocessor status
    if [ $? -ne "0" ]; then
        echo "        Either the service does not exist or is not running."
    else
        echo "        Service is found to be running, stopping it."
        service taskqueueprocessor stop
        echo "        Service is stopped."
    fi
    echo "     Checked the status of the service."
    

echo "Ending pre-install script."

# exit with a successful exit code
exit 0
