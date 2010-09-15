--
-- Set database version to 1.0.000
--

-- insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0006', 'U0006MigrateStreamViewsToStreams.sql', 'Migrate from StreamView to Stream');

-- add the missing constraint
-- alter table Person_Stream
-- 	add constraint FKBEF553AAB077E4B8
-- 	foreign key (streams_id)
-- 	references Stream;


-- ************************** Custom (non-search) Streams **************************
-- Create a temp table
CREATE TABLE TempStreamViewMigrate
(
	id SERIAL,
	personId bigserial NOT NULL,
	streamviewid bigserial NOT NULL,
	request character varying(8000) NOT NULL,
	"name" character varying(255) NOT NULL,
	streamId bigserial
);


-- 
-- Function to create streams for views
CREATE or replace FUNCTION createStreamsForStreamViews() RETURNS integer AS $$
DECLARE
	rec RECORD ;
	cnt integer ;
	followingId integer ;
	parentOrgId integer ;
	everyoneId integer ;
	savedItemsId integer ;
	personMaxStreamIndex integer ;
BEGIN
	
	-- Empty out streams
	DELETE FROM person_stream;
	DELETE FROM stream;
	PERFORM setval('stream_id_seq', 1, false);
	
	-- insert shared views
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'Following', true, '{query:{followedBy:"%%CURRENT_USER_ACCOUNT_ID%%"}}');
	followingId = currval('stream_id_seq');
		
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'EUREKA:PARENT_ORG_TAG', true, '{query:{parentOrg:"%%CURRENT_USER_ACCOUNT_ID%%"}}');
	parentOrgId = currval('stream_id_seq');
		
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'Everyone', true, '{query:{}}');
	everyoneId = currval('stream_id_seq');
		
	INSERT INTO Stream (version, "name", readonly, request) 
		VALUES(0, 'My saved items', true, '{query:{savedBy:"%%CURRENT_USER_ACCOUNT_ID%%"}}');
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
		-- //QUERY
	LOOP
	
		-- this is a custom view the person created
		select count(1) into cnt from TempStreamViewMigrate where streamviewid = rec.StreamViewId AND personId = rec.PersonId;
		if cnt = 0 then
			
			-- insert a stream record
			insert into Stream ("name", request, version, readonly) values (rec.StreamViewName, '', 0, false); 
	
			insert into TempStreamViewMigrate (personId, streamviewid, "name", streamid, request) values(
				rec.PersonId,
				rec.StreamViewId,
				rec.StreamViewName,
				currval('stream_id_seq'),
				'{query:{recipient:[{type:"' || rec.scopetype || '",name:"' || rec.uniquekey || '"}]}}'
			);
			
		else
			update 
				TempStreamViewMigrate 
			SET 
				request = replace(request, ']}}', ',{type:"' || rec.scopetype || '",name:"' || rec.uniquekey || '"}]}}')
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
	INSERT INTO person_stream (person_id, streams_id, streamindex) 
		SELECT id, followingId, 0 FROM Person;

	INSERT INTO person_stream (person_id, streams_id, streamindex) 
		SELECT id, parentOrgId, 1 FROM Person;

	INSERT INTO person_stream (person_id, streams_id, streamindex) 
		SELECT id, everyoneId, 2 FROM Person;

	INSERT INTO person_stream (person_id, streams_id, streamindex) 
		SELECT id, savedItemsId, 3 FROM Person;
		
	
	-- Tie the user's custom-created streams to those people
	FOR rec IN 
		-- QUERY//
		SELECT * FROM TempStreamViewMigrate
		-- //QUERY
     LOOP
		
		SELECT MAX(streamindex) + 1 INTO personMaxStreamIndex FROM Person_Stream WHERE person_id = rec.personid;
		
		INSERT INTO person_stream (person_id, streams_id, streamindex) 
			VALUES(rec.personid, rec.streamid, personMaxStreamIndex);
	
    END LOOP;	
	
    RETURN 0;
    
END;
$$ LANGUAGE plpgsql;


-- Create the streams
select createStreamsForStreamViews();

-- drop the temp table
DROP TABLE TempStreamViewMigrate;