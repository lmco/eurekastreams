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
-- Data for Name: persistentlogin; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY persistentlogin (id, version, accountid, tokenexpirationdate, tokenvalue) FROM stdin;
\.


--
-- Data for Name: activity; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY activity (id, version, actorid, actortype, annotation, appid, baseobject, baseobjecttype, location, mood, opensocialid, originalactorid, originalactortype, postedtime, updated, verb, recipientparentorgid, streamscopeid, originalactivityid) FROM stdin;
\.


--
-- Data for Name: appdata; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY appdata (id, version, gadgetdefinitionid, personid) FROM stdin;
\.


--
-- Data for Name: appdatavalue; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY appdatavalue (id, version, name, value, appdataid) FROM stdin;
\.


--
-- Data for Name: background; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY background (id, version, personid) FROM stdin;
\.


--
-- Data for Name: background_affiliations; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY background_affiliations (background_id, backgrounditem_id, affiliationindex) FROM stdin;
\.


--
-- Data for Name: background_honors; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY background_honors (background_id, backgrounditem_id, honorsindex) FROM stdin;
\.


--
-- Data for Name: background_interests; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY background_interests (background_id, backgrounditem_id, interestindex) FROM stdin;
\.


--
-- Data for Name: background_skills; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY background_skills (background_id, backgrounditem_id, skillsindex) FROM stdin;
\.


--
-- Data for Name: backgrounditem; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY backgrounditem (id, version, backgroundtype, name) FROM stdin;
\.


--
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY comment (id, version, body, timesent, authorpersonid, activityid) FROM stdin;
\.


--
-- Data for Name: domaingroup; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY domaingroup (id, version, avatarcropsize, avatarcropx, avatarcropy, avatarid, bannerbackgroundcolor, bannerid, dateadded, followerscount, missionstatement, name, privatesearchable, publicgroup, shortname, url, parentorganizationid, profiletabgroupid, updatescount, themeid, overview, entitystreamviewid, ispending, createdbyid, streamscopeid, commentable, streampostable) FROM stdin;
\.


--
-- Data for Name: enrollment; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY enrollment (id, version, additionaldetails, datefrom, dateto, degree, personid, schoolnameid, graddate) FROM stdin;
\.


--
-- Data for Name: enrollment_activities; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY enrollment_activities (enrollment_id, backgrounditem_id, activitiesindex) FROM stdin;
\.


--
-- Data for Name: enrollment_areasofstudy; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY enrollment_areasofstudy (enrollment_id, backgrounditem_id, areasofstudyindex) FROM stdin;
\.


--
-- Data for Name: feedreader; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY feedreader (id, dateadded, moduleid, opensocialid, url, feedtitle) FROM stdin;
\.


--
-- Data for Name: follower; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY follower (followerid, followingid) FROM stdin;
\.

--
-- Data for Name: group_capability; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY group_capability (domaingroupid, capabilityid) FROM stdin;
\.


--
-- Data for Name: group_coordinators; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY group_coordinators (domaingroup_id, coordinators_id) FROM stdin;
\.


--
-- Data for Name: group_task; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY group_task (groupid, taskid) FROM stdin;
\.


--
-- Data for Name: groupfollower; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY groupfollower (followerid, followingid) FROM stdin;
\.


--
-- Data for Name: job; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY job (id, version, companyname, datefrom, dateto, description, industry, title, personid) FROM stdin;
\.


--
-- Data for Name: linkinformation; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY linkinformation (id, version, created, description, largestimageurl, title, url, source) FROM stdin;
\.


--
-- Data for Name: linkinformation_imageurls; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY linkinformation_imageurls (linkinformation_id, element) FROM stdin;
\.


--
-- Data for Name: gadget; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY gadget (id, version, datedeleted, deleted, minimized, zoneindex, zonenumber, gadgetdefinitionid, ownerid, tabtemplateid, gadgetuserpref) FROM stdin;
2	0	\N	f	f	0	0	3	\N	2	\N
3	0	\N	f	f	0	1	12	\N	2	\N
4	0	\N	f	f	1	0	2	\N	2	\N
5	0	\N	f	f	1	1	6	\N	2	\N
6	0	\N	f	f	0	0	4	\N	3	\N
7	0	\N	f	f	0	1	5	\N	3	\N
8	0	\N	f	f	0	0	14	\N	4	\N
9	0	\N	f	f	0	1	13	\N	4	\N
13	0	\N	f	f	0	1	24	\N	1	\N
14	0	\N	f	f	0	2	25	\N	1	\N
\.

SELECT pg_catalog.setval('gadget_id_seq', 14, true);


--
-- Data for Name: gadgetdefinition; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY gadgetdefinition (id, version, created, url, uuid, gadgetcategoryid, ownerid, showingallery, numberofusers, gadgettitle) FROM stdin;
2	1	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/recommendations.xml	6c1b17c4-1945-4b3b-a136-dd1f643833d9	2	\N	f	0	\N
3	1	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/biography.xml	841dedc8-5b38-4cc4-9cf5-c82bb85a6e66	3	\N	f	0	\N
4	1	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/organization-overview.xml	821dedc8-5b38-4cc4-9cf5-c82bb85a6e66	3	\N	f	0	\N
5	1	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/organization-capabilities.xml	821dedc8-5b38-4ce4-3cf5-c82bb85a6e66	3	\N	f	0	\N
6	1	2009-05-04 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/background.xml	821eac8-5b38-4cc4-9cf5-c82bb85a6e66	3	\N	f	0	\N
13	1	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/group-capabilities.xml	921dedc8-5b38-4ce4-3cf5-c82bb85a6a56	3	\N	f	0	\N
14	1	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/group-overview.xml	d7a58391-5375-4c76-b5fc-a431c42a7487	3	\N	f	0	\N
12	2	2009-08-01 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/education.xml	841dadc8-5b38-4cc4-9cf5-c82bb85a6e66	3	\N	f	0	\N
18	0	2009-08-17 00:00:00	http://www.labpixies.com/campaigns/weather/weather.xml	d7a58391-5375-4c76-b5fc-a431c42a7333	3	\N	t	0	\N
21	0	2009-08-17 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/feed-reader.xml	d7a58391-5375-4c76-b5fc-a431c42a7666	3	\N	t	0	\N
22	0	2009-08-17 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/activitygadget.xml	d7a58391-5375-4c76-b5fc-a431c42a7555	3	\N	f	0	\N
24	0	2010-01-13 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/whitehouseblog-feed-reader.xml	06f98282-0083-11df-b82d-3a7356d89593	1	\N	t	0	\N
25	0	2010-01-13 00:00:00	http://localhost:8080/org/eurekastreams/gadgets/washtech-feed-reader.xml	0ba47620-0083-11df-8ff8-887356d89593	1	\N	t	0	\N
\.

SELECT pg_catalog.setval('gadgetdefinition_id_seq', 25, true);


--
-- Data for Name: galleryitemcategory; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY galleryitemcategory (id, version, galleryitemtype, name) FROM stdin;
1	0	GADGET	News
2	0	GADGET	Tools
3	0	GADGET	Productivity
4	0	THEME	Lockheed Martin
5	0	THEME	Nature
6	0	THEME	Abstract
\.

SELECT pg_catalog.setval('galleryitemcategory_id_seq', 6, true);


--
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY message (id, version, body, recipientid, recipienttype, timesent, title, recipientparentorgid, senderpersonid, streamitemid, attachment, sharedactivityid, sharerpersonid) FROM stdin;
\.


--
-- Data for Name: oauthconsumer; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY oauthconsumer (id, version, callbackurl, consumerkey, consumersecret, gadgeturl, serviceprovidername, signaturemethod, title) FROM stdin;
\.


--
-- Data for Name: oauthdomainentry; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY oauthdomainentry (id, version, appid, authorized, callbacktoken, callbacktokenattempts, callbackurl, callbackurlsigned, container, domain, issuetime, oauthversion, token, tokensecret, type, userid, consumerid) FROM stdin;
\.


--
-- Data for Name: oauthentrydto; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY oauthentrydto (id, version, appid, authorized, callbacktoken, callbacktokenattempts, callbackurl, callbackurlsigned, container, domain, issuetime, oauthversion, token, tokensecret, type, userid, consumerid) FROM stdin;
\.


--
-- Data for Name: oauthtoken; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY oauthtoken (id, version, accesstoken, ownerid, tokenexpiremillis, tokensecret, viewerid, consumerid) FROM stdin;
\.


--
-- Data for Name: theme; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY theme (id, version, bannerid, created, cssfile, dategenerated, description, name, themeurl, uuid, themecategoryid, ownerid, authoremail, authorname, numberofusers) FROM stdin;
6	0	/style/images/Start_page_3.jpg	2010-01-21 12:03:39.086	/themes/Lockheed Air Power.css	\N	Lockheed Air Power	Lockheed Air Power	http://localhost:8080/themes/start_page_3.xml	651c7e94-1f1f-4d85-9438-28dbfd86e53c	4	\N	sysadmin@example.com	Eureka Streams	0
8	0	/style/images/Start_page_5.jpg	2010-01-21 12:03:57.115	/themes/Lockheed Martin Through the Years.css	\N	Lockheed Martin Through the Years	Lockheed Martin Through the Years	http://localhost:8080/themes/start_page_5.xml	76aa85b5-700a-4ac5-9c97-f61e5bc87199	4	\N	sysadmin@example.com	Eureka Streams	0
9	0	/style/images/default-banner-eureka.png	2010-01-21 12:07:23.25	/themes/unity.css	\N	Default theme for the Eureka Framework	Eureka - the rush of discovery	http://localhost:8080/themes/unity.xml	a8d82fe9-dce6-496f-b835-77bb08eeb123	4	\N	sysadmin@example.com	Eureka Streams	0
5	0	/style/images/Start_page_2.jpg	2010-01-21 12:00:27.194	/themes/Blue Plasma.css	\N	Blue Plasma	Blue Plasma	http://localhost:8080/themes/start_page_2.xml	4b181152-f2ea-430e-b204-be47158cf3a1	6	\N	sysadmin@example.com	Eureka Streams	0
7	0	/style/images/Start_page_4.jpg	2010-01-21 12:03:47.798	/themes/Green Blocks.css	\N	Green Blocks	Green Blocks	http://localhost:8080/themes/start_page_4.xml	15ab3b38-db71-4eb7-8b51-876f0d77a33b	6	\N	sysadmin@example.com	Eureka Streams	0
4	1	/style/images/Start_page_1.jpg	2010-01-21 11:54:15.285	/themes/green_hills.css	\N	Green Hills	Green Hills	http://localhost:8080/themes/start_page_1.xml	c6b305b7-143e-44ce-925d-5143a6540894	5	\N	sysadmin@example.com	Eureka Streams	1122
\.

SELECT pg_catalog.setval('theme_id_seq', 9, true);


--
-- Data for Name: membershipcriteria; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY membershipcriteria (id, version, criteria, systemsettingsid) FROM stdin;
\.


--
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization (id, version, avatarid, avatarcropsize, avatarcropx, avatarcropy, bannerbackgroundcolor, bannerid, descendantemployeecount, descendantgroupcount, descendantorganizationcount, employeefollowercount, missionstatement, name, overview, shortname, url, parentorganizationid, profiletabgroupid, themeid, updatescount, entitystreamviewid, alluserscancreategroups, streamscopeid) FROM stdin;
\.


--
-- Data for Name: organization_capability; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization_capability (organizationid, capabilityid) FROM stdin;
\.


--
-- Data for Name: organization_coordinators; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization_coordinators (organization_id, coordinators_id) FROM stdin;
\.


--
-- Data for Name: organization_leaders; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization_leaders (organization_id, leaders_id) FROM stdin;
\.


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY person (id, version, accountid, avatarcropsize, avatarcropx, avatarcropy, avatarid, biography, cellphone, dateadded, email, fax, firstname, followerscount, followingcount, groupscount, lastname, location, middlename, opensocialid, overview, preferredname, quote, title, workphone, parentorganizationid, profiletabgroupid, starttabgroupid, themeid, personid, updatescount, entitystreamviewid, streamsearchhiddenlineindex, streamviewhiddenlineindex, lastacceptedtermsofservice, streamscopeid, commentable, streampostable, accountlocked) FROM stdin;
\.


--
-- Data for Name: person_relatedorganization; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY person_relatedorganization (organizationid, personid) FROM stdin;
\.


--
-- Data for Name: person_streamsearch; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY person_streamsearch (person_id, streamsearches_id, streamsearchindex) FROM stdin;
\.


--
-- Data for Name: person_streamview; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--
-- ** create 4 per person for the 4 default stream views
--

COPY person_streamview (person_id, streamviews_id, streamviewindex) FROM stdin;
\.


--
-- Data for Name: recommendation; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY recommendation (id, version, authoropensocialid, date, subjectopensocialid, text) FROM stdin;
\.


--
-- Data for Name: starredactivity; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY starredactivity (activityid, personid) FROM stdin;
\.


--
-- Data for Name: streamitemid; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY streamitemid (id, version) FROM stdin;
1	0
\.


--
-- Data for Name: streamscope; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--
-- ** create one for each person, org, and group that is created (PERSON accountid).
--

COPY streamscope (id, version, scopetype, uniquekey) FROM stdin;
1	0	ALL	everyone
2	0	STARRED	starred
3	0	PERSONS_PARENT_ORGANIZATION	parentorg
4	0	PERSONS_FOLLOWED_STREAMS	follow
\.

SELECT pg_catalog.setval('streamscope_id_seq', 7, true);


--
-- Data for Name: streamview; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--
-- ** create one for each person, org, and group that is created
--

COPY streamview (id, version, name, type) FROM stdin;
1	0	Following	PEOPLEFOLLOW
2	0	EUREKA:PARENT_ORG_TAG	PARENTORG
3	0	Everyone	EVERYONE
4	0	My starred items	STARRED
\.

SELECT pg_catalog.setval('streamview_id_seq', 6, true);

--
-- Data for Name: streamview_streamscope; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--
-- ** create one per person for the person, org, and group for stream stream to the stream scope.
--

COPY streamview_streamscope (streamview_id, includedscopes_id) FROM stdin;
1	4
2	3
3	1
4	2
\.


--
-- Data for Name: streamsearch; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY streamsearch (id, version, name, streamview_id) FROM stdin;
\.


--
-- Data for Name: streamsearch_keywords; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY streamsearch_keywords (streamsearch_id, element) FROM stdin;
\.


--
-- Data for Name: tab; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--
-- ** create 2 per person, one for the profiletabgroup and one for the starttabgroup
--

COPY tab (id, version, datedeleted, deleted, tabindex, tabgroupid, templateid) FROM stdin;
1	0	\N	f	0	3	1
2	0	\N	f	0	2	2
3	0	\N	f	0	1	3
4	0	\N	f	0	4	4
\.

SELECT pg_catalog.setval('tab_id_seq', 6, true);


--
-- Data for Name: tabgroup; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--
-- ** create 2 per person, one for the profiletabgroup and one for the starttabgroup
--

COPY tabgroup (id, version) FROM stdin;
1	0
2	0
3	0
4	0
\.

SELECT pg_catalog.setval('tabgroup_id_seq', 6, true);

--
-- Data for Name: tabtemplate; Type: TABLE DATA; Schema: public; Owner: eurekastreams
-- 
-- one record per person, org, or group
--

COPY tabtemplate (id, version, datedeleted, deleted, tablayout, tabname, type) FROM stdin;
1	0	\N	f	THREECOLUMN	Welcome	WELCOME
2	0	\N	f	TWOCOLUMN	About	PERSON_ABOUT
3	0	\N	f	TWOCOLUMN	About	ORG_ABOUT
4	0	\N	f	TWOCOLUMN	About	GROUP_ABOUT
\.

SELECT pg_catalog.setval('tabtemplate_id_seq', 5, true);

--
-- ## Task related data ##
--

--
-- Data for Name: task; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY task (id, version, description, name, gadgetdefinitionid) FROM stdin;
2	0	Write an overview of the organization.	Organization Overview	4
3	0	Fill in capabilities of the organization.	Organization Capabilities	5
4	0	Get another user to write a recommendation for you.	Get a Recommendation	2
5	0	Adding skills and interests increases your ability to be found and provides some information for others to connect with you on a personal level	Additional Information	6
6	0	Adding a work history provides others with a quick overview of the past positions you have held and an overview of your past achievements	Work History	3
7	0	Fill in capabilities of the group.	Group Capabilities	13
8	0	Write an overview of the group.	Group Overview	14
9	0	Fill in the schools you have attended.	Education History	12
\.

SELECT pg_catalog.setval('task_id_seq', 9, true);


--
-- Data for Name: person_task; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY person_task (personid, taskid) FROM stdin;
\.


--
-- Data for Name: organization_task; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY organization_task (organizationid, taskid) FROM stdin;
\.


--
-- Data for Name: systemsettings; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY systemsettings (id, version, contentexpiration, contentwarningtext, sitelabel, termsofservice, istosdisplayedeverysession, tospromptinterval, sendwelcomeemails) FROM stdin;
1	1	90	Content Warning Text	Site Label	&lt;These are the terms of service&gt;	f	1	f
\.


--
-- Data for Name: systemsettings_ldapgroups; Type: TABLE DATA; Schema: public; Owner: eurekastreams
--

COPY systemsettings_ldapgroups (systemsettings_id, element) FROM stdin;
\.


--
-- End
--