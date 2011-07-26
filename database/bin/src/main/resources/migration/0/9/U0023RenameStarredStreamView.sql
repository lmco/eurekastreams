--
-- Set database version to 0.9.0023
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0023', 'U0023RenameStarredStreamView.sql', 'Rename the starred streamView to use the term saved');

update streamview set name='My saved items' where type='STARRED';
