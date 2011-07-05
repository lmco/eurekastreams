insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0041', 'U0041MigrateBuiltinStreams', 'Remove Everyone, Following, add Liked By.');

delete from person_stream where streamid <= 4;
delete from stream where id <= 4;
