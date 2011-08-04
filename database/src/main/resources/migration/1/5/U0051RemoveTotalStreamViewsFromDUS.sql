insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0051', 
'U0051RemoveTotalStreamViewsFromDUS', 'Drop totalStreamViewCount from DailyUsageSummary');

    ALTER TABLE DailyUsageSummary DROP COLUMN totalStreamViewCount;

