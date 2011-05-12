insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0009', 
'U0009RemoveOrgCoordsAndLeaders', 'Remove org coordinators and leaders');

DROP TABLE Organization_Coordinators;
DROP TABLE Organization_Leaders;
