insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0003','U0003FeedSubscriberTable.sql','Creates the feedsubscriber table');
    
CREATE TABLE feedsubscriber
(
  id bigserial NOT NULL,
  version bigint NOT NULL,
  confsettings bytea,
  entityid bigint NOT NULL,
  type character varying(255) NOT NULL,
  feedid bigint NOT NULL,
  CONSTRAINT feedsubscriber_pkey PRIMARY KEY (id)
);


ALTER TABLE public.feedsubscriber OWNER TO eurekastreams;

ALTER TABLE ONLY feedsubscriber
    ADD CONSTRAINT fk49f6ec4867b493921 FOREIGN KEY (feedid) REFERENCES feed(id);