#!/usr/bin/perl
use POSIX;
use Data::UUID;
use List::Util qw/shuffle/;

#--------------------------
#-- The people table has three columns: id, accountid, streamid
#--
#--------------------------

# -- 100,000 people
#my $numberofpeople = 100000;
my $numberofpeople = 25000;

# -- 10,000 groups
#my $numberofdomaingroups = 10000;
my $numberofdomaingroups = 2500;

# -- 6 orgs for people assignment
my $orgsforpeople = 6;

# -- 8 orgs for group assignemnt
my $orgsfordomaingroups = 8;

# -- table offsets
my $orgtableoffset = 1;
my $gadgettableoffset = 14;
my $streamscopetableoffset = 4;
my $streamviewtableoffset = 4;
my $tabtableoffset = 4;
my $tabgrouptableoffset = 4;
my $tabtemplatetableoffset = 4;
# -----------------------------------

# -- open the files for overwriting
	open (PERSONTABLECOPYFILE, '> eurekastreams_35_persontablecopy.sql');
	open (ORGANIZATIONTABLECOPYFILE, '> eurekastreams_35_organizationtablecopy.sql');
	open (DOMAINGROUPTABLECOPYFILE, '> eurekastreams_35_domaingrouptablecopy.sql');
	open (FOLLOWERTABLECOPYFILE, '> eurekastreams_35_followertablecopy.sql');
	open (GROUPFOLLOWERTABLECOPYFILE, '> eurekastreams_35_groupfollowertablecopy.sql');
	open (GADGETTABLECOPYFILE, '> eurekastreams_35_gadgettablecopy.sql');
	open (STREAMSCOPETABLECOPYFILE, '> eurekastreams_35_streamscopetablecopy.sql');
	open (STREAMVIEWTABLECOPYFILE, '> eurekastreams_35_streamviewtablecopy.sql');
	open (TABTABLECOPYFILE, '> eurekastreams_35_tabtablecopy.sql');
	open (TABGROUPTABLECOPYFILE, '> eurekastreams_35_tabgrouptablecopy.sql');
	open (TABTEMPLATETABLECOPYFILE, '> eurekastreams_35_tabtemplatetablecopy.sql');
	open (STREAMVIEWSTREAMSCOPETABLECOPYFILE, '> eurekastreams_35_streamview_streamscopetablecopy.sql');
	open (PERSONSTREAMVIEWTABLECOPYFILE, '> eurekastreams_35_person_streamviewtablecopy.sql');
	open (PERSONACTIVITYTABLECOPYFILE, '> eurekastreams_35_person_activitytablecopy.sql');
	open (STREAMSEARCHTABLECOPYFILE, '> eurekastreams_35_streamsearchtablecopy.sql');
	open (PERSONSTREAMSEARCHTABLECOPYFILE, '> eurekastreams_35_person_streamsearchtablecopy.sql');
	open (STREAMSEARCHKEYWORDSTABLECOPYFILE, '> eurekastreams_35_streamsearchkeywords.sql');
# ----------------------------------------------------------------------------------------

	# -- write database header record
    my $dbheader = "--\n-- PostgreSQL\n--\nSET client_encoding = 'UTF8';\nSET standard_conforming_strings = off;\nSET check_function_bodies = false;\nSET client_min_messages = warning;\nSET escape_string_warning = off;\n\n\\connect eurekastreams\n\nSET client_encoding = 'UTF8';\nSET standard_conforming_strings = off;\nSET check_function_bodies = false;\nSET client_min_messages = warning;\nSET escape_string_warning = off;\n\nSET search_path = public, pg_catalog;\n\nSET default_tablespace = '';\n\nSET default_with_oids = false;\n\n\n--\n";
			print PERSONTABLECOPYFILE $dbheader;
			print ORGANIZATIONTABLECOPYFILE $dbheader;
			print DOMAINGROUPTABLECOPYFILE $dbheader;
			print FOLLOWERTABLECOPYFILE $dbheader;
            print GROUPFOLLOWERTABLECOPYFILE $dbheader;			
			print GADGETTABLECOPYFILE $dbheader;
			print STREAMSCOPETABLECOPYFILE $dbheader;
			print STREAMVIEWTABLECOPYFILE $dbheader;
			print TABTABLECOPYFILE $dbheader;
			print TABGROUPTABLECOPYFILE $dbheader;
			print TABTEMPLATETABLECOPYFILE $dbheader;
			print STREAMVIEWSTREAMSCOPETABLECOPYFILE $dbheader;
			print PERSONSTREAMVIEWTABLECOPYFILE $dbheader;
            print PERSONACTIVITYTABLECOPYFILE $dbheader;
            print STREAMSEARCHTABLECOPYFILE $dbheader;
            print PERSONSTREAMSEARCHTABLECOPYFILE $dbheader;
            print STREAMSEARCHKEYWORDSTABLECOPYFILE $dbheader;
	
	# -- write copy header record	
			print PERSONTABLECOPYFILE "COPY person (id, version, accountid, avatarcropsize, avatarcropx, avatarcropy, avatarid, biography, cellphone, dateadded, email, fax, firstname, followerscount, followingcount, groupscount, lastname, location, middlename, opensocialid, overview, preferredname, quote, title, workphone, parentorganizationid, profiletabgroupid, starttabgroupid, themeid, personid, updatescount, entitystreamviewid, streamsearchhiddenlineindex, streamviewhiddenlineindex, lastacceptedtermsofservice, streamscopeid, commentable, streampostable, accountlocked) FROM stdin;\n";
			print ORGANIZATIONTABLECOPYFILE "COPY organization (id, version, avatarid, avatarcropsize, avatarcropx, avatarcropy, bannerbackgroundcolor, bannerid, descendantemployeecount, descendantgroupcount, descendantorganizationcount, employeefollowercount, missionstatement, name, overview, shortname, url, parentorganizationid, profiletabgroupid, themeid, updatescount, entitystreamviewid, alluserscancreategroups, streamscopeid) FROM stdin;\n";
			print DOMAINGROUPTABLECOPYFILE "COPY domaingroup (id, version, avatarcropsize, avatarcropx, avatarcropy, avatarid, bannerbackgroundcolor, bannerid, dateadded, followerscount, missionstatement, name, privatesearchable, publicgroup, shortname, url, parentorganizationid, profiletabgroupid, updatescount, themeid, overview, entitystreamviewid, ispending, createdbyid, streamscopeid, commentable, streampostable) FROM stdin;\n";
			print FOLLOWERTABLECOPYFILE "COPY follower (followerid, followingid) FROM stdin;\n";
			print GROUPFOLLOWERTABLECOPYFILE "COPY groupfollower (followerid, followingid) FROM stdin;\n";
			print GADGETTABLECOPYFILE "COPY gadget (id, version, datedeleted, deleted, minimized, zoneindex, zonenumber, gadgetdefinitionid, ownerid, tabtemplateid, gadgetuserpref) FROM stdin;\n";
			print STREAMSCOPETABLECOPYFILE "COPY streamscope (id, version, scopetype, uniquekey) FROM stdin;\n";
			print STREAMVIEWTABLECOPYFILE "COPY streamview (id, version, name, type) FROM stdin;\n";
			print TABTABLECOPYFILE "COPY tab (id, version, datedeleted, deleted, tabindex, tabgroupid, templateid) FROM stdin;\n";
			print TABGROUPTABLECOPYFILE "COPY tabgroup (id, version) FROM stdin;\n";
			print TABTEMPLATETABLECOPYFILE "COPY tabtemplate (id, version, datedeleted, deleted, tablayout, tabname, type) FROM stdin;\n";
			print STREAMVIEWSTREAMSCOPETABLECOPYFILE "COPY streamview_streamscope (streamview_id, includedscopes_id) FROM stdin;\n";
			print PERSONSTREAMVIEWTABLECOPYFILE "COPY person_streamview (person_id, streamviews_id, streamviewindex) FROM stdin;\n";
            print PERSONACTIVITYTABLECOPYFILE "COPY activity (id, version, actorid, actortype, annotation, appid, baseobject, baseobjecttype, location, mood, opensocialid, originalactorid, originalactortype, postedtime, updated, verb, recipientparentorgid, streamscopeid, originalactivityid,isdestinationstreampublic) FROM stdin;\n";
			print STREAMSEARCHTABLECOPYFILE "COPY streamsearch (id, version, name, streamview_id) FROM stdin;\n";
			print PERSONSTREAMSEARCHTABLECOPYFILE "COPY person_streamsearch (person_id, streamsearches_id, streamsearchindex) FROM stdin;\n";
			print STREAMSEARCHKEYWORDSTABLECOPYFILE "COPY streamsearch_keywords (streamsearch_id, element) FROM stdin;\n";
	# ----------------------------------------------------------------------------------------

	# -- setup the data files
			@totalwords = `cat ../datapopulation/datafiles/totalwords.txt`;
			my $totalwordssize = @totalwords;
			@accounts = `cat ../datapopulation/datafiles/accounts.txt`;
			my $accountssize = @accounts;
			@groups = `cat ../datapopulation/datafiles/groups.txt`;
			my $groupssize = @groups;
			@eightcharwords = `cat ../datapopulation/datafiles/8charwords.txt`;
			my $eightcharwordsize = @eightcharwords;
			@followercounts = `cat ../datapopulation/datafiles/followercountfor100kusers.txt`;
            @personactivitiescount = `cat ../datapopulation/datafiles/personactivitycountfor100kusers.txt`;
            @activitiessentences = `cat ../datapopulation/datafiles/activitiessentences.txt`;
            my $activitiessentencessize = @activitiessentences;
            @lastnames = `cat ../datapopulation/datafiles/lastnames.txt`;
            my $lastnamecount = @lastnames;
            @firstnames = `cat ../datapopulation/datafiles/firstnames.txt`;
            my $firstnamecount = @firstnames;
            

	# -- running variables for table ids
			my $gadgetid = $gadgettableoffset + 1;
			my $streamscopeid = $streamscopetableoffset + 1;
			my $streamviewid = $streamviewtableoffset + 1;
			my $tabid = $tabtableoffset + 1;
			my $tabgroupid = $tabgrouptableoffset + 1;
			my $tabtemplateid = $tabtemplatetableoffset + 1;
			my $personid = 0;
			my @people_ids = (1..$numberofpeople);
			my $activityid = 1;
			my $streamsearchid = 1;
	
	# -- UUID variable		
			my $ug    = new Data::UUID;			
			
	# -- array to count how many people a person is following
            my @person_following;

# ------------------------------------------------------------------------------
			
            # -- -------------------------------
            # --
            # -- Main organization create block (9 organizations)
            # --
            # -- -------------------------------

			print ORGANIZATIONTABLECOPYFILE "1\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t4\t0\tTo be the best\tComputer\t\\N\tcomputer\thttp://www.lockheedmartin.com\t1\t1\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tisgs\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tcomputer\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";
					
			print ORGANIZATIONTABLECOPYFILE "2\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t0\t0\tTo be Compaq\tCompaq\t\\N\tcompaq\thttp://www.lockheedmartin.com\t8\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";					                                    	
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tlegal\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tcompaq\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";

			print ORGANIZATIONTABLECOPYFILE "3\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t0\t0\tTo be Acer\tAcer\t\\N\tacer\thttp://www.lockheedmartin.com\t8\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tcivil\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tacer\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";					
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";

			print ORGANIZATIONTABLECOPYFILE "4\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t0\t0\tTo be Asus\tAsus\t\\N\tasus\thttp://www.lockheedmartin.com\t1\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";					                                    						
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tasus\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tasus\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";

			print ORGANIZATIONTABLECOPYFILE "5\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t0\t0\tTo be Sun\tSun\t\\N\tsun\thttp://www.lockheedmartin.com\t9\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";					                                    						
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tsun\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tsun\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";

			print ORGANIZATIONTABLECOPYFILE "6\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t0\t0\tTo be Apple\tApple\t\\N\tapple\thttp://www.lockheedmartin.com\t9\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";					                                    	
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tapple\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tapple\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";

			print ORGANIZATIONTABLECOPYFILE "7\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t0\t0\tTo be Dell\tDell\t\\N\tdell\thttp://www.lockheedmartin.com\t1\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";					                                    						
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tdell\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tdell\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";		

			print ORGANIZATIONTABLECOPYFILE "8\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t2\t0\tTo be HP\tHP\t\\N\thp\thttp://www.lockheedmartin.com\t1\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tdefense\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\thp\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";

			print ORGANIZATIONTABLECOPYFILE "9\t0\t\\N\t\\N\t\\N\t\\N\tFFFFFF\t\\N\t0\t0\t2\t0\tTo be Gateway\tGateway\t\\N\tgateway\thttp://www.lockheedmartin.com\t1\t".$tabgroupid."\t\\N\t0\t".$streamviewid."\tt\t".$streamscopeid."\n";					                                    	
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\tcomputer\t"."\\N"."\n";
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."ORGANIZATION"."\tgateway\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."3"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";
	    # ----------------------------------------------------------------------

        # -- -------------------------------
        # --
        # -- Main group create loop
        # --
        # -- -------------------------------
        
		for ($i = 1; $i <= $numberofdomaingroups; $i++) {
			my $groupid = @groups[($i-1)];
			    chop($groupid);
				# -- randomize the org that a person is in; +1 for 1-$orgsforpeople
				my $parentorgid = (((floor(rand($orgsfordomaingroups)))+1)+1);
		
				print DOMAINGROUPTABLECOPYFILE $i."\t"."0"."\t"."\\N"."\t"."\\N"."\t"."\\N"."\t"."\\N"."\t"."FFFFFF"."\t"."\\N"."\t"."2009-12-17 12:09:57.471"."\t"."0"."\t"."\\N"."\t".$groupid." Group"."\t"."f"."\t"."t"."\t".$groupid."\t"."\\N"."\t".$parentorgid."\t".$tabgroupid."\t"."0"."\t"."\\N"."\t"."\\N"."\t".$streamviewid."\t"."f"."\t"."1"."\t".$streamscopeid."\t"."t"."\t"."t"."\n";
				# -- create the 5 gadget records
					print GADGETTABLECOPYFILE $gadgetid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."f"."\t"."0"."\t"."0"."\t"."13"."\t"."\\N"."\t".$tabtemplateid."\t"."\\N"."\n";
					print GADGETTABLECOPYFILE $gadgetid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."f"."\t"."1"."\t"."0"."\t"."14"."\t"."\\N"."\t".$tabtemplateid."\t"."\\N"."\n";
										
				# -- create the 2 tab records
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t".$tabtemplateid."\n";
														
				# -- create the single tabtemplate record
					print TABTEMPLATETABLECOPYFILE $tabtemplateid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."TWOCOLUMN"."\t"."Welcome"."\t"."\\N"."\n";
	
				# -- create the 2 tabgroup records
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";
					
				# -- create the single streamview_streamscope record
					print STREAMVIEWSTREAMSCOPETABLECOPYFILE $streamviewid."\t".$streamscopeid."\n";

				# -- create the single streamview record
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\t".$groupid."\t"."\\N"."\n";

				# -- create the 2 streamscope records
					print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."GROUP"."\t".$groupid."\n";				
		}
	    # ----------------------------------------------------------------------

        # -- -------------------------------
        # --
        # -- Main person create loop
        # --
        # -- -------------------------------

		for ($i = 1; $i <= $numberofpeople; $i++) {
			my $accountid = @accounts[($i-1)];
			    chop($accountid);
				$personid = $i;
				$person_lastname = @lastnames[(floor(rand($lastnamecount)))];
				chop($person_lastname);
                $person_firstname = @firstnames[(floor(rand($firstnamecount)))];
				chop($person_firstname);					
									
				my $raw_uuid = $ug->create();									
				my $uuid = $ug->to_string($raw_uuid);

				# -- randomize the org that a person is in; +1 for 1-$orgsforpeople
				my $parentorgid = (((floor(rand($orgsforpeople)))+1)+1);
				
				my $jobtitleword = @eightcharwords[floor(rand($eightcharwordsize))];					
					chop ($jobtitleword);
				my $jobtitle = $jobtitleword." App SW Engr Sr Stf";
				
				my $followers = @followercounts[($i-1)];
				
				# -- create the single person record
				print PERSONTABLECOPYFILE $personid."\t","0"."\t".$accountid."\t"."\\N"."\t"."\\N"."\t"."\\N"."\t"."\\N"."\t"."\\N"."\t"."\\N"."\t"."2009-08-17 13:57:28.265"."\t".$person_firstname.".".$person_lastname."\@example.com"."\t"."\\N"."\t".$person_firstname."\t"."0"."\t"."0"."\t"."0"."\t".$person_lastname."\t"."\\N"."\t"."H"."\t".$uuid."\t"."\\N"."\t".$person_firstname."\t"."\\N"."\t".$jobtitle."\t"."\\N"."\t".$parentorgid."\t".$tabgroupid."\t".($tabgroupid+1)."\t"."\\N"."\t"."\\N"."\t"."0"."\t".$streamviewid."\t"."2"."\t"."3"."\t"."2009-12-07 13:25:39.242"."\t".$streamscopeid."\t"."t"."\t"."t"."\t"."f"."\n";
																														
				# -- create the 5 gadget records
					print GADGETTABLECOPYFILE $gadgetid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."f"."\t"."0"."\t"."0"."\t"."23"."\t"."\\N"."\t".$tabtemplateid."\t"."\\N"."\n";
					print GADGETTABLECOPYFILE $gadgetid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."f"."\t"."1"."\t"."0"."\t"."24"."\t"."\\N"."\t".$tabtemplateid."\t"."\\N"."\n";
					print GADGETTABLECOPYFILE $gadgetid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."f"."\t"."1"."\t"."1"."\t"."25"."\t"."\\N"."\t".$tabtemplateid."\t"."\\N"."\n";
										
				# -- create the 2 tab records
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".$tabgroupid."\t"."2"."\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."0"."\t".($tabgroupid+1)."\t".$tabtemplateid."\n";
														
				# -- create the single tabtemplate record
					print TABTEMPLATETABLECOPYFILE $tabtemplateid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."TWOCOLUMN"."\t"."Welcome"."\t"."\\N"."\n";
	
				# -- create the 2 tabgroup records
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";
					print TABGROUPTABLECOPYFILE $tabgroupid++."\t"."0"."\n";
					
				# -- create the single streamview_streamscope record
					print STREAMVIEWSTREAMSCOPETABLECOPYFILE $streamviewid."\t".$streamscopeid."\n";

				# -- create the single streamview record
					print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\t".$accountid."\t"."\\N"."\n";

				#-- create the 4 person_streamview records
					print PERSONSTREAMVIEWTABLECOPYFILE $personid."\t"."1"."\t"."0"."\n";
					print PERSONSTREAMVIEWTABLECOPYFILE $personid."\t"."2"."\t"."1"."\n";
					print PERSONSTREAMVIEWTABLECOPYFILE $personid."\t"."3"."\t"."2"."\n";
					print PERSONSTREAMVIEWTABLECOPYFILE $personid."\t"."4"."\t"."3"."\n";
					
		        # -- -------------------------------
                # --
                # -- Tabs for a Person (3)
                # -- Gagets for each tab (3)
                # --
                # -- -------------------------------			
                for ($u = 1; $u < 3; $u++) {
					print TABTEMPLATETABLECOPYFILE $tabtemplateid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."THREECOLUMN"."\t"."Tab ".$u."\t"."\\N"."\n";
					print TABTABLECOPYFILE $tabid++."\t"."0"."\t"."\\N"."\t"."f"."\t".$u."\t".($tabgroupid-1)."\t".($tabtemplateid-1)."\n";	
					
					for ($r = 1; $r < 3; $r++) {
					    # a random gadget from id 22 to 25
						my $existinggadgetid = (ceil(rand(4)))+21;
						print GADGETTABLECOPYFILE $gadgetid++."\t"."0"."\t"."\\N"."\t"."f"."\t"."f"."\t".($r-1)."\t"."0"."\t".$existinggadgetid."\t"."\\N"."\t".($tabtemplateid-1)."\t"."\\N"."\n";
					}
				}			
				
		        # -- -------------------------------
                # --
                # -- Custom Lists for a Person (3) with (5) people per list
                # --
                # -- -------------------------------			
                for ($u = 1; $u < 3; $u++) {
						print STREAMVIEWTABLECOPYFILE $streamviewid++."\t"."0"."\t"."List Name ".$u."\t"."\\N"."\n";
						print PERSONSTREAMVIEWTABLECOPYFILE $personid."\t".($streamviewid-1)."\t".($u+3)."\n";

                    # random person's stream scope
                    my $random_streamscope = (ceil(rand($numberofpeople)));
                    if ($random_streamscope % 2) {
                        $random_streamscope--;
                    }

                    $random_streamscope = $random_streamscope + 14;
                    
					for ($r= 1; $r < 6; $r++) {					    
					    print STREAMVIEWSTREAMSCOPETABLECOPYFILE ($streamviewid-1)."\t".$random_streamscope++."\n";						
					}
				}				
				
		        # -- -------------------------------
                # --
				# -- Saved Search for a Person (4 random)
				# --  (1 fixed on everyone)
				# --  (1 fixed on following)
                # --
                # -- -------------------------------			
                for ($u = 0; $u < 5; $u++) {
                	print STREAMSEARCHKEYWORDSTABLECOPYFILE $streamsearchid."\t"."testing"."\n";				

                    print PERSONSTREAMSEARCHTABLECOPYFILE $personid."\t".$streamsearchid."\t".$u."\n";

				    my $random_streamviewid = ceil(rand($streamviewid));
                	print STREAMSEARCHTABLECOPYFILE $streamsearchid++."\t"."0"."\t"."Search stream view ".$random_streamviewid."\t".$random_streamviewid."\n";

				}			
                    print STREAMSEARCHKEYWORDSTABLECOPYFILE $streamsearchid."\t"."testing"."\n";                
                    print PERSONSTREAMSEARCHTABLECOPYFILE $personid."\t".$streamsearchid."\t5\n";
                    print STREAMSEARCHTABLECOPYFILE $streamsearchid++."\t"."0"."\t"."Search stream view everyone"."\t3\n";
                    
                    print STREAMSEARCHKEYWORDSTABLECOPYFILE $streamsearchid."\t"."testing"."\n";                
                    print PERSONSTREAMSEARCHTABLECOPYFILE $personid."\t".$streamsearchid."\t6\n";
                    print STREAMSEARCHTABLECOPYFILE $streamsearchid++."\t"."0"."\t"."Search stream view following"."\t1\n";                    
				    	
								
						
		        # -- -------------------------------
                # --
                # -- People following other people
                # --
                # -- -------------------------------
					
				# -- get a starting position to select people ids assuming a span of 16 times the followers and 
				# shuffle that list to pull the followers from.
				my $start = ((floor(rand(($numberofpeople-($followers*16)))))+1);

				if (($start<0)||($start>$numberofpeople)) {
				    $start = 1;
				}
				my @temppeople_ids = ($start..($start+($followers-1)));
				my @followers_ids = (shuffle(@temppeople_ids))[0..$followers-1];
				for ($j = 0; $j < $followers; $j++) {
					print FOLLOWERTABLECOPYFILE @followers_ids[$j]."\t".$i."\n";
					@person_following[@followers_ids[$j]] = @person_following[@followers_ids[$j]] + 1;
				}	
				
				# -- -------------------------------
                # --
                # -- Create activity for people
                # --
                # -- -------------------------------
#				for ($j = 0; $j < @personactivitiescount[$i]; $j++) {
                for ($j = 0; $j < (ceil(@personactivitiescount[$i]*.25)); $j++) {
	                my $raw_uuid = $ug->create();                                   
                    my $uuid = $ug->to_string($raw_uuid);
                    
                    my $activitysentence = @activitiessentences[(floor(rand($activitiessentencessize)))];
                    # -- remove the 10 zeros and the x to add the space and person table id plue the x
                    my $rawsentence = substr $activitysentence,0,(length($activitysentence)-12);
                    
                    my $persontableid = $personid;
                    
                    # -- pad the string that is the person table id with leading zeros until 9 characters
                    while (length($persontableid) < 9) {
                        $persontableid = "0".$persontableid;
                    }
                    # -- complete the string the the person table id and a trailing 'x'
                    $finalactivitysentence = $rawsentence." ".$persontableid."x";
                    
                    # -- get a random month from 1 to 12
                    my $month = (ceil(rand(12)));
                    # -- get a random day from 1 to 28 (considering the chance of Feb
                    my $day = (ceil(rand(28)));
                    # -- get a random year from 2007 to 2009
                    my $year = (ceil(rand(3))+2004);
                    
				    print PERSONACTIVITYTABLECOPYFILE $activityid++."\t"."0"."\t".$accountid."\t"."PERSON"."\t"."\\N"."\t"."\\N"."\t"."\\\\254\\\\355\\\\000\\\\005sr\\\\000\\\\021java.util.HashMap\\\\005\\\\007\\\\332\\\\301\\\\303\\\\026`\\\\321\\\\003\\\\000\\\\002F\\\\000\\\\012loadFactorI\\\\000\\\\011thresholdxp?@\\\\000\\\\000\\\\000\\\\000\\\\000\\\\014w\\\\010\\\\000\\\\000\\\\000\\\\020\\\\000\\\\000\\\\000\\\\001t\\\\000\\\\007contentt\\\\000".$finalactivitysentence."\t"."NOTE"."\t"."\\N"."\t"."\\N"."\t".$uuid."\t"."\\N"."\t"."\\N"."\t".$year."-".$month."-".$day." 13:57:28.265"."\t".$year."-".$month."-".$day." 13:57:28.265"."\t"."POST"."\t".$parentorgid."\t".$streamscopeid."\t"."\\N"."\t"."t"."\n";
				}
				
				# -- -------------------------------
                # --
                # -- Create the default streamscopes for a Person
                # --
                # -- -------------------------------
			
                print STREAMSCOPETABLECOPYFILE $streamscopeid++."\t"."0"."\t"."PERSON"."\t".$accountid."\n";
				
				print "person: $i\n";	
		}
		
		# -- -------------------------------
		# --
		# -- People following a group
		# --
		# -- -------------------------------
		
		# -- Given all of the people and their following counts, have them
		# follow random groups for 33% more of their current following count
        for ($i = 1; $i <= $numberofpeople; $i++) {
            $following = @person_following[$i];
            if ($following == "") {
                $following = 0;
            }
            $numberofgroupstofollow = ceil($following*.333);
            print "number of group followers |".$numberofgroupstofollow."|\n";
            # find the starting group id
            my $startinggroupid = ((floor(rand(($numberofdomaingroups-($numberofgroupstofollow+1)))))+1);
            for ($h = 0; $h < $numberofgroupstofollow; $h++) {
                print GROUPFOLLOWERTABLECOPYFILE $i."\t".$startinggroupid++."\n";
                @person_following[$i] = @person_following[$i] + 1;
            }
        }      
        

#-------------------------------------------------------------------------------
  		# -- write copy closing record
  			print PERSONTABLECOPYFILE "\\.\n";
  			print ORGANIZATIONTABLECOPYFILE "\\.\n";
  			print DOMAINGROUPTABLECOPYFILE "\\.\n";
  			print FOLLOWERTABLECOPYFILE "\\.\n";
  			print GROUPFOLLOWERTABLECOPYFILE "\\.\n";
			print GADGETTABLECOPYFILE "\\.\n";
			print STREAMSCOPETABLECOPYFILE "\\.\n";
			print STREAMVIEWTABLECOPYFILE "\\.\n";
			print TABTABLECOPYFILE "\\.\n";
			print TABGROUPTABLECOPYFILE "\\.\n";
			print TABTEMPLATETABLECOPYFILE "\\.\n";
			print STREAMVIEWSTREAMSCOPETABLECOPYFILE "\\.\n";
			print PERSONSTREAMVIEWTABLECOPYFILE "\\.\n";
			print PERSONSTREAMSEARCHTABLECOPYFILE "\\.\n";
			print PERSONACTIVITYTABLECOPYFILE "\\.\n";
			print STREAMSEARCHTABLECOPYFILE "\\.\n";
			print STREAMSEARCHKEYWORDSTABLECOPYFILE "\\.\n";
		# ----------------------------------------------------------------------
	                                    
		# -- set new sequence numbers for tables that have them
			print PERSONTABLECOPYFILE         "SELECT pg_catalog.setval('person_id_seq', ".$numberofpeople.", true);\n";
			print ORGANIZATIONTABLECOPYFILE   "SELECT pg_catalog.setval('organization_id_seq', 9, true);\n";		
			print DOMAINGROUPTABLECOPYFILE    "SELECT pg_catalog.setval('domaingroup_id_seq', $numberofdomaingroups, true);\n";			
			print GADGETTABLECOPYFILE         "SELECT pg_catalog.setval('gadget_id_seq', ".$gadgetid.", true);\n";        
			print STREAMSCOPETABLECOPYFILE    "SELECT pg_catalog.setval('streamscope_id_seq', ".$streamscopeid.", true);\n";
			print STREAMVIEWTABLECOPYFILE     "SELECT pg_catalog.setval('streamview_id_seq', ".$streamviewid.", true);\n";
			print TABTABLECOPYFILE            "SELECT pg_catalog.setval('tab_id_seq', ".$tabid.", true);\n";
			print TABGROUPTABLECOPYFILE       "SELECT pg_catalog.setval('tabgroup_id_seq', ".$tabgroupid.", true);\n";					
			print TABTEMPLATETABLECOPYFILE    "SELECT pg_catalog.setval('tabtemplate_id_seq', ".$tabtemplateid.", true);\n";										
            print PERSONACTIVITYTABLECOPYFILE "SELECT pg_catalog.setval('activity_id_seq', ".$activityid.", true);\n";  
            print STREAMSEARCHTABLECOPYFILE   "SELECT pg_catalog.setval('streamsearch_id_seq', ".$streamsearchid.", true);\n";  
        # ----------------------------------------------------------------------
                                        													
		# -- close the files
			close (PERSONTABLECOPYFILE);
			close (ORGANIZATIONTABLECOPYFILE);
			close (DOMAINGROUPTABLECOPYFILE);
			close (FOLLOWERTABLECOPYFILE);
			close (GROUPFOLLOWERTABLECOPYFILE);
			close (GADGETTABLECOPYFILE);
			close (STREAMSCOPETABLECOPYFILE);
			close (STREAMVIEWTABLECOPYFILE);
			close (TABTABLECOPYFILE);
			close (TABGROUPTABLECOPYFILE);
			close (TABTEMPLATETABLECOPYFILE);
			close (STREAMVIEWSTREAMSCOPETABLECOPYFILE);
			close (PERSONSTREAMVIEWTABLECOPYFILE);
			close (PERSONSTERAMSEARCHTABLECOPYFILE);
			close (PERSONACTIVITYTABLECOPYFILE);
			close (STREAMSEARCHTABLECOPYFILE);
			close (PERSONSTREAMSEARCHTABLECOPYFILE);
			close (STREAMSEARCHKEYWORDSTABLECOPYFILE);
        # ----------------------------------------------------------------------			
			
#-------------------------------------------------------------------------------
# -- end.
