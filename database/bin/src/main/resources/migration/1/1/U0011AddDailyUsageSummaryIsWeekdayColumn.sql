insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0011', 
'U0011AddDailyUsageSummaryIsWeekdayColumn', 'Add isWeekday col to DailyUsageSummary');

DELETE FROM DailyUsageSummary;

ALTER TABLE DailyUsageSummary ADD COLUMN isWeekday bool not null;
