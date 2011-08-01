insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0048', 'U0048AddSupportStreamColumn', 'Re-add systemsettings.url column');

CREATE OR REPLACE FUNCTION update_the_db() RETURNS INT AS
$$
BEGIN
	IF NOT EXISTS(SELECT true FROM pg_attribute WHERE attrelid = (SELECT oid FROM pg_class WHERE relname = 'systemsettings') and attname = 'supportstreamgroupshortname') THEN
		ALTER TABLE systemsettings ADD COLUMN supportstreamgroupshortname character varying(255);
	END IF;
	RETURN 1;
END;
$$
LANGUAGE 'plpgsql';

SELECT update_the_db();
DROP FUNCTION update_the_db();
