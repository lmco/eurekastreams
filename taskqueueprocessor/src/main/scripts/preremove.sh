#!/bin/sh
# Title: pre-remove script
# Description: 
#
PACKAGES_LEFT=$1
echo "Starting pre-remove script..."
echo " Package instances left after this removal: $PACKAGES_LEFT"

if [ "$PACKAGES_LEFT" == "0" ]; then
    echo "    Removing the package from the system..."
    
    echo "Stopping the process..."
    service taskqueueprocessor stop
    echo "The process has been stopped."

    echo "Removing the startup script configuration."
    chkconfig taskqueueprocessor off
    chkconfig --del taskqueueprocessor
    
    echo "Deleting the symbolic link."
    rm -f /etc/rc.d/init.d/taskqueueprocessor
    
    echo "Removing the logs directory."
    rm -rf /opt/taskqueueprocessor/logs

    echo "    The package has been removed."
else
  echo "    An upgrade is occuring..."
  echo "    An upgrade has occured."
fi

echo "Ending pre-remove script."

# exit with a successful exit code
exit 0
