--
-- Set database version to 1.0.000
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0006', 'U0006MigrateStreamViewsToStreams.sql', 'Migrate from StreamView to Stream');

-- add the missing constraint
alter table Person_Stream
	add constraint FKBEF553AAB077E4B8
	foreign key (streams_id)
	references Stream;


-- ************************** Custom (non-search) Streams **************************
-- Create a temp table
CREATE TABLE TempStreamViewMigrate
(
  id SERIAL,
  personId bigserial NOT NULL,
  streamviewid bigserial NOT NULL,
  request character varying(8000) NOT NULL,
  "name" character varying(255) NOT NULL,
  streamId bigserial,
  streamViewIndex bigserial
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
			p.id as PersonId, 
			sv.name as StreamViewName, 
			sv.id as StreamViewId, 
			sc.scopetype, 
			sc.uniquekey, 
			psv.streamviewindex, 
			(sv.type is not null) as readOnly
		from Person p
			inner join person_streamview psv
				on p.id = psv.person_id
			inner join streamview sv 
				on psv.streamviews_id = sv.id
			inner join streamview_streamscope svsc
				on svsc.streamview_id = sv.id
			inner join streamscope sc 
				on svsc.includedscopes_id = sc.id
	-- //QUERY
     LOOP
	
		select count(1) into cnt from TempStreamViewMigrate where streamviewid = rec.StreamViewId AND personId = rec.PersonId;
		if cnt = 0 then
			
			-- insert a stream record
			insert into Stream ("name", request, version, readonly) values (rec.StreamViewName, '', 0, rec.readOnly); 
		
			insert into TempStreamViewMigrate (personId, streamviewid, "name", streamid, request, streamViewIndex) values(
				rec.PersonId,
				rec.StreamViewId,
				rec.StreamViewName,
				currval('stream_id_seq'),
				'{query:{recipient:[{type:"' || rec.scopetype || '",name:"' || rec.uniquekey || '"}]}}',
				rec.streamviewindex
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

	-- tie the streams to people
	INSERT INTO person_stream (person_id, streams_id, streamindex) 
		SELECT personId, streamid, streamViewIndex FROM TempStreamViewMigrate GROUP BY personId, streamId, streamViewIndex;
	

	DELETE FROM TempStreamViewMigrate;

    RETURN 0;
    
END;
$$ LANGUAGE plpgsql;


-- Create the streams
select createStreamsForStreamViews();


-- ************************** END OF Custom (non-search) Streams **************************


-- ************************** Custom Searches **************************




-- ************************** END OF Custom Searches **************************





-- drop the temp table
DROP TABLE TempStreamViewMigrate;