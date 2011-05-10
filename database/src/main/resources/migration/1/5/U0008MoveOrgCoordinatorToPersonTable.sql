insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0008', 
'U0008MoveOrgCoordinatorToPersonTable', 'Move org coordinator to person table');

-- Add the column to person
ALTER TABLE Person ADD COLUMN isAdministrator bool DEFAULT NULL;

-- update it for all existing coordinators
UPDATE Person SET isAdministrator = FALSE;
UPDATE Person SET isAdministrator = TRUE WHERE id IN (SELECT coordinators_id FROM Organization_Coordinators);

-- set the field to not null
ALTER TABLE PERSON ALTER COLUMN isAdministrator SET NOT NULL;
