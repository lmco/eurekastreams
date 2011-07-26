insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0043', 'U0043ResizeCommentLength', 'Resize body column in comment table.');

UPDATE pg_attribute SET atttypmod = 1004 WHERE attrelid = 'comment'::regclass AND attname = 'body';
