--
-- Set database version to 0.9.0024
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0024', 'U0024ImageTable.sql', 'Creates the image table.');

-- Add the Image table

create table Image (
    id  bigserial NOT NULL,
    version bigint NOT NULL,
    imageIdentifier varchar(255) NOT NULL,
    imageBlob bytea NOT NULL,
    primary key (id)
);

ALTER SEQUENCE Image_id_seq OWNED BY Image.id;

SELECT pg_catalog.setval('Image_id_seq', 1, true);

ALTER TABLE Image ALTER COLUMN id SET DEFAULT nextval('Image_id_seq'::regclass);

    