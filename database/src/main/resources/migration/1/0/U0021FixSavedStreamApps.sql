insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0021', 'U0021FixSavedStreamApps.sql', 'Fix the saved stream apps.');

Update gadget g2 set gadgetuserpref ='{"gadgetTitle" : "' || coalesce(substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"'), 'Gadget') || E'", "streamQuery":"saved/' || (select s.streamindex from gadget g, person_stream s where g.id = g2.id and  g.gadgetuserpref ~* 'saved/' AND s.streamid = cast(substring(g.gadgetuserpref from '"saved/([^"]*)"') as integer))  || '"}' WHERE g2.gadgetuserpref ~* 'saved/' AND cast(substring(g2.gadgetuserpref from '"saved/([^"]*)"') as integer) > 4;

Update gadget set gadgetuserpref = '{"gadgetTitle" : "Following", "streamQuery":"saved/1"}' where gadgetuserpref ~* '"filterId":"1"';
Update gadget set gadgetuserpref = '{"gadgetTitle" : "Parent Org", "streamQuery":"saved/2"}' where gadgetuserpref ~* '"filterId":"2"';
Update gadget set gadgetuserpref = '{"gadgetTitle" : "Everyone", "streamQuery":"saved/3"}' where gadgetuserpref ~* '"filterId":"3"';
Update gadget set gadgetuserpref = '{"gadgetTitle" : "Saved", "streamQuery":"saved/4"}' where gadgetuserpref ~* '"filterId":"4"';
