--
-- Set database version to 1.0.0004
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0004', 'U0004AddStreamHashTagTable.sql', 'Add streamhashtag table.');

    create table StreamHashTag (
        id  bigserial not null,
        version int8 not null,
        activityDate timestamp,
        streamEntityUniqueKey varchar(255),
        streamScopeType varchar(255),
        activityId int8,
        hashTagId int8,
        primary key (id),
        unique (streamEntityUniqueKey, activityId, hashTagId)
    );
	
    alter table StreamHashTag
        add constraint FK75654BECA9C5A662
        foreign key (hashTagId)
        references HashTag;

    alter table StreamHashTag
        add constraint FK75654BEC3E94A88A
        foreign key (activityId)
        references Activity;

