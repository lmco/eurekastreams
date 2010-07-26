#!/bin/sh
# Title: post-install script
# Description: the post-install script is responsible for finishing up the installation
#  including starting up the Web application
#
echo ""
echo "Starting post-install script..."

if [ -f /var/eureka/db/scripts/apply_updates.sh ]; then
	echo "    Updating the database to the latest version..."
	su - postgres -c "cd /var/eureka/db/scripts; ./apply_updates.sh"
	if [ ! `echo $?` -eq 0 ]; then
		echo "    A problem has occurred with the database.  Aborting the script with error code."
		exit -3
	fi
	echo "    Database changes complete."
else
	echo "    Did not find the database scripts... halting the script with error code."
	exit -3
fi

echo "Ending post-install script"

# exit with a successful exit code
exit 0
