--
-- Set database version to 0.9.0005
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0005', 'U0005TutorialVideoCreation', 'Adds tutorialVideo table and a comma delimited string field to person for videoIds they opted out of.');

ALTER TABLE person ADD COLUMN optOutVideoIds character varying(10000);

create table TutorialVideo (
    id  bigserial not null,
    version int8 not null,
    dialogTitle varchar(255),
    innerContent varchar(1000),
    innerContentTitle varchar(255),
    page varchar(255) not null,
    videoHeight int4,
    videoUrl varchar(1000),
    videoWidth int4,
    primary key (id)
);
