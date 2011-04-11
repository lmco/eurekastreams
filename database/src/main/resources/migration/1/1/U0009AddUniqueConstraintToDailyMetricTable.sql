insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0009', 
    'U0009AddUniqueConstraintToDailyMetricTable', 'add unique constraint to DailyUsageMetric.usageDate');

ALTER TABLE DailyUsageSummary ADD CONSTRAINT daily_usage_summary_usage_date_uconstraint UNIQUE (usageDate);
