#!/bin/bash
# dev_reset_db.sh:  For developers.  Removes and recreates the database, then applies updates.
debugmode=0
fullscript=/tmp/$dbname_scripts_executed_`date +%Y-%m-%d-%H%M%S`.sql

# This script requires xmlstarlet to be installed.  Check to see which executable name is being used by xmlstarlet.
if command -v xml &>/dev/null; then xmlcommand=xml; else xmlcommand=xmlstarlet; fi

# Check the execution path.  The script must be run from the scripts directory.
exepath=`pwd`
logdir=$HOME/logs
tempdbname=$1
tempdbuser=$2
tempdevhost=$3

function WriteLog {
	# Log file rolls over daily
	if [[ ! -e $logdir ]]
	then
		mkdir -p $logdir
	fi
	echo `date +%Y-%m-%d-%T` $* >> $logdir/$logfilebase`date +%Y%m%d`.log
	if [[ $* =~ "^\[DEBUG" ]] 
	then
		if [[ $debugmode == 1 ]]
		then
			echo $@
		fi
	else
		echo $@
	fi
}

function main {
if [[ ! "$exepath" =~ 'scripts$' ]]
then
	WriteLog "[ERROR]:  This script must be executed from within the directory where the scripts reside."
	exit 1
fi

if [[ ! "$exepath" =~ 'target' ]]
then
		# If running as a developer, the path must contain "target"
		WriteLog "[ERROR]:  You can only run database change scripts from within a deployed target."
		exit 1
fi

WriteLog "[INFO] Dropping the existing database $tempdbname"
mycommand=`dropdb $tempdbname 2>&1`
if [[ `echo $?` -eq 0 ]]
then
	WriteLog "[INFO]: Database Dropped Successfully."
elif [[ $mycommand =~ 'does not exist$' ]]
then
	WriteLog "[INFO]: The database did not exist.  Continuing anyway."
else
	WriteLog "[ERROR]: A problem occurred with the database drop.  Your database has not been refreshed."
	WriteLog "[DEBUG]: Output of the offending drop command: $mycommand"
	exit 1
fi
WriteLog "[INFO]: Running schema and upgrades."
./dev_apply_updates.sh $tempdbname $tempdbuser $tempdevhost force
}
main
exit 0
