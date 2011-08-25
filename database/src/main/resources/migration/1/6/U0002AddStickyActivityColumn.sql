insert into db_version (major, minor, patch, scriptname, description) values (1, 6, '0002', 'U0002AddStickyActivityColumn', 'Add column for sticky activity');

ALTER TABLE domaingroup ADD COLUMN stickyActivityId int8;
