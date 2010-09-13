--
-- Set database version to 1.0.0005
--

<<<<<<< HEAD
insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '000x5', 'U0004AddStreamTable.sql', 'Add stream table.');

    create table Stream (
        id  bigserial not null,
        version int8 not null,
        name varchar(255) not null,
        readOnly bool not null,
        request text not null,
        primary key (id)
    );
=======
insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0005', 'U0005AddStreamTable.sql', 'Add Stream table.');


create table Stream (
    id  bigserial not null,
    version int8 not null,
    name varchar(255) not null,
    readOnly bool not null,
    request varchar(255) not null,
    primary key (id)
);


create table Person_Stream (
    Person_id int8 not null,
    streams_id int8 not null,
    streamIndex int4 not null,
    primary key (Person_id, streamIndex)
);


alter table Person_Stream
    add constraint FKBEF553AA3C9AB417
    foreign key (Person_id)
    references Person;
>>>>>>> 21e03a7e77f5aee1feba813a18bada5826598348

