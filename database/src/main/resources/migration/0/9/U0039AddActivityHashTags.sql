--
-- Set database version to 0.9.0039
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0039', 'U0039AddActivityHashTags.sql', 'Add activity hashtags.');

create table HashTag (
        id  bigserial not null,
        version int8 not null,
        content varchar(255),
        primary key (id),
        unique (content)
    );

create table Activity_HashTags (
        activityId int8 not null,
        hashTagId int8 not null,
        primary key (activityId, hashTagId),
        unique (activityId, hashTagId)
    );

alter table Activity_HashTags
    add constraint FK2A9C1577A9C5A662
    foreign key (hashTagId)
    references HashTag;


alter table Activity_HashTags
    add constraint FK2A9C15773E94A88A
    foreign key (activityId)
    references Activity;
