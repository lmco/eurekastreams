--
-- Set database version to 0.8.0002
--

insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0002','U0002PluginDefinitionTable.sql','Creates the plugindefinition table');

--
-- Create table
--

CREATE TABLE plugindefinition
(
  id bigserial NOT NULL,
  "version" bigint NOT NULL,
  created timestamp without time zone NOT NULL,
  url character varying(255) NOT NULL,
  uuid character varying(255) NOT NULL,
  objecttype character varying(255),
  plugincategoryid bigint,
  ownerid bigint,
  showingallery boolean,
  numberofusers integer,
  updatefrequency bigint,
  CONSTRAINT plugindefinition_pkey PRIMARY KEY (id),
  CONSTRAINT plugindefinition_cat_fkey FOREIGN KEY (plugincategoryid)
      REFERENCES galleryitemcategory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT plugindefinition_owner_fkey FOREIGN KEY (ownerid)
      REFERENCES person (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT plugindefinition_uuid_key UNIQUE (uuid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE plugindefinition OWNER TO eurekastreams;

--
-- alter feed for new contraint
--

ALTER TABLE feed
    DROP CONSTRAINT fk276a314ed584123;

ALTER TABLE ONLY feed
    ADD CONSTRAINT feed_plugindef_fkey FOREIGN KEY (streampluginid)
      REFERENCES plugindefinition (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
    
--
--DROP unused table plugin
--
  
DROP Table plugin;