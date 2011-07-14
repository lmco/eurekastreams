--
-- Set database version to 0.9.0031
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0031', 'U0031AddGroupMembershipRequestTable.sql', 'Add table for group membership requests.');

CREATE TABLE GroupMembershipRequests
(
	groupId int8 not null,
	personId int8 not null,
	primary key (groupId, personId)
);

ALTER TABLE GroupMembershipRequests
	ADD CONSTRAINT FK6E2B9E79242A4102 
	FOREIGN KEY (personId) 
	REFERENCES Person ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE GroupMembershipRequests
	ADD CONSTRAINT FK6E2B9E7952F1F398 
	FOREIGN KEY (groupId) 
	REFERENCES DomainGroup ON DELETE CASCADE ON UPDATE CASCADE;
