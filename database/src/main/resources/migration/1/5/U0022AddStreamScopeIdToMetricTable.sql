insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0022', 
'U0022AddStreamScopeIdToMetricTable', 'Add stream scope id to metric table for streams');

-- 1.1 was patched to include this column, so we need to only add it if it's not there already
CREATE OR REPLACE FUNCTION update_the_db() RETURNS INT AS
$$
BEGIN
	IF NOT EXISTS(SELECT true FROM pg_attribute WHERE attrelid = (SELECT oid FROM pg_class WHERE relname = 'usagemetric') and attname = 'streamviewstreamscopeid') THEN
		ALTER TABLE UsageMetric ADD COLUMN streamviewstreamscopeid INT NULL;
	END IF;
	RETURN 1;
END;
$$
LANGUAGE 'plpgsql';

SELECT update_the_db();
DROP FUNCTION update_the_db();



