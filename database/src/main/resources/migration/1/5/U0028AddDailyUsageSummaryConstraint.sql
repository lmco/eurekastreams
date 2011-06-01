insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0028', 
'U0028AddDailyUsageSummaryConstraint', 'Add unique constraint on DailyUsageSummary');

ALTER TABLE dailyusagesummary
  ADD CONSTRAINT dailyusagesummary_streamviewstreamscopeid_usagedate_constraint UNIQUE(streamviewstreamscopeid, usagedate);
