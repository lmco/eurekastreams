#!/usr/bin/perl
use POSIX;
use Data::UUID;
use Switch;

#--------------------------
#--
#--------------------------

# -- 100,000 
my $numberofactivities = 100000;

# -- open the files for overwriting
	open (ACTIVITIESSENTENCEFILE, '> ../datapopulation/datafiles/activitiessentences.txt');
# ----------------------------------------------------------------------------------------

	# -- loads the data files
			@fivecharwords = `cat ../datapopulation/datafiles/5charwords.txt`;
			my $fivecharwordssize = @fivecharwords;
			@sixcharwords = `cat ../datapopulation/datafiles/6charwords.txt`;
			my $sixcharwordssize = @sixcharwords;
			@sevencharwords = `cat ../datapopulation/datafiles/7charwords.txt`;
			my $sevencharwordssize = @sevencharwords;
			@eightcharwords = `cat ../datapopulation/datafiles/8charwords.txt`;
			my $eightcharwordssize = @eightcharwords;

	# -- start group create main loop
		for ($i = 1; $i <= $numberofactivities; $i++) {			
			my $activitysize = ((floor(rand(4)))+2);
			my $activitysentence = "";
			    for ($j = 1; $j <= $activitysize; $j++) {
   			    	$word = @eightcharwords[(floor(rand($eightcharwordssize)))];
   			    	chop($word);
			    	$activitysentence = $activitysentence.$word." ";

			    	$word = @sevencharwords[(floor(rand($sevencharwordssize)))];
   			    	chop($word);
			    	$activitysentence = $activitysentence.$word." ";

			    	$word = @fivecharwords[(floor(rand($fivecharwordssize)))];	
   			    	chop($word);
			    	$activitysentence = $activitysentence.$word." ";	
			    				    	
			    	$word = @sixcharwords[(floor(rand($sixcharwordssize)))]; 
   			    	chop($word);
			    	$activitysentence = $activitysentence.$word." ";
			    				    				    				    	
			    	$word = @sevencharwords[(floor(rand($sevencharwordssize)))];
   			    	chop($word);
			    	$activitysentence = $activitysentence.$word." ";
			    				    				    	
			    	$word = @eightcharwords[(floor(rand($eightcharwordssize)))];
   			    	chop($word);
			    	$activitysentence = $activitysentence.$word." ";
			    }
				chop($activitysentence);
				# -- given the activity sentence size, prepend the serialized size code
				#  including a double back slash to prepare for a database copy string
				switch ($activitysize) {
				    case 2      { print ACTIVITIESSENTENCEFILE "g" }
				    case 3      { print ACTIVITIESSENTENCEFILE "\\\\226" }
                    case 4      { print ACTIVITIESSENTENCEFILE "\\\\305" }
                    case 5      { print ACTIVITIESSENTENCEFILE "\\\\364" }                    
				}
				# -- write out the activity
				print ACTIVITIESSENTENCEFILE $activitysentence;
				# -- add the trailing "x" found at the end of each String along with the dummy 
				#  space and person table id.
				print ACTIVITIESSENTENCEFILE "0000000000x\n";
		}

		# -- close the files
        	close (ACTIVITIESSENTENCEFILE);

# -- end.