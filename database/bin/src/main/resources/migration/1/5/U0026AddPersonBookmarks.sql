insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0026', 
'U0026AddPersonBookmarks.sql', 'Adds the person bookmarks table');

create table Person_Bookmark (
    personId int8 not null,
    scopeId int8 not null,
    primary key (personId, scopeId)
);
