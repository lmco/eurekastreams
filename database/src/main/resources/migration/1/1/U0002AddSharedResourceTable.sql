insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0002', 'U0002AddSharedResourceTable', 'add SharedResource table');

    create table SharedResource (
        id  bigserial not null,
        version int8 not null,
        resourceType varchar(255) not null,
        uniqueKey varchar(2000) not null,
        primary key (id)
    );

    create table Person_LikedSharedResources (
        personId int8 not null,
        sharedResourceId int8 not null,
        unique (personId, sharedResourceId)
    );

    alter table Person_LikedSharedResources
        add constraint FK7111CA7D9A7E3314
        foreign key (personId)
        references SharedResource;
    

    alter table Activity
        add column linkSharedResourceId int8;

        
    alter table Activity
        add constraint FKA126572F442EBFAC
        foreign key (linkSharedResourceId)
        references SharedResource;
        