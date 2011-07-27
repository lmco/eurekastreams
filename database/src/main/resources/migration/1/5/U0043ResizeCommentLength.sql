insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0043', 'U0043ResizeCommentLength', 'Resize body column in comment table.');

ALTER TABLE comment ALTER COLUMN body TYPE varchar(1000);
