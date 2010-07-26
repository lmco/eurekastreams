--
-- PostgreSQL 
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Create indexes that are not created from primary key designations.
--

CREATE INDEX groupfollower_followingid_idx ON groupfollower ( followingid );

CREATE INDEX follower_followingid_idx ON follower ( followingid );

CREATE INDEX streamsearch_keywords_streamsearch_id_idx ON streamsearch_keywords ( streamsearch_id );

CREATE INDEX activity_streamscopeid_idx ON activity ( streamscopeid );

CREATE INDEX streamscope_uniquekey_idx ON streamscope ( uniquekey );

CREATE INDEX person_streamview_person_id_idx ON person_streamview ( person_id );

CREATE INDEX person_streamscopeid_idx ON person ( streamscopeid );

CREATE INDEX streamview_streamscope_includedscopes_idx ON streamview_streamscope( includedscopes_id, streamview_id );

CREATE INDEX person_accountid_idx ON person ( accountid );
