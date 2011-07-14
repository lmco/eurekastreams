--
-- The default SQL data load file.
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
SELECT pg_catalog.setval('organization_id_seq', 1, true);

SELECT pg_catalog.setval('person_id_seq', 1, true);

SELECT pg_catalog.setval('streamscope_id_seq', 6, true);

SELECT pg_catalog.setval('streamview_id_seq', 6, true);

SELECT pg_catalog.setval('tab_id_seq', 6, true);

SELECT pg_catalog.setval('tabgroup_id_seq', 6, true);

SELECT pg_catalog.setval('tabtemplate_id_seq', 5, true);


--
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization (id, version, avatarid, avatarcropsize, avatarcropx, avatarcropy, bannerbackgroundcolor, bannerid, descendantemployeecount, descendantgroupcount, descendantorganizationcount, employeefollowercount, missionstatement, name, overview, shortname, url, parentorganizationid, profiletabgroupid, themeid, updatescount, entitystreamviewid, alluserscancreategroups, streamscopeid) FROM stdin;
1	0	\N	\N	\N	\N	FFFFFF	\N	1	0	0	0	Sample root organization	Root Organization	\N	rootorg	http://www.eurekastreams.org	1	1	\N	30	5	t	5
\.


--
-- Data for Name: organization_coordinators; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization_coordinators (organization_id, coordinators_id) FROM stdin;
1	1
\.


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY person (id, version, accountid, avatarcropsize, avatarcropx, avatarcropy, avatarid, biography, cellphone, dateadded, email, fax, firstname, followerscount, followingcount, groupscount, lastname, location, middlename, opensocialid, overview, preferredname, quote, title, workphone, parentorganizationid, profiletabgroupid, starttabgroupid, themeid, personid, updatescount, entitystreamviewid, streamsearchhiddenlineindex, streamviewhiddenlineindex, lastacceptedtermsofservice, streamscopeid, commentable, streampostable, accountlocked) FROM stdin;
1	1	sysadmin	\N	\N	\N	\N	\N	\N	2010-01-01 00:00:00.000	sysadmin@localhost	\N	Administrator	0	0	0	Administrator	\N	A	F95CE1FC-893D-11DF-B0FB-5F0BE0D72085	\N	Administrator	\N	System Administrator	\N	1	5	6	\N	\N	0	6	2	3	2010-01-01 00:00:00.000	6	t	t	f
\.


--
-- Data for Name: person_streamview; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY person_streamview (person_id, streamviews_id, streamviewindex) FROM stdin;
1	1	0
1	2	1
1	3	2
1	4	3
\.

--
-- Data for Name: streamscope; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY streamscope (id, version, scopetype, uniquekey) FROM stdin;
5	0	ORGANIZATION	rootorg
6	0	PERSON	sysadmin
\.


--
-- Data for Name: streamview; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY streamview (id, version, name, type) FROM stdin;
5	0	rootorg	\N
6	0	sysadmin	\N
\.


--
-- Data for Name: streamview_streamscope; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY streamview_streamscope (streamview_id, includedscopes_id) FROM stdin;
5	5
6	6
\.


--
-- Data for Name: tab; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY tab (id, version, datedeleted, deleted, tabindex, tabgroupid, templateid) FROM stdin;
5	0	\N	f	0	5	2
6	0	\N	f	0	6	5
\.


--
-- Data for Name: tabgroup; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY tabgroup (id, version) FROM stdin;
5	0
6	0
\.


--
-- Data for Name: tabtemplate; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY tabtemplate (id, version, datedeleted, deleted, tablayout, tabname, type) FROM stdin;
5	0	\N	f	THREECOLUMN	Welcome	\N
\.



--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

--
-- CreatePerson
-- Function to create a minimal entry for a person for system evaluation.
--
CREATE OR REPLACE FUNCTION CreatePerson (_accountid VARCHAR, _firstname VARCHAR, _middlename VARCHAR,  _lastname VARCHAR, _email VARCHAR, _opensocialid VARCHAR) RETURNS VOID AS
$$
DECLARE
	_startPageTabGroupId BIGINT;
	_profileTabGroupId BIGINT;
	_tabTemplateId BIGINT;
	_streamScopeId BIGINT;
	_streamViewId BIGINT;
	_personId BIGINT;
	rec RECORD;
BEGIN
	-- create tab for start page
	FOR rec IN INSERT INTO tabgroup (version) values (0) RETURNING id LOOP
		_startPageTabGroupId := rec.id;
	END LOOP;
	FOR rec IN INSERT INTO tabtemplate (version, deleted, tablayout, tabname) VALUES (0, false, 'THREECOLUMN', 'Welcome') RETURNING id LOOP
		_tabTemplateId := rec.id;
	END LOOP;
	INSERT INTO tab (version, deleted, tabindex, tabgroupid, templateid) VALUES (0, false, 0, _startPageTabGroupId, _tabTemplateId);
	
	-- create tab for profile (not actually used, but may be required somewhere within the software)
	FOR rec IN INSERT INTO tabgroup (version) values (0) RETURNING id LOOP
		_profileTabGroupId := rec.id;
	END LOOP;
	_tabTemplateId := id FROM tabtemplate WHERE type = 'PERSON_ABOUT';
	INSERT INTO tab (version, deleted, tabindex, tabgroupid, templateid) VALUES (0, false, 0, _profileTabGroupId, _tabTemplateId);
	
	-- create stream
	FOR rec IN INSERT INTO streamscope (version, scopetype, uniquekey) values (0, 'PERSON', _accountid) RETURNING id LOOP
		_streamScopeId := rec.id;
	END LOOP;
	FOR rec IN INSERT INTO streamview (version, name) values (0, _accountid) RETURNING id LOOP
		_streamViewId := rec.id;
	END LOOP;
	INSERT INTO streamview_streamscope (streamview_id, includedscopes_id) VALUES (_streamViewId, _streamScopeId);
	
	-- create person
	FOR rec IN INSERT INTO person (version, accountid, dateadded, email, firstname, followerscount, followingcount, groupscount, 
			lastname, middlename, opensocialid, preferredname, parentorganizationid, profiletabgroupid, starttabgroupid, 
			updatescount, entitystreamviewid, streamsearchhiddenlineindex, streamviewhiddenlineindex, streamscopeid, 
			commentable, streampostable, accountlocked)
		VALUES (0, _accountid, now(), _email, _firstname, 0, 0, 0, _lastname, _middlename, _opensocialid, _firstname, 1, _profileTabGroupId, 
			_startPageTabGroupId, 0, _streamViewId, 2, 3, _streamScopeId, true, true, false) RETURNING id LOOP
		_personId := rec.id;
	END LOOP;

	-- add views for activity page
	INSERT INTO person_streamview (person_id, streamviews_id, streamviewindex) 
	VALUES (_personId, 1, 0), (_personId, 2, 1), (_personId, 3, 2), (_personId, 4, 3);
END;
$$ LANGUAGE plpgsql;
