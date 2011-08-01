insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0040', 'U0040DropHelpColumns', 'Drop Help columns from system settings.');

alter table systemsettings drop column supportemailaddress;
alter table systemsettings drop column supportphonenumber;

