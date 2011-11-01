insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0003', 'U0003AddNotificationAggregationCount.sql', 'updates notification table to add an aggregation count column.');

ALTER TABLE InAppNotification ADD COLUMN aggregationcount integer NOT NULL DEFAULT 1;