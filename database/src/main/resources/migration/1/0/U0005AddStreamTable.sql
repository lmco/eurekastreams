--
-- Set database version to 1.0.0005
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '000x5', 'U0004AddStreamTable.sql', 'Add stream table.');

    create table Stream (
        id  bigserial not null,
        version int8 not null,
        name varchar(255) not null,
        readOnly bool not null,
        request text not null,
        primary key (id)
    );

