insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0004', 'U0004AddShowInStreamToActivityTable', 'add showinstream column');

ALTER TABLE activity 
    ADD COLUMN showinstream boolean NOT NULL DEFAULT true;