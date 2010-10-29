insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0016', 'U0016AddPersonAdditionalPropertiesField.sql', 'Adds column to the person table.');

ALTER TABLE Person
    ADD COLUMN additionalProperties bytea;
    
