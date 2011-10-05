insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0002', 
'U0002AddIndexOnPersonEmail.sql', 'Adds an index on email on the person table');

CREATE INDEX person_email_idx ON person(email);
