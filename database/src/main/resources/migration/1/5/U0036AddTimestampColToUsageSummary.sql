insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0036', 
'U0036AddTimestampColToUsageSummary', 'Add timestamp on DailyUsageSummary');

ALTER TABLE DailyUsageSummary ADD COLUMN usageDateTimeStampInMs BIGINT;

UPDATE DailyUsageSummary SET usageDateTimeStampInMs = DATE_PART('epoch', usageDate) * 1000;

ALTER TABLE DailyUsageSummary ALTER COLUMN usageDateTimeStampInMs SET NOT NULL;
