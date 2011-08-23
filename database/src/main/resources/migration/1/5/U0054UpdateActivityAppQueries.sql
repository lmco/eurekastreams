insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0054', 
'U0054UpdateActivityAppQueries', 'Update activity app gadget prefs to use stream queries.');

UPDATE gadget 
SET gadgetuserpref = replace(gadgetuserpref, '"streamQuery":"saved/1"', E'"streamQuery":"{\\"query\\":{\\"followedBy\\":\\"%%CURRENT_USER_ACCOUNT_ID%%\\", \\"sortBy\\":\\"date\\"}}"')
WHERE gadgetuserpref LIKE '%"streamQuery":"saved/1"%';

UPDATE gadget 
SET gadgetuserpref = replace(gadgetuserpref, '"streamQuery":"saved/3"', E'"streamQuery":"{\\"query\\":{}}"')
WHERE gadgetuserpref LIKE '%"streamQuery":"saved/3"%';

UPDATE gadget 
SET gadgetuserpref = replace(gadgetuserpref, '"streamQuery":"saved/4"', E'"streamQuery":"{\\"query\\":{\\"savedBy\\":\\"%%CURRENT_USER_ACCOUNT_ID%%\\", \\"sortBy\\":\\"date\\"}}"')
WHERE gadgetuserpref LIKE '%"streamQuery":"saved/4"%';

