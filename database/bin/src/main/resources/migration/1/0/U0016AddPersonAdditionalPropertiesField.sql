insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0016', 'U0016AddPersonAdditionalPropertiesField.sql', 'Adds column to the person table.');

-- This script appears in both 0.9 and 1.0 due to a downward merge.

-- Add the new column only if it does not already exist.
CREATE FUNCTION addAdditionalProperties() RETURNS VOID AS
$$
BEGIN
  IF NOT EXISTS
    (SELECT attname FROM pg_attribute WHERE attrelid = (SELECT oid FROM pg_class WHERE relname = 'person') AND attname = 'additionalproperties')
  THEN
    ALTER TABLE Person ADD COLUMN additionalProperties bytea;
  END IF;
END;
$$
LANGUAGE 'plpgsql';

SELECT * FROM addAdditionalProperties();
DROP FUNCTION addAdditionalProperties();
