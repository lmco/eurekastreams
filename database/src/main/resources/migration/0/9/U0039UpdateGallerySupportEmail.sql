--
-- Set database version to 0.9.0039
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0039', 'U0039UpdateGallerySupportEmail', 'Update support email in gallery.');

update theme set authoremail='support@eurekastreams.org' where authoremail='smp-support.fc-isgs@lmco.com';