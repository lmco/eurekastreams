insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0044', 'U0044AllowNullEmail', 'Remove not-null constraint from person email.');

ALTER TABLE person ALTER COLUMN email DROP NOT NULL;
