#!/bin/bash
# apply_updates.sh:  Applies database update scripts where necessary
if [[ -n %1 ]]
then
        force=%1
fi
debugmode=0
tempdbname=$1
tempdbuser=$2
tempdevhost=$3
xmlfile="/etc/eureka/eureka.xml"
newline="
"

# This script requires xmlstarlet to be installed.  Check to see which executable name is being used by xmlstarlet.
if command -v xml &>/dev/null; then xmlcommand=xml; else xmlcommand=xmlstarlet; fi

# Check the execution path.  The script must be run from the scripts directory.
exepath=`pwd`

function WriteLog {
        if [[ ! -e $logdir ]]
        then
                mkdir -p $logdir
        fi
        echo `date +%Y-%m-%d-%T` $* >> $fulllogfile
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


# Check to see which name this script ran as.  If it's dev_apply_updates.sh, then this was run by a developer.
scriptname=`basename $0`

if [[ "$scriptname" == 'dev_apply_updates.sh' ]]
then
        asdev=1;
        logfilebase=$tempdbname
        dbname=$tempdbname
        dbuser=$tempdbuser
        appurl=$tempdevhost
        logdir=$HOME/logs
        fulllogfile=$logdir/$logfilebase`date +%Y%m%d-%H%M`.log
        if [[ ! "$exepath" =~ 'target' ]]
        then
                # If running as a developer, the path must contain "target"
                WriteLog "[ERROR] You can only run database change scripts from within a deployed target."
                exit 1
        fi
        dbhost=localhost
        if [[ ! -e ~/tmp ]]
        then
                mkdir ~/tmp
        fi
        fullscript=~/tmp/$dbname_scripts_executed_`date +%Y-%m-%d-%H%M%S`.sql
else
        logfilebase=eurekastreams
        dbname=eurekastreams
        dbuser=eurekastreams
        logdir=/var/log/eureka-database
        fulllogfile=$logdir/$logfilebase`date +%Y%m%d-%H%M`.log
        appurl=`$xmlcommand sel -t -v "//url" $xmlfile`
        WriteLog "[DEBUG] App URL: $appurl"
        dbhost=`$xmlcommand sel -t -v "//node[capabilities/capability='Database Server']/network/hostname" $xmlfile`
        WriteLog "[DEBUG] DB Host: $dbhost"
        fullscript=/tmp/$dbname_scripts_executed_`date +%Y-%m-%d-%H%M%S`.sql
fi


if [[ ! $exepath =~ .*scripts$ ]]
then
        WriteLog "[ERROR] This script must be executed from within the directory where the scripts reside."
        exit 1
fi

function ApplyLoop ()
{
        # Concatenates the necessary scripts onto $fullscript

        # Iterate through all major versions from the current to the new
        WriteLog "[DEBUG] Iterating from major version $currmajor to $newmajor"
        for majorver in `seq $currmajor $newmajor`
        do
                WriteLog "[DEBUG] Iterating through major version $majorver"
                if [[ $majorver == $currmajor ]]
                then
                        minorstarter=$currminor
                else
                        minorstarter=`ls -1r $exepath/../migration/$majorver|tail -1`
                fi
                maxminorinmajor=`ls -1 $exepath/../migration/$majorver|tail -1`
                WriteLog "[DEBUG] Iterating from minor version $minorstarter to $maxminorinmajor"
                # Iterate through all minor versions from the current to the new
                for minorver in `seq $minorstarter $maxminorinmajor`
                do
                        if [[ $minorver == $currminor ]]
                        then
                                patchstarter=$((`echo $currpatch|sed 's/^0\+//g'`+1))
                        else
                                patchstarter=0
                        fi
                        maxpatchfile=`ls -1 $exepath/../migration/$majorver/$minorver|tail -1`
                        maxpatchinminor=$((`echo ${maxpatchfile:1:4}|sed 's/^0\+//g'`))

                        WriteLog "[DEBUG] Iterating in version $majorver.$minorver from patch $patchstarter to $maxpatchinminor"
                        # Iterate through all patches from the current (if applicable) to the new.
                        for patchver in `seq $patchstarter $maxpatchinminor`
                        do
                                formattedpatchver=`printf "%04d" $patchver`
                                for script in $exepath/../migration/$majorver/$minorver/U$formattedpatchver*.sql
                                do
                                        WriteLog "[INFO] Concatenating $script to the full execution script."
                                        WriteLog `echo "$newline$newline-- From $script$newline" >> $fullscript`
                                        WriteLog `cat $script>>$fullscript`
                                        exitcode=`echo $?`
                                        if [[ $exitcode == 0 ]]
                                        then
                                                returnval=0
                                        else
                                                WriteLog "[ERROR] Concatenation of $script failed with a bash exit code of "$exitcode
                                                WriteLog "[ERROR] A problem has occurred concatenating $script.  No database changes have occurred.  Check the log for details."
                                                returnval=$exitcode
                                                break
                                        fi
                                done
                        done
                done
        done
        WriteLog "[INFO] All applicable scripts have been concatenated."
        return $returnval
}

function ApplyBase ()
{
        # Apply schema script 1, then cat the rest for running in a transaction
        for dbscript in `ls -1 $exepath/../schema/*.sql -1`
        do
                if [[ $dbscript =~ '_1_' ]]
                then
                        WriteLog "[INFO] Replacing tokens in $dbscript..."
                        # Change database user
                        sed "s/OWNER TO eurekastreams/OWNER TO $dbuser/g" $dbscript >> $fullscript\_create.sql
                        # Change database name
                        sed -i "s/eurekastreams/$dbname/g" $fullscript\_create.sql
                        # Change the application URL
                        sed -i "s,http://localhost:8080,$appurl,g" $fullscript\_create.sql
                        WriteLog "[INFO] Executing $dbscript to initialize the database."
                        mycommand=`psql -h $dbhost -d postgres -f $fullscript\_create.sql 2>&1 1>/dev/null`
                        WriteLog "[DEBUG] Output from execution: $mycommand"
                        if [[ ! `echo $?` == 0 ]]
                        then
                                WriteLog "[ERROR] $dbscript had a problem executing.  Exit code is `echo $?`."
                                return 1
                        fi
                        if [[ $mycommand =~ "FATAL" ]]
                        then
                                WriteLog "[ERROR] A problem occurred.  See $fulllogfile for details."
                                exit 1
                        fi
                else
                        WriteLog "[INFO] Concatenating $dbscript to the master transaction script."
                        cat $dbscript>>$fullscript
                        if [[ ! `echo $?` == 0 ]]
                        then
                                WriteLog "$dbscript had a problem concatenating.  Exit code is `echo $?`."
                                return 1
                        fi
                fi
        done
        WriteLog "[INFO] All schema scripts have been concatenated successfully."
        return 0
}

function main ()
{
        WriteLog "[INFO] ===== Updates Script Starting====="
        # Get the current database version components.  If it doesn't exist, suppress the error.  It will be handled in the subsequent if
        currver=`psql -h $dbhost -d $dbname -t -c 'select major, minor, patch from db_version order by major desc, minor desc, patch desc limit 1;' 2> /dev/null`
        # Set startingversion so that we know how far to back out if there's a failure.
        if [[ $currver != "" ]]
        then
                old_IFS=$IFS
                IFS="|"
                currverarr=( $currver )
                currmajor=${currverarr[0]//[[:space:]]}
                currminor=${currverarr[1]//[[:space:]]}
                currpatch=${currverarr[2]//[[:space:]]}
                IFS=$old_IFS
                WriteLog "[INFO] Current database version:  " $currver
        else
                if [[ -n $asdev ]] && [[ ! -n $force ]]
                then
                        WriteLog "[ERROR] No database found.  Aborting.  If you want to overwrite your database, you must run dev_reset_db.sh instead."
                        exit 1
                else
                        WriteLog "[INFO] No database found.  Creating the $dbuser user."
                        createuser -S -D -R $dbuser
                        # Initialize version variables to the lowest possible in the system
                        currmajor=0
                        currminor=7
                        currpatch=0
                fi
        fi

# Get the deployment's database version
        newmajor=`ls -1 $exepath/../migration|tail -1`
        newminor=`ls -1 $exepath/../migration/$newmajor|tail -1`
        newpatch=`ls -1 $exepath/../migration/$newmajor/$newminor|tail -1`
        newpatch=$((`echo ${newpatch:1:4}|sed 's/^0\+//g'`))
        WriteLog "[INFO] Eureka database version attempting to be applied:  $newmajor $newminor $newpatch"
        if [[ $newmajor -eq $currmajor ]] && [[ $newminor -eq $currminor ]] && [[ $newpatch -eq `echo $currpatch|sed 's/^[0]*//'` ]]
        then
                WriteLog "[INFO] New version is the same as the version being applied.  No database scripts will run."
        else
                # If there is no version in the database, cat the schema scripts into the $fullscript variable
                if [[ $currver == "" ]]
                then
                        WriteLog "[INFO] Installing the baseline schema"
                        newdb=true
                        ApplyBase
                        if [[ ! `echo $?` -eq 0 ]]
                        then
                                WriteLog "[ERROR] Concatenation of the baseline schema has failed.  Check the log for details."
                                exit 1
                        fi
                fi
        # Cat all patches since the latest upgrade version into the $fullscript variable
                ApplyLoop

        # Replace all localhost:8080 tokens with the proper cname and port
                #Todo:  Implement sed commands
                WriteLog "[DEBUG] Replacing tokens in the database scripts..."
                # Change database user
                sed -i "s/OWNER TO eurekastreams/OWNER TO $dbuser/g" $fullscript
                # Change database name
                sed -i "s/ eurekastreams/ $dbname/g" $fullscript
                # Change the application URL
                sed -i "s,http://localhost:8080,$appurl,g" $fullscript

        # Run the $fullscript
                WriteLog "[INFO] Modifying the database..."
                #Todo:  Connect to the remote instance instead of assuming a local application of the script below.
                # Note:  connect to the postgres database, then let the scripts do the connect to the $dbname database
                mycommand=`psql -U $dbuser -h $dbhost -d $dbname -t -f $fullscript -1 2>&1`
                WriteLog "[DEBUG] Output from execution: $mycommand"
                if [[ $mycommand =~ 'commands ignored until end of transaction block$' ]] || [[ $mycommand =~ "FATAL" ]] || [[ $mycommand =~ "ERROR" ]]
                then
                        WriteLog "[ERROR] A problem occurred with the application of the latest scripts.  Your database has not been updated. See the log file for details: $fulllogfile"
                        if [[ -n $newdb ]]
                        then
                                dropdb $dbname
                        fi
                        exit 1
                else
                        WriteLog "[INFO] Script Successfully Completed"
                        # After successful completion, remove the concatenated script and creation script
                        rm -f $fullscript
                        rm -f $fullscript\_create.sql
                        exit 0
                fi
        fi
}
main

