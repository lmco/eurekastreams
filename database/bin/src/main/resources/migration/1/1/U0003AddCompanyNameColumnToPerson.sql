insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0003', 'U0001AddCompanyNameToPerson', 'add CompanyName column to person');

ALTER TABLE Person
    ADD COLUMN companyName character varying(50);
