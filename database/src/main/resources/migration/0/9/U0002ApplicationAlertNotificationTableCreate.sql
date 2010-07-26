--
-- Set database version to 0.9.0002
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0002', 'U0002ApplicationAlertNotificationTableCreate.sql', 'Creates the table of notifications to be displayed to users within the app.');

CREATE TABLE applicationalertnotification
(
  id bigserial NOT NULL,
  "version" bigint NOT NULL,
  activityauthorname character varying(255),
  activityid bigint,
  actoraccountid character varying(255) NOT NULL,
  actorname character varying(255) NOT NULL,
  notificationtype character varying(255) NOT NULL,
  recipientid bigint NOT NULL
);

ALTER TABLE public.applicationalertnotification OWNER TO eurekastreams;

ALTER TABLE public.applicationalertnotification_id_seq OWNER TO eurekastreams;

ALTER SEQUENCE applicationalertnotification_id_seq OWNED BY applicationalertnotification.id;

SELECT pg_catalog.setval('applicationalertnotification_id_seq', 1, true);

ALTER TABLE applicationalertnotification ALTER COLUMN id SET DEFAULT nextval('applicationalertnotification_id_seq'::regclass);

ALTER TABLE ONLY applicationalertnotification
    ADD CONSTRAINT applicationalertnotification_pkey PRIMARY KEY (id);

ALTER TABLE ONLY applicationalertnotification
    ADD CONSTRAINT fk1a60d517880d76c6 FOREIGN KEY (recipientid) REFERENCES person(id);
