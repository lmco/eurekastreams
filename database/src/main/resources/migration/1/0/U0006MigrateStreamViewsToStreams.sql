--
-- Set database version to 1.0.000
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0006', 'U0006MigrateStreamViewsToStreams.sql', 'Migrate from StreamView to Stream');


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
BEGIN
     FOR rec IN 

	-- QUERY//
		select 
			p.id as PersonId, sv.name as StreamViewName, sv.id as StreamViewId, sc.scopetype, sc.uniquekey
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
			sc.scopetype NOT IN ('PERSONS_FOLLOWED_STREAMS', 'PERSONS_PARENT_ORGANIZATION', 'ALL', 'STARRED')
		order by 
			p.id, sv.id, sc.scopetype, sc.uniquekey
	-- //QUERY
     LOOP
	
		select count(1) into cnt from TempStreamViewMigrate where streamviewid=rec.StreamViewId;
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

    RETURN 0;
    
END;
$$ LANGUAGE plpgsql;

-- Tie the streams to people in a loop to be able to set the indexes
CREATE or replace FUNCTION createPersonStreamsForStreamViews() RETURNS integer AS $$
DECLARE
    rec RECORD ;
    cnt integer ;
BEGIN
	FOR rec IN 
		SELECT * FROM TempStreamViewMigrate ORDER BY id
	LOOP
		select count(*) into cnt from TempStreamViewMigrate where PersonId = rec.PersonId AND id < rec.id;

		-- tie the streams to people
		INSERT INTO person_stream (person_id, streams_id, streamindex)
			SELECT rec.personId, rec.streamid, cnt;
	END LOOP;

	RETURN 0;
END;
$$ LANGUAGE plpgsql;


-- Create the streams
select createStreamsForStreamViews();

-- Tie the streams to people
select createPersonStreamsForStreamViews();

-- ************************** END OF Custom (non-search) Streams **************************


-- ************************** Custom Searches **************************




-- ************************** END OF Custom Searches **************************





-- drop the temp table
DROP TABLE TempStreamViewMigrate;