insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0008', 'U0008RemoveBookmarksToDeletedGroups.sql', 'Remove bookmarks to deleted groups.');

delete from person_bookmark pb where not exists (select 1 from streamscope b where b.id = pb.scopeid);

