insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0010', 'U0010AddAvgActResponseTimeToDailyMetricTable', 'Add Avg Activity Response Time To DailyUsageSummary Table');

ALTER TABLE DailyUsageSummary
    ADD COLUMN avgActivityResponseTime bigint NOT NULL DEFAULT 0;
