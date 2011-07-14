insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0017',
'U0017RemoveOrgEntityType', 'Remove EntityType.ORGANIZATION from data');

UPDATE applicationalertnotification SET destinationtype='NOTSET' WHERE destinationtype = 'ORGANIZATION';

