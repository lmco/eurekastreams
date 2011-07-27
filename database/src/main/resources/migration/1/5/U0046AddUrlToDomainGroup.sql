insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0046', 'U0046AddUrlToDomainGroup', 'Re-add DomainGroup.url column');

-- Note: This column was accidentally removed in an earlier migration script, U0016RemoveGroupUrl.  That script was commented out, so the column will exist
-- for users that are upgrading from 1.1.  For developers, the column is probably gone, so we need to re-create the column if it doesn't exist, which should just 
-- be for developers.


CREATE OR REPLACE FUNCTION update_the_db() RETURNS INT AS
$$
BEGIN
	IF NOT EXISTS(SELECT true FROM pg_attribute WHERE attrelid = (SELECT oid FROM pg_class WHERE relname = 'domaingroup') and attname = 'url') THEN
		ALTER TABLE DomainGroup ADD COLUMN url character varying(255);
	END IF;
	RETURN 1;
END;
$$
LANGUAGE 'plpgsql';

SELECT update_the_db();
DROP FUNCTION update_the_db();
