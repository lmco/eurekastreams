insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0010', 
'U0010UnhookOrganizations', 'Unhook organizations from different entities');

-- set the field to not null
ALTER TABLE Person ALTER COLUMN parentorganizationid DROP NOT NULL;
ALTER TABLE DomainGroup ALTER COLUMN parentorganizationid DROP NOT NULL;
ALTER TABLE Organization ALTER COLUMN parentorganizationid DROP NOT NULL;
ALTER TABLE Activity ALTER COLUMN recipientparentorgid DROP NOT NULL;

UPDATE Person SET parentorganizationid = NULL;
UPDATE DomainGroup SET parentorganizationid = NULL;
UPDATE Organization SET parentorganizationid = NULL;
UPDATE Activity SET recipientparentorgid = NULL;
