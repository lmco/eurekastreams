insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0021', 'U0021FixSavedStreamApps.sql', 'Fix the saved stream apps.');

Update gadget set gadgetuserpref = '{"gadgetTitle" : "Following", "streamQuery":"saved/1", "streamLocation":"activity?streamId=1"}' where gadgetuserpref ~* '"filterId":"1"';
Update gadget set gadgetuserpref = '{"gadgetTitle" : "Parent Org", "streamQuery":"saved/2", "streamLocation":"activity?streamId=2"}' where gadgetuserpref ~* '"filterId":"2"';
Update gadget set gadgetuserpref = '{"gadgetTitle" : "Everyone", "streamQuery":"saved/3", "streamLocation":"activity?streamId=3"}' where gadgetuserpref ~* '"filterId":"3"';
Update gadget set gadgetuserpref = '{"gadgetTitle" : "Saved", "streamQuery":"saved/4", "streamLocation":"activity?streamId=4"}' where gadgetuserpref ~* '"filterId":"4"';
