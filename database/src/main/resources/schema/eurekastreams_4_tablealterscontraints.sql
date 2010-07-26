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
-- Name: activity_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (id);


--
-- Name: appdata_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY appdata
    ADD CONSTRAINT appdata_pkey PRIMARY KEY (id);


--
-- Name: appdatavalue_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY appdatavalue
    ADD CONSTRAINT appdatavalue_pkey PRIMARY KEY (id);


--
-- Name: background_affiliations_backgrounditem_id_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_affiliations
    ADD CONSTRAINT background_affiliations_backgrounditem_id_key UNIQUE (backgrounditem_id);


--
-- Name: background_affiliations_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_affiliations
    ADD CONSTRAINT background_affiliations_pkey PRIMARY KEY (background_id, affiliationindex);


--
-- Name: background_honors_backgrounditem_id_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_honors
    ADD CONSTRAINT background_honors_backgrounditem_id_key UNIQUE (backgrounditem_id);


--
-- Name: background_honors_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_honors
    ADD CONSTRAINT background_honors_pkey PRIMARY KEY (background_id, honorsindex);


--
-- Name: background_interests_backgrounditem_id_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_interests
    ADD CONSTRAINT background_interests_backgrounditem_id_key UNIQUE (backgrounditem_id);


--
-- Name: background_interests_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_interests
    ADD CONSTRAINT background_interests_pkey PRIMARY KEY (background_id, interestindex);


--
-- Name: background_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background
    ADD CONSTRAINT background_pkey PRIMARY KEY (id);


--
-- Name: background_skills_backgrounditem_id_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_skills
    ADD CONSTRAINT background_skills_backgrounditem_id_key UNIQUE (backgrounditem_id);


--
-- Name: background_skills_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY background_skills
    ADD CONSTRAINT background_skills_pkey PRIMARY KEY (background_id, skillsindex);


--
-- Name: backgrounditem_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY backgrounditem
    ADD CONSTRAINT backgrounditem_pkey PRIMARY KEY (id);


--
-- Name: comment_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);


--
-- Name: db_version_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY db_version
    ADD CONSTRAINT db_version_pkey PRIMARY KEY (major, minor, patch);


--
-- Name: domaingroup_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT domaingroup_pkey PRIMARY KEY (id);


--
-- Name: domaingroup_shortname_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT domaingroup_shortname_key UNIQUE (shortname);


--
-- Name: enrollment_activities_backgrounditem_id_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY enrollment_activities
    ADD CONSTRAINT enrollment_activities_backgrounditem_id_key UNIQUE (backgrounditem_id);


--
-- Name: enrollment_activities_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY enrollment_activities
    ADD CONSTRAINT enrollment_activities_pkey PRIMARY KEY (enrollment_id, activitiesindex);


--
-- Name: enrollment_areasofstudy_backgrounditem_id_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY enrollment_areasofstudy
    ADD CONSTRAINT enrollment_areasofstudy_backgrounditem_id_key UNIQUE (backgrounditem_id);


--
-- Name: enrollment_areasofstudy_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY enrollment_areasofstudy
    ADD CONSTRAINT enrollment_areasofstudy_pkey PRIMARY KEY (enrollment_id, areasofstudyindex);


--
-- Name: enrollment_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY enrollment
    ADD CONSTRAINT enrollment_pkey PRIMARY KEY (id);


--
-- Name: feedreader_moduleid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY feedreader
    ADD CONSTRAINT feedreader_moduleid_key UNIQUE (moduleid, opensocialid);


--
-- Name: feedreader_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY feedreader
    ADD CONSTRAINT feedreader_pkey PRIMARY KEY (id);


--
-- Name: follower_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY follower
    ADD CONSTRAINT follower_pkey PRIMARY KEY (followerid, followingid);


--
-- Name: gadget_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY gadget
    ADD CONSTRAINT gadget_pkey PRIMARY KEY (id);


--
-- Name: gadgetdefinition_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY gadgetdefinition
    ADD CONSTRAINT gadgetdefinition_pkey PRIMARY KEY (id);


--
-- Name: gadgetdefinition_uuid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY gadgetdefinition
    ADD CONSTRAINT gadgetdefinition_uuid_key UNIQUE (uuid);


--
-- Name: galleryitemcategory_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY galleryitemcategory
    ADD CONSTRAINT galleryitemcategory_pkey PRIMARY KEY (id);


--
-- Name: group_capability_capabilityid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY group_capability
    ADD CONSTRAINT group_capability_capabilityid_key UNIQUE (capabilityid);


--
-- Name: group_capability_domaingroupid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY group_capability
    ADD CONSTRAINT group_capability_domaingroupid_key UNIQUE (domaingroupid, capabilityid);


--
-- Name: group_coordinators_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY group_coordinators
    ADD CONSTRAINT group_coordinators_pkey PRIMARY KEY (domaingroup_id, coordinators_id);


--
-- Name: group_task_groupid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY group_task
    ADD CONSTRAINT group_task_groupid_key UNIQUE (groupid, taskid);


--
-- Name: groupfollower_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY groupfollower
    ADD CONSTRAINT groupfollower_pkey PRIMARY KEY (followerid, followingid);


--
-- Name: job_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY job
    ADD CONSTRAINT job_pkey PRIMARY KEY (id);


--
-- Name: linkinformation_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY linkinformation
    ADD CONSTRAINT linkinformation_pkey PRIMARY KEY (id);


--
-- Name: membershipcriteria_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY membershipcriteria
    ADD CONSTRAINT membershipcriteria_pkey PRIMARY KEY (id);


--
-- Name: message_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);


--
-- Name: oauthconsumer_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY oauthconsumer
    ADD CONSTRAINT oauthconsumer_pkey PRIMARY KEY (id);


--
-- Name: oauthdomainentry_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY oauthdomainentry
    ADD CONSTRAINT oauthdomainentry_pkey PRIMARY KEY (id);


--
-- Name: oauthentrydto_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY oauthentrydto
    ADD CONSTRAINT oauthentrydto_pkey PRIMARY KEY (id);


--
-- Name: oauthtoken_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY oauthtoken
    ADD CONSTRAINT oauthtoken_pkey PRIMARY KEY (id);


--
-- Name: organization_capability_capabilityid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization_capability
    ADD CONSTRAINT organization_capability_capabilityid_key UNIQUE (capabilityid);


--
-- Name: organization_capability_organizationid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization_capability
    ADD CONSTRAINT organization_capability_organizationid_key UNIQUE (organizationid, capabilityid);


--
-- Name: organization_coordinators_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization_coordinators
    ADD CONSTRAINT organization_coordinators_pkey PRIMARY KEY (organization_id, coordinators_id);


--
-- Name: organization_leaders_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization_leaders
    ADD CONSTRAINT organization_leaders_pkey PRIMARY KEY (organization_id, leaders_id);


--
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);


--
-- Name: organization_shortname_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_shortname_key UNIQUE (shortname);


--
-- Name: organization_task_organizationid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY organization_task
    ADD CONSTRAINT organization_task_organizationid_key UNIQUE (organizationid, taskid);


--
-- Name: persistentlogin_accountid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY persistentlogin
    ADD CONSTRAINT persistentlogin_accountid_key UNIQUE (accountid);


--
-- Name: persistentlogin_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY persistentlogin
    ADD CONSTRAINT persistentlogin_pkey PRIMARY KEY (id);


--
-- Name: persistentlogin_tokenvalue_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY persistentlogin
    ADD CONSTRAINT persistentlogin_tokenvalue_key UNIQUE (tokenvalue);


--
-- Name: person_accountid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_accountid_key UNIQUE (accountid);


--
-- Name: person_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: person_relatedorganization_personid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person_relatedorganization
    ADD CONSTRAINT person_relatedorganization_personid_key UNIQUE (personid, organizationid);


--
-- Name: person_relatedorganization_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person_relatedorganization
    ADD CONSTRAINT person_relatedorganization_pkey PRIMARY KEY (organizationid, personid);


--
-- Name: person_streamsearch_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person_streamsearch
    ADD CONSTRAINT person_streamsearch_pkey PRIMARY KEY (person_id, streamsearchindex);


--
-- Name: person_streamview_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person_streamview
    ADD CONSTRAINT person_streamview_pkey PRIMARY KEY (person_id, streamviewindex);


--
-- Name: person_task_personid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY person_task
    ADD CONSTRAINT person_task_personid_key UNIQUE (personid, taskid);


--
-- Name: plugin_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY plugin
    ADD CONSTRAINT plugin_pkey PRIMARY KEY (id);

--
-- Name: recommendation_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY recommendation
    ADD CONSTRAINT recommendation_pkey PRIMARY KEY (id);


--
-- Name: starredactivity_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY starredactivity
    ADD CONSTRAINT starredactivity_pkey PRIMARY KEY (activityid, personid);


--
-- Name: streamitemid_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY streamitemid
    ADD CONSTRAINT streamitemid_pkey PRIMARY KEY (id);


--
-- Name: streamscope_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY streamscope
    ADD CONSTRAINT streamscope_pkey PRIMARY KEY (id);


--
-- Name: streamsearch_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY streamsearch
    ADD CONSTRAINT streamsearch_pkey PRIMARY KEY (id);


--
-- Name: streamview_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY streamview
    ADD CONSTRAINT streamview_pkey PRIMARY KEY (id);


--
-- Name: streamview_streamscope_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY streamview_streamscope
    ADD CONSTRAINT streamview_streamscope_pkey PRIMARY KEY (streamview_id, includedscopes_id);


--
-- Name: systemsettings_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY systemsettings
    ADD CONSTRAINT systemsettings_pkey PRIMARY KEY (id);


--
-- Name: tab_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY tab
    ADD CONSTRAINT tab_pkey PRIMARY KEY (id);


--
-- Name: tabgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY tabgroup
    ADD CONSTRAINT tabgroup_pkey PRIMARY KEY (id);


--
-- Name: tabtemplate_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY tabtemplate
    ADD CONSTRAINT tabtemplate_pkey PRIMARY KEY (id);


--
-- Name: task_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY task
    ADD CONSTRAINT task_pkey PRIMARY KEY (id);


--
-- Name: theme_pkey; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY theme
    ADD CONSTRAINT theme_pkey PRIMARY KEY (id);

--
-- Name: theme_uuid_key; Type: CONSTRAINT; Schema: public; Owner: eurekastreams; Tablespace: 
--

ALTER TABLE ONLY theme
    ADD CONSTRAINT theme_uuid_key UNIQUE (uuid);


--
-- Name: fk1239d351eb3ab; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk1239d351eb3ab FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk14775196c04de; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY tab
    ADD CONSTRAINT fk14775196c04de FOREIGN KEY (templateid) REFERENCES tabtemplate(id);


--
-- Name: fk1477552f35e36; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY tab
    ADD CONSTRAINT fk1477552f35e36 FOREIGN KEY (tabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk14775a2dcda35; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY tab
    ADD CONSTRAINT fk14775a2dcda35 FOREIGN KEY (tabgroupid) REFERENCES tabgroup(id);

    
--
-- Name: fk15d7843e52a2bc28; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY follower
    ADD CONSTRAINT fk15d7843e52a2bc28 FOREIGN KEY (followingid) REFERENCES person(id);


--
-- Name: fk1ab676f42a703d60; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_affiliations
    ADD CONSTRAINT fk1ab676f42a703d60 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fk1ab676f47847b0e1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_affiliations
    ADD CONSTRAINT fk1ab676f47847b0e1 FOREIGN KEY (background_id) REFERENCES background(id);


--
-- Name: fk276a314ed5843ec; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background
    ADD CONSTRAINT fk276a314ed5843ec FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk27a9a5938cac5f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY task
    ADD CONSTRAINT fk27a9a5938cac5f FOREIGN KEY (gadgetdefinitionid) REFERENCES gadgetdefinition(id);


--
-- Name: fk288013a45a0afee8; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_capability
    ADD CONSTRAINT fk288013a45a0afee8 FOREIGN KEY (organizationid) REFERENCES organization(id);


--
-- Name: fk288013a4f0deb4ba; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_capability
    ADD CONSTRAINT fk288013a4f0deb4ba FOREIGN KEY (capabilityid) REFERENCES backgrounditem(id);


--
-- Name: fk2ec9b5d225c8b701; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_streamsearch
    ADD CONSTRAINT fk2ec9b5d225c8b701 FOREIGN KEY (person_id) REFERENCES person(id);


--
-- Name: fk2ec9b5d2a06b953; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_streamsearch
    ADD CONSTRAINT fk2ec9b5d2a06b953 FOREIGN KEY (streamsearches_id) REFERENCES streamsearch(id);


--
-- Name: fk2fe875433a791ab3; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY group_coordinators
    ADD CONSTRAINT fk2fe875433a791ab3 FOREIGN KEY (domaingroup_id) REFERENCES domaingroup(id);


--
-- Name: fk2fe87543a345f853; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY group_coordinators
    ADD CONSTRAINT fk2fe87543a345f853 FOREIGN KEY (coordinators_id) REFERENCES person(id);


--
-- Name: fk30923da21779c778; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY oauthtoken
    ADD CONSTRAINT fk30923da21779c778 FOREIGN KEY (consumerid) REFERENCES oauthconsumer(id);


--
-- Name: fk30d3517568a7e2e9; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY membershipcriteria
    ADD CONSTRAINT fk30d3517568a7e2e9 FOREIGN KEY (systemsettingsid) REFERENCES systemsettings(id);


--
-- Name: fk33de7d4b351eb3ab; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY appdata
    ADD CONSTRAINT fk33de7d4b351eb3ab FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk33de7d4b938cac5f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY appdata
    ADD CONSTRAINT fk33de7d4b938cac5f FOREIGN KEY (gadgetdefinitionid) REFERENCES gadgetdefinition(id);


--
-- Name: fk37f18514351eb3ab; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_relatedorganization
    ADD CONSTRAINT fk37f18514351eb3ab FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk37f185145a0afee8; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_relatedorganization
    ADD CONSTRAINT fk37f185145a0afee8 FOREIGN KEY (organizationid) REFERENCES organization(id);


--
-- Name: fk37f18514d5843ec; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_relatedorganization
    ADD CONSTRAINT fk37f18514d5843ec FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk42d0fef71779c778; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY oauthdomainentry
    ADD CONSTRAINT fk42d0fef71779c778 FOREIGN KEY (consumerid) REFERENCES oauthconsumer(id);


--
-- Name: fk49fd3c2f351eb3ab; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_task
    ADD CONSTRAINT fk49fd3c2f351eb3ab FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk49fd3c2f816eda0c; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_task
    ADD CONSTRAINT fk49fd3c2f816eda0c FOREIGN KEY (taskid) REFERENCES task(id);


--
-- Name: fk4cadbdf225c8b701; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_streamsearch
    ADD CONSTRAINT fk4cadbdf225c8b701 FOREIGN KEY (person_id) REFERENCES person(id);


--
-- Name: fk4cadbdf2a06b953; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_streamsearch
    ADD CONSTRAINT fk4cadbdf2a06b953 FOREIGN KEY (streamsearches_id) REFERENCES streamsearch(id);


--
-- Name: fk4d085a935d4e75f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY theme
    ADD CONSTRAINT fk4d085a935d4e75f FOREIGN KEY (themecategoryid) REFERENCES galleryitemcategory(id);


--
-- Name: fk4d085a9701f8461; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY theme
    ADD CONSTRAINT fk4d085a9701f8461 FOREIGN KEY (ownerid) REFERENCES person(id);


--
-- Name: fk50104153258e249e; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk50104153258e249e FOREIGN KEY (profiletabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk50104153336c46dc; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk50104153336c46dc FOREIGN KEY (streamscopeid) REFERENCES streamscope(id);


--
-- Name: fk501041539e7c9612; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk501041539e7c9612 FOREIGN KEY (parentorganizationid) REFERENCES organization(id);


--
-- Name: fk50104153a5396b4f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk50104153a5396b4f FOREIGN KEY (entitystreamviewid) REFERENCES streamview(id);


--
-- Name: fk50104153b79fc87; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk50104153b79fc87 FOREIGN KEY (themeid) REFERENCES theme(id);


--
-- Name: fk50104153d5a4a89f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk50104153d5a4a89f FOREIGN KEY (profiletabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk598cbae6aa5070ab; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY appdatavalue
    ADD CONSTRAINT fk598cbae6aa5070ab FOREIGN KEY (appdataid) REFERENCES appdata(id);


--
-- Name: fk67784f28d85b27c0; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY systemsettings_ldapgroups
    ADD CONSTRAINT fk67784f28d85b27c0 FOREIGN KEY (systemsettings_id) REFERENCES systemsettings(id);


--
-- Name: fk684e33fb258e249e; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fb258e249e FOREIGN KEY (profiletabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk684e33fb336c46dc; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fb336c46dc FOREIGN KEY (streamscopeid) REFERENCES streamscope(id);


--
-- Name: fk684e33fb9e7c9612; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fb9e7c9612 FOREIGN KEY (parentorganizationid) REFERENCES organization(id);


--
-- Name: fk684e33fba5396b4f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fba5396b4f FOREIGN KEY (entitystreamviewid) REFERENCES streamview(id);


--
-- Name: fk684e33fbb79fc87; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fbb79fc87 FOREIGN KEY (themeid) REFERENCES theme(id);


--
-- Name: fk684e33fbc9da04a0; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fbc9da04a0 FOREIGN KEY (createdbyid) REFERENCES person(id);


--
-- Name: fk684e33fbc9f1fe30; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fbc9f1fe30 FOREIGN KEY (entitystreamviewid) REFERENCES streamview(id);


--
-- Name: fk684e33fbd5a4a89f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY domaingroup
    ADD CONSTRAINT fk684e33fbd5a4a89f FOREIGN KEY (profiletabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk6ae8ef184c93c94b; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY group_capability
    ADD CONSTRAINT fk6ae8ef184c93c94b FOREIGN KEY (domaingroupid) REFERENCES domaingroup(id);


--
-- Name: fk6ae8ef18a3e873fb; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY group_capability
    ADD CONSTRAINT fk6ae8ef18a3e873fb FOREIGN KEY (capabilityid) REFERENCES backgrounditem(id);


--
-- Name: fk7025257a2a703d60; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_honors
    ADD CONSTRAINT fk7025257a2a703d60 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);

    
--
-- Name: fk7025257a7847b0e1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_honors
    ADD CONSTRAINT fk7025257a7847b0e1 FOREIGN KEY (background_id) REFERENCES background(id);


--
-- Name: fk789c1008752c5160; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment_activities
    ADD CONSTRAINT fk789c1008752c5160 FOREIGN KEY (enrollment_id) REFERENCES enrollment(id);


--
-- Name: fk789c1008dd79fca1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment_activities
    ADD CONSTRAINT fk789c1008dd79fca1 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fk7dbd3f24351eb3ab; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment
    ADD CONSTRAINT fk7dbd3f24351eb3ab FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk7dbd3f24aa562d22; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment
    ADD CONSTRAINT fk7dbd3f24aa562d22 FOREIGN KEY (schoolnameid) REFERENCES backgrounditem(id);


--
-- Name: fk7eae006c16a70e33; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY gadget
    ADD CONSTRAINT fk7eae006c16a70e33 FOREIGN KEY (tabtemplateid) REFERENCES tabtemplate(id);


--
-- Name: fk7eae006c938cac5f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY gadget
    ADD CONSTRAINT fk7eae006c938cac5f FOREIGN KEY (gadgetdefinitionid) REFERENCES gadgetdefinition(id);


--
-- Name: fk7eae006cb4103bca; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY gadget
    ADD CONSTRAINT fk7eae006cb4103bca FOREIGN KEY (ownerid) REFERENCES person(id);


--
-- Name: fk82afc4732a703d60; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_skills
    ADD CONSTRAINT fk82afc4732a703d60 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fk82afc47373c22920; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_skills
    ADD CONSTRAINT fk82afc47373c22920 FOREIGN KEY (background_id) REFERENCES background(id);


--
-- Name: fk82afc473dd79fca1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_skills
    ADD CONSTRAINT fk82afc473dd79fca1 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fk88047adf1da47e22; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY gadgetdefinition
    ADD CONSTRAINT fk88047adf1da47e22 FOREIGN KEY (gadgetcategoryid) REFERENCES galleryitemcategory(id);


--
-- Name: fk88047adfb4103bca; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY gadgetdefinition
    ADD CONSTRAINT fk88047adfb4103bca FOREIGN KEY (ownerid) REFERENCES person(id);


--
-- Name: fk8e488775258e249e; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e488775258e249e FOREIGN KEY (profiletabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk8e488775336c46dc; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e488775336c46dc FOREIGN KEY (streamscopeid) REFERENCES streamscope(id);


--
-- Name: fk8e4887757656ef13; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e4887757656ef13 FOREIGN KEY (personid) REFERENCES theme(id);


--
-- Name: fk8e4887759d4f0c17; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e4887759d4f0c17 FOREIGN KEY (starttabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk8e4887759e7c9612; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e4887759e7c9612 FOREIGN KEY (parentorganizationid) REFERENCES organization(id);


--
-- Name: fk8e488775a5396b4f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e488775a5396b4f FOREIGN KEY (entitystreamviewid) REFERENCES streamview(id);


--
-- Name: fk8e488775a5c6101b; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e488775a5c6101b FOREIGN KEY (streamscopeid) REFERENCES streamscope(id);


--
-- Name: fk8e488775b79ce026; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e488775b79ce026 FOREIGN KEY (themeid) REFERENCES theme(id);


--
-- Name: fk8e488775d5a4a89f; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person
    ADD CONSTRAINT fk8e488775d5a4a89f FOREIGN KEY (profiletabgroupid) REFERENCES tabgroup(id);


--
-- Name: fk9bde863f8ffcaa77; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT fk9bde863f8ffcaa77 FOREIGN KEY (authorpersonid) REFERENCES person(id);


--
-- Name: fk9bde863fa72a4ce0; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT fk9bde863fa72a4ce0 FOREIGN KEY (activityid) REFERENCES activity(id);


--
-- Name: fk9c2397e730953d1e; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY message
    ADD CONSTRAINT fk9c2397e730953d1e FOREIGN KEY (sharerpersonid) REFERENCES person(id);


--
-- Name: fk9c2397e789cf9aa4; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY message
    ADD CONSTRAINT fk9c2397e789cf9aa4 FOREIGN KEY (streamitemid) REFERENCES streamitemid(id);


--
-- Name: fk9c2397e7d9e9c2b5; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY message
    ADD CONSTRAINT fk9c2397e7d9e9c2b5 FOREIGN KEY (recipientparentorgid) REFERENCES organization(id);


--
-- Name: fka126572fa5c6101b; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572fa5c6101b FOREIGN KEY (streamscopeid) REFERENCES streamscope(id);


--
-- Name: fka126572fd32c5e36; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT fka126572fd32c5e36 FOREIGN KEY (recipientparentorgid) REFERENCES organization(id);


--
-- Name: fka537d97e3a006dac; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_leaders
    ADD CONSTRAINT fka537d97e3a006dac FOREIGN KEY (leaders_id) REFERENCES person(id);

    
--
-- Name: fka537d97ee05d1901; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_leaders
    ADD CONSTRAINT fka537d97ee05d1901 FOREIGN KEY (organization_id) REFERENCES organization(id);


--
-- Name: fkac9d5fba5c202e42; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY streamview_streamscope
    ADD CONSTRAINT fkac9d5fba5c202e42 FOREIGN KEY (streamview_id) REFERENCES streamview(id);


--
-- Name: fkac9d5fbabd447f6c; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY streamview_streamscope
    ADD CONSTRAINT fkac9d5fbabd447f6c FOREIGN KEY (includedscopes_id) REFERENCES streamscope(id);


--
-- Name: fkb7f4fd115a0afee8; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_task
    ADD CONSTRAINT fkb7f4fd115a0afee8 FOREIGN KEY (organizationid) REFERENCES organization(id);


--
-- Name: fkb7f4fd11816eda0c; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_task
    ADD CONSTRAINT fkb7f4fd11816eda0c FOREIGN KEY (taskid) REFERENCES task(id);


--
-- Name: fkbbf4bb9da1ea2a34; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY groupfollower
    ADD CONSTRAINT fkbbf4bb9da1ea2a34 FOREIGN KEY (followerid) REFERENCES person(id);


--
-- Name: fkbbf4bb9db18f0221; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY groupfollower
    ADD CONSTRAINT fkbbf4bb9db18f0221 FOREIGN KEY (followingid) REFERENCES domaingroup(id);


--
-- Name: fkc3347f85e73c2d8b; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY group_task
    ADD CONSTRAINT fkc3347f85e73c2d8b FOREIGN KEY (taskid) REFERENCES task(id);


--
-- Name: fkc3347f85ef8332ee; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY group_task
    ADD CONSTRAINT fkc3347f85ef8332ee FOREIGN KEY (groupid) REFERENCES domaingroup(id);


--
-- Name: fkcb77c0b87847b0e1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_interests
    ADD CONSTRAINT fkcb77c0b87847b0e1 FOREIGN KEY (background_id) REFERENCES background(id);


--
-- Name: fkcb77c0b8dd79fca1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY background_interests
    ADD CONSTRAINT fkcb77c0b8dd79fca1 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fkcc1310a19739a162; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY streamsearch_keywords
    ADD CONSTRAINT fkcc1310a19739a162 FOREIGN KEY (streamsearch_id) REFERENCES streamsearch(id);


--
-- Name: fkd7981d6f25c8b701; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_streamview
    ADD CONSTRAINT fkd7981d6f25c8b701 FOREIGN KEY (person_id) REFERENCES person(id);


--
-- Name: fkd7981d6f35b5dcd9; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY person_streamview
    ADD CONSTRAINT fkd7981d6f35b5dcd9 FOREIGN KEY (streamviews_id) REFERENCES streamview(id);


--
-- Name: fkd8b50b272a703d60; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment_areasofstudy
    ADD CONSTRAINT fkd8b50b272a703d60 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fkd8b50b27752c5160; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment_areasofstudy
    ADD CONSTRAINT fkd8b50b27752c5160 FOREIGN KEY (enrollment_id) REFERENCES enrollment(id);


--
-- Name: fkd8b50b27dd79fca1; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY enrollment_areasofstudy
    ADD CONSTRAINT fkd8b50b27dd79fca1 FOREIGN KEY (backgrounditem_id) REFERENCES backgrounditem(id);


--
-- Name: fke448a6cfa345f853; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_coordinators
    ADD CONSTRAINT fke448a6cfa345f853 FOREIGN KEY (coordinators_id) REFERENCES person(id);


--
-- Name: fke448a6cfe05d1901; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY organization_coordinators
    ADD CONSTRAINT fke448a6cfe05d1901 FOREIGN KEY (organization_id) REFERENCES organization(id);


--
-- Name: fkf3343c41779c778; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY oauthentrydto
    ADD CONSTRAINT fkf3343c41779c778 FOREIGN KEY (consumerid) REFERENCES oauthconsumer(id);


--
-- Name: fkf4d16992476abad2; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY linkinformation_imageurls
    ADD CONSTRAINT fkf4d16992476abad2 FOREIGN KEY (linkinformation_id) REFERENCES linkinformation(id);


ALTER TABLE ONLY feed
    ADD CONSTRAINT fk276a314ed584123 FOREIGN KEY (streampluginid) REFERENCES plugin(id);

--
-- Name: fk15d7843e90f5b78b; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY follower
    ADD CONSTRAINT fk15d7843e90f5b78b FOREIGN KEY (followerid) REFERENCES person(id);

--
-- Name: fk8216d90e242a4102; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY starredactivity
    ADD CONSTRAINT fk8216d90e242a4102 FOREIGN KEY (personid) REFERENCES person(id);


--
-- Name: fk8216d90e3e94a88a; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY starredactivity
    ADD CONSTRAINT fk8216d90e3e94a88a FOREIGN KEY (activityid) REFERENCES activity(id);

--
-- Name: fkb3a655689da9b48b; Type: FK CONSTRAINT; Schema: public; Owner: eurekastreams
--

ALTER TABLE ONLY streamsearch
    ADD CONSTRAINT fkb3a655689da9b48b FOREIGN KEY (streamview_id) REFERENCES streamview(id);
--
-- End.
--
