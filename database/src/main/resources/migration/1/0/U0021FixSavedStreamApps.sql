insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0021', 'U0021FixSavedStreamApps.sql', 'Fix the saved stream apps.');

UPDATE gadget g2 set gadgetuserpref = '{"gadgetTitle" : "' || substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"') || E'", "streamQuery":"saved/' || (select s.streamindex from gadget g, person_stream s where g.id = g2.id and  g.gadgetuserpref ~* 'saved/' AND s.streamid = cast(substring(g.gadgetuserpref from '"saved/([^"]*)"') as integer) and s.streamid > 3)  || '"}' WHERE g2.gadgetuserpref ~* 'saved/' AND cast(substring(g2.gadgetuserpref from '"saved/([^"]*)"') as integer) > 3;

