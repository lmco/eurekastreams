insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0047', 
'U0047AddPersonBlockedSuggestions.sql', 'Adds the person blocked suggestions table');

create table Person_BlockedSuggestion (
    personId int8 not null,
    scopeId int8 not null,
    primary key (personId, scopeId)
);
