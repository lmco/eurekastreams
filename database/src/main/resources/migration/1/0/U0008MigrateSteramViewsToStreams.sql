--
-- Set database version to 1.0.0008
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0008', 'U0008MigrateSteramViewsToStreams.sql', 'Migrate from StreamView to Stream');

-- Recreate the tables
DROP TABLE Stream CASCADE;
DROP TABLE Person_Stream;

create table Stream (
	id  bigserial not null,
	streamSearchId integer,
	streamViewId integer,
	version int8 not null,
	name varchar(255) not null,
	readOnly bool not null,
	request text not null,
	primary key (id)
);


create table Person_Stream (
	personId int8 not null,
	streamId int8 not null,
	streamIndex int4 not null,
	primary key (personId, streamIndex),
	unique (personId, streamId)
);

alter table Person_Stream
	add constraint FKBEF553AA242A4102
	foreign key (personId)
	references Person;

alter table Person_Stream
	add constraint FKBEF553AA6260196C
	foreign key (streamId)
	references Stream;


-- 
-- Function to migrate stream views
CREATE or replace FUNCTION migrateStreamViews() RETURNS integer AS $$
DECLARE
	rec RECORD ;
	cnt integer ;
	followingId integer ;
	parentOrgId integer ;
	everyoneId integer ;
	savedItemsId integer ;
	personMaxStreamIndex integer ;
BEGIN
	
	CREATE TABLE TempStreamViewMigrate
	(
		id SERIAL,
		personId bigserial NOT NULL,
		streamviewid bigserial NOT NULL,
		request character varying(8000) NOT NULL,
		"name" character varying(255) NOT NULL,
		streamId bigserial
	);
	
	-- insert shared views
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'Following', true, '{"query":{"followedBy":"%%CURRENT_USER_ACCOUNT_ID%%"}}');
	followingId = currval('stream_id_seq');
		
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'EUREKA:PARENT_ORG_TAG', true, '{"query":{"parentOrg":"%%CURRENT_USER_ACCOUNT_ID%%"}}');
	parentOrgId = currval('stream_id_seq');
		
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'Everyone', true, '{"query":{}}');
	everyoneId = currval('stream_id_seq');
		
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'My saved items', true, '{"query":{"savedBy":"%%CURRENT_USER_ACCOUNT_ID%%"}}');
	savedItemsId = currval('stream_id_seq');
	
	-- Add all 
	FOR rec IN 

		-- QUERY//
		select 
			p.id as PersonId, 
			sv.name as StreamViewName, 
			sv.id as StreamViewId,
			sc.scopetype, 
			sc.uniquekey
		from Person p
			inner join person_streamview psv
				on p.id = psv.person_id
			inner join streamview sv 
				on psv.streamviews_id = sv.id
			inner join streamview_streamscope svsc
				on svsc.streamview_id = sv.id
			inner join streamscope sc 
				on svsc.includedscopes_id = sc.id
		where
			-- omits the standard 4 streams
			sv.type is null
		ORDER BY
			sc.scopetype, 
			sc.uniquekey
		-- //QUERY
	LOOP
	
		-- this is a custom view the person created
		select count(1) into cnt from TempStreamViewMigrate where streamviewid = rec.StreamViewId AND personId = rec.PersonId;
		if cnt = 0 then
			
			-- insert a stream record
			insert into Stream ("name", streamViewId, request, version, readonly) values (rec.StreamViewName, rec.streamviewid, '', 0, false); 
	
			insert into TempStreamViewMigrate (personId, streamviewid, "name", streamid, request) values(
				rec.PersonId,
				rec.StreamViewId,
				rec.StreamViewName,
				currval('stream_id_seq'),
				'{"query":{"recipient":[{"type":"' || rec.scopetype || '","name":"' || rec.uniquekey || '"}]}}'
			);
			
		else
			update 
				TempStreamViewMigrate 
			SET 
				request = replace(request, ']}}', ',{"type":"' || rec.scopetype || '","name":"' || rec.uniquekey || '"}]}}')
			WHERE
				PersonId = rec.PersonId
				AND StreamViewId = rec.StreamViewId;
		end if;
			
	END LOOP;

	-- update the streams with the requests
	UPDATE Stream 
		SET request = TempStreamViewMigrate.request 
	FROM 
		TempStreamViewMigrate 
	WHERE Stream.id = TempStreamViewMigrate.StreamId;


	-- everyone gets the default 4
	INSERT INTO person_stream (personId, streamId, streamindex) 
		SELECT id, followingId, 0 FROM Person;

	INSERT INTO person_stream (personId, streamId, streamindex) 
		SELECT id, parentOrgId, 1 FROM Person;

	INSERT INTO person_stream (personId, streamId, streamindex)
		SELECT id, everyoneId, 2 FROM Person;

	INSERT INTO person_stream (personId, streamId, streamindex)
		SELECT id, savedItemsId, 3 FROM Person;
		
	
	-- Tie the user's custom-created streams to their owners
	FOR rec IN 
		-- QUERY//
		SELECT * FROM TempStreamViewMigrate
		-- //QUERY
     LOOP
		
		SELECT MAX(streamindex) + 1 INTO personMaxStreamIndex FROM Person_Stream WHERE personId = rec.personid;
		
		INSERT INTO person_stream (personId, streamId, streamindex) 
			VALUES(rec.personid, rec.streamid, personMaxStreamIndex);
	
    END LOOP;
	
	DROP TABLE TempStreamViewMigrate;
	
    RETURN 0;
    
END;
$$ LANGUAGE plpgsql;



-- 
-- Function to migrate stream searches
CREATE or replace FUNCTION migrateStreamSearches() RETURNS integer AS $$
DECLARE
	rec RECORD ;
	cnt integer ;
	personMaxStreamIndex integer ;
BEGIN
	
	CREATE TABLE TempStreamSearchMigrate
	(
		id SERIAL,
		personId bigserial NOT NULL,
		streamsearchid bigserial NOT NULL,
		request character varying(8000) NOT NULL,
		"name" character varying(255) NOT NULL,
		streamId bigserial
	);

    FOR rec IN
        select 
			p.id as PersonId, 
			ss.name as StreamSearchName, 
			ss.id as StreamSearchId,
            ss.streamview_id as StreamViewId,
            (select array_to_string(array_agg(k.element), ' OR ') 
            from streamsearch_keywords k  WHERE k.streamsearch_id = ss.id  GROUP BY ss.id) 
            AS keywords 
		from Person p
			inner join person_streamsearch pss
				on p.id = pss.person_id
			inner join streamsearch ss
				on ss.id = pss.streamsearches_id
        WHERE ss.streamview_id <= 4
    LOOP
        if rec.StreamViewId = 1 then
    	    insert into Stream ("name", streamSearchId, request, version, readonly) values (rec.StreamSearchName, rec.streamsearchid, '{"query":{"followedBy":"%%CURRENT_USER_ACCOUNT_ID%%","keywords":"' || rec.keywords || '"}}', 0, false); 
        end if;
        if rec.StreamViewId = 2 then
    	    insert into Stream ("name", streamSearchId, request, version, readonly) values (rec.StreamSearchName, rec.streamsearchid, '{"query":{"parentOrg":"%%CURRENT_USER_ACCOUNT_ID%%","keywords":"' || rec.keywords || '"}}', 0, false); 
        end if;
        if rec.StreamViewId = 3 then
    	    insert into Stream ("name", streamSearchId, request, version, readonly) values (rec.StreamSearchName, rec.streamsearchid, '{"query":{"keywords":"' || rec.keywords || '"}}', 0, false); 
        end if;
        if rec.StreamViewId = 4 then
    	    insert into Stream ("name", streamSearchId, request, version, readonly) values (rec.StreamSearchName, rec.streamsearchid, '{"query":{"savedBy":"%%CURRENT_USER_ACCOUNT_ID%%","keywords":"' || rec.keywords || '"}}', 0, false); 
        end if;

		SELECT MAX(streamindex) + 1 INTO personMaxStreamIndex FROM Person_Stream WHERE personId = rec.PersonId;
		
		INSERT INTO person_stream (personId, streamId, streamindex) 
			VALUES(rec.PersonId, currval('stream_id_seq'), personMaxStreamIndex);
	       
	END LOOP;

	-- Add all stream searches without keywords
	FOR rec IN 

		-- QUERY//
		select 
			p.id as PersonId, 
			ss.name as StreamSearchName, 
			ss.id as StreamSearchId,
			sc.scopetype, 
			sc.uniquekey
		from Person p
			inner join person_streamsearch pss
				on p.id = pss.person_id
			inner join streamsearch ss
				on ss.id = pss.streamsearches_id
			inner join streamview sv 
				on ss.streamview_id = sv.id
			inner join streamview_streamscope svsc
				on svsc.streamview_id = sv.id
			inner join streamscope sc 
				on svsc.includedscopes_id = sc.id
		ORDER BY 
			sc.scopetype, 
			sc.uniquekey
		-- //QUERY
	LOOP

		select count(1) into cnt from TempStreamSearchMigrate where streamsearchid = rec.StreamSearchId AND personId = rec.PersonId;
		if cnt = 0 then
			
			-- insert a stream record
			insert into Stream ("name", streamSearchId, request, version, readonly) values (rec.StreamSearchName, rec.streamsearchid, '', 0, false); 
	
			insert into TempStreamSearchMigrate (personId, streamsearchid, "name", streamid, request) values(
				rec.PersonId,
				rec.StreamSearchId,
				rec.StreamSearchName,
				currval('stream_id_seq'),
				'{"query":{"recipient":[{"type":"' || rec.scopetype || '","name":"' || rec.uniquekey || '"}]}}'
			);
			
		else
			update 
				TempStreamSearchMigrate 
			SET 
				request = replace(request, ']}}', ',{"type":"' || rec.scopetype || '","name":"' || rec.uniquekey || '"}]}}')
			WHERE
				PersonId = rec.PersonId
				AND streamsearchid = rec.StreamSearchId;
		end if;
			
	END LOOP;
	
	-- create the block for keywords in all requests
	UPDATE 
		TempStreamSearchMigrate 
	SET 
		request = replace(request, ']}}', '], "keywords":"____KEYWORDS_TEMP_BLOCK____"}}');
	
	
	-- add all keywords
	FOR rec IN
	 	-- QUERY//
		SELECT 
			kw.streamsearch_id AS StreamSearchId, 
			replace(element, '"', '') AS keyword 
		FROM 
			streamsearch_keywords kw 
			INNER JOIN TempStreamSearchMigrate tmp 
				ON tmp.streamsearchid = kw.streamsearch_id
		-- //QUERY
	LOOP
	
		update 
			TempStreamSearchMigrate 
		SET 
			request = replace(request, '____KEYWORDS_TEMP_BLOCK____', '____KEYWORDS_TEMP_BLOCK____ ' || rec.keyword)
		WHERE
			streamsearchid = rec.StreamSearchId;
	
	END LOOP;
	
	-- remove the block for keywords in all requests
	update 
		TempStreamSearchMigrate 
	SET 
		request = replace(request, '____KEYWORDS_TEMP_BLOCK____ ', '');


	-- update the streams with the requests
	UPDATE Stream 
		SET request = TempStreamSearchMigrate.request 
	FROM 
		TempStreamSearchMigrate 
	WHERE Stream.id = TempStreamSearchMigrate.StreamId;


	-- Tie the user's searches to their owners
	FOR rec IN 
		-- QUERY//
		SELECT * FROM TempStreamSearchMigrate
		-- //QUERY
     LOOP
		
		SELECT MAX(streamindex) + 1 INTO personMaxStreamIndex FROM Person_Stream WHERE personId = rec.personid;
		
		INSERT INTO person_stream (personId, streamId, streamindex) 
			VALUES(rec.personid, rec.streamid, personMaxStreamIndex);
	
    END LOOP;


	-- drop the temp table
	DROP TABLE TempStreamSearchMigrate;
	
    RETURN 0;
    
END;
$$ LANGUAGE plpgsql;

-- Create the streams
SELECT migrateStreamViews();
SELECT migrateStreamSearches();

DROP FUNCTION migrateStreamViews();
DROP FUNCTION migrateStreamSearches();

-- Migrate Persons
UPDATE gadget set gadgetuserpref = '{"gadgetTitle" : "' || substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"') || '", "streamLocation":people/' ||  substring(gadgetuserpref from '"shortName":"([a-zA-Z0-9]*)"') 
    || E'", "streamQuery":"{\\"query\\":{\\"recipient\\":[{\\"type\\":\\"PERSON\\", \\"name\\":\\"' || 
    substring(gadgetuserpref from '"shortName":"([a-zA-Z0-9]*)"') || E'\\"}]}}" }' where gadgetdefinitionid = 22 and gadgetuserpref ~* '"streamtype":"personstream"';

-- Migrate Groups
UPDATE gadget set gadgetuserpref = '{"gadgetTitle" : "' || substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"') || '", "streamLocation":"groups/' || substring(gadgetuserpref from '"shortName":"([a-zA-Z0-9]*)"')
    || E'", "streamQuery":"{\\"query\\":{\\"recipient\\":[{\\"type\\":\\"GROUP\\", \\"name\\":\\"' || 
    substring(gadgetuserpref from '"shortName":"([a-zA-Z0-9]*)"') || E'\\"}]}}" }' where gadgetdefinitionid = 22 and gadgetuserpref ~* '"streamtype":"groupstream"';

-- Migrate Orgs
UPDATE gadget set gadgetuserpref = '{"gadgetTitle" : "' || substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"') || '", "streamLocation":"organizations/' || substring(gadgetuserpref from '"shortName":"([a-zA-Z0-9]*)"')
    || E'", "streamQuery":"{\\"query\\":{\\"organization\\": \\"' || 
    substring(gadgetuserpref from '"shortName":"([a-zA-Z0-9]*)"') || E'\\"}" }' where gadgetdefinitionid = 22 and gadgetuserpref ~* '"streamtype":"orgstream"';

-- Migrate Saved Searches
UPDATE gadget g2 set gadgetuserpref = '{"gadgetTitle" : "' || substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"') || '", "streamLocation":"activity?streamId=' || 
    (SELECT s.id from stream s, gadget g where int4(substring(g.gadgetuserpref from '"filterId":"([a-zA-Z0-9]*)"')) = s.streamsearchid AND g.id = g2.id)
    || E'", "streamQuery":"saved/' || 
    (SELECT s.id from stream s, gadget g where int4(substring(g.gadgetuserpref from '"filterId":"([a-zA-Z0-9]*)"')) = s.streamsearchid AND g.id = g2.id) || '"}' 
    where g2.gadgetdefinitionid = 22 and g2.gadgetuserpref ~* '"streamtype":"streamsearch"';

-- Migrate Composite Streams
UPDATE gadget g2 set gadgetuserpref = '{"gadgetTitle" : "' || substring(gadgetuserpref from '"gadgetTitle":"([^"]*)"')  || '", "streamLocation":"activity?streamId=' ||
    (SELECT s.id from stream s, gadget g where int4(substring(g.gadgetuserpref from '"filterId":"([a-zA-Z0-9]*)"')) = s.streamviewid AND g.id = g2.id)
    || E'", "streamQuery":"saved/' || 
    (SELECT s.id from stream s, gadget g where int4(substring(g.gadgetuserpref from '"filterId":"([a-zA-Z0-9]*)"')) = s.streamviewid AND g.id = g2.id) || '"}' 
    where g2.gadgetdefinitionid = 22 and g2.gadgetuserpref ~* '"streamtype":"compositestream"' and int4(substring(g2.gadgetuserpref from '"filterId":"([a-zA-Z0-9]*)"')) > 4;

