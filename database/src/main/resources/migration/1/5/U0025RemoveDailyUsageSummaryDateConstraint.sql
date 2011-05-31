insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0025', 
'U0025RemoveDailyUsageSummaryDateCstr"', 'Remove unique date constraint from usage summary');

ALTER TABLE DailyUsageSummary DROP CONSTRAINT daily_usage_summary_usage_date_uconstraint;
