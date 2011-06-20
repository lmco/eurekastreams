insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0031', 
'U0031RemoveIsWeekdayFromUsageSummary', 'Remove DailyUsageSummary.isWeekday column');

DELETE FROM DailyUsageSummary WHERE isWeekday = false;
ALTER TABLE DailyUsageSummary DROP COLUMN isWeekday;
