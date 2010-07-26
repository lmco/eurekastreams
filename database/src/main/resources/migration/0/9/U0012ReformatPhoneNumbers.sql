--
-- Set database version to 0.9.0012
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0012', 'U0012ReformatPhoneNumbers.sql', 'Formats existing phone numbers in the database since it will no longer be done in code.');

update person set workphone = substr(workphone,1,3)||'-'||substr(workphone,4,3)||'-'||substr(workphone,7,4) where length(workphone) = 10;
 
update person set cellphone = substr(cellphone,1,3)||'-'||substr(cellphone,4,3)||'-'||substr(cellphone,7,4) where length(cellphone) = 10;

update person set fax = substr(fax,1,3)||'-'||substr(fax,4,3)||'-'||substr(fax,7,4) where length(fax) = 10;
