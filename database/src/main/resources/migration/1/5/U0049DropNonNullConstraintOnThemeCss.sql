insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0049', 
'U0049DropNonNullConstraintOnThemeCss', 'Drop nonNull constraint from cssfile col in theme table');

ALTER TABLE theme ALTER COLUMN cssfile DROP NOT NULL; 