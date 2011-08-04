insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0052', 
'U0052DropThemeCssFileColumn', 'Drop cssfile col in theme table');

ALTER TABLE theme DROP COLUMN cssfile;