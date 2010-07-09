--
-- Set database version to 0.9.0017
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0017', 'U0017AddGadgetMaximizedColumn.sql', 'Adds the maximized bool for gadgets.');

ALTER TABLE gadget ADD COLUMN maximized boolean DEFAULT false;
