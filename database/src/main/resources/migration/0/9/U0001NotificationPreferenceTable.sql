--
-- Set database version to 0.9.0001
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0001', 'U0001NotificationPreferenceTable.sql', 'Creates the user notification preferences table.');

create table NotificationFilterPreference (
    id  bigserial not null,
    version bigint NOT NULL,
    notificationCategory varchar(255),
    notifierType varchar(255) not null,
    personId bigint NOT NULL,
    primary key (id)
);

ALTER SEQUENCE NotificationFilterPreference_id_seq OWNED BY NotificationFilterPreference.id;

SELECT pg_catalog.setval('NotificationFilterPreference_id_seq', 1, true);

ALTER TABLE NotificationFilterPreference ALTER COLUMN id SET DEFAULT nextval('NotificationFilterPreference_id_seq'::regclass);

ALTER TABLE ONLY NotificationFilterPreference
    ADD CONSTRAINT FK7BC6199E242A4102
    FOREIGN KEY (personId)
    REFERENCES person(id);

    