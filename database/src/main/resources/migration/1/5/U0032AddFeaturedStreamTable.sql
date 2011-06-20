insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0032', 
'U0032AddFeaturedStreamTable.', 'Add Featured Stream table');

    create table FeaturedStream (
        id  bigserial not null,
        version int8 not null,
        created timestamp not null,
        description varchar(250) not null,
        streamScopeId int8,
        primary key (id)
    );
    
        alter table FeaturedStream 
        add constraint FK2451916E956D52F2 
        foreign key (streamScopeId) 
        references StreamScope;