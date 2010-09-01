--
-- Set database version to 0.9.0039
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0003', 'U0003AddHashTagTable.sql', 'Add hashtags table.');

create table HashTag (
        id  bigserial not null,
        version int8 not null,
        content varchar(255),
        primary key (id),
        unique (content)
    );

create index hashtag_content_idx on HashTag (content);