insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0001', 
'U0001AddPersonCryptoKey.sql', 'Adds the person encryption key table');

create table Person_CryptoKey (
	id  bigserial not null,
	version int8 not null,
	cryptoKey bytea,
	personId int8 not null,
	primary key (id),
	unique (personId)
);