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
-- Name: activity; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE activity (
    id bigint NOT NULL,
    version bigint NOT NULL,
    actorid character varying(255) NOT NULL,
    actortype character varying(255) NOT NULL,
    annotation character varying(255),
    appid character varying(255),
    baseobject bytea,
    baseobjecttype character varying(255) NOT NULL,
    location character varying(255),
    mood character varying(255),
    opensocialid character varying(255) NOT NULL,
    originalactorid character varying(255),
    originalactortype character varying(255),
    postedtime timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL,
    verb character varying(255) NOT NULL,
    recipientparentorgid bigint NOT NULL,
    streamscopeid bigint NOT NULL,
    originalactivityid bigint,
    isdestinationstreampublic boolean NOT NULL
);

--
-- Name: activity_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE activity_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: activity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE activity_id_seq OWNED BY activity.id;


--
-- Name: activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('activity_id_seq', 1, true);


--
-- Name: appdata; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE appdata (
    id bigint NOT NULL,
    version bigint NOT NULL,
    gadgetdefinitionid bigint,
    personid bigint
);

--
-- Name: appdata_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE appdata_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: appdata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE appdata_id_seq OWNED BY appdata.id;


--
-- Name: appdata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('appdata_id_seq', 1, true);


--
-- Name: appdatavalue; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE appdatavalue (
    id bigint NOT NULL,
    version bigint NOT NULL,
    name character varying(255),
    value text,
    appdataid bigint
);

--
-- Name: appdatavalue_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE appdatavalue_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: appdatavalue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE appdatavalue_id_seq OWNED BY appdatavalue.id;


--
-- Name: appdatavalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('appdatavalue_id_seq', 1, true);


--
-- Name: background; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE background (
    id bigint NOT NULL,
    version bigint NOT NULL,
    personid bigint
);

--
-- Name: background_affiliations; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE background_affiliations (
    background_id bigint NOT NULL,
    backgrounditem_id bigint NOT NULL,
    affiliationindex integer NOT NULL
);

--
-- Name: background_honors; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE background_honors (
    background_id bigint NOT NULL,
    backgrounditem_id bigint NOT NULL,
    honorsindex integer NOT NULL
);

--
-- Name: background_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE background_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: background_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE background_id_seq OWNED BY background.id;


--
-- Name: background_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('background_id_seq', 1, true);


--
-- Name: background_interests; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE background_interests (
    background_id bigint NOT NULL,
    backgrounditem_id bigint NOT NULL,
    interestindex integer NOT NULL
);

--
-- Name: background_skills; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE background_skills (
    background_id bigint NOT NULL,
    backgrounditem_id bigint NOT NULL,
    skillsindex integer NOT NULL
);

--
-- Name: backgrounditem; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE backgrounditem (
    id bigint NOT NULL,
    version bigint NOT NULL,
    backgroundtype character varying(255),
    name character varying(50) NOT NULL
);

--
-- Name: backgrounditem_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE backgrounditem_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: backgrounditem_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE backgrounditem_id_seq OWNED BY backgrounditem.id;


--
-- Name: backgrounditem_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('backgrounditem_id_seq', 1, true);


--
-- Name: comment; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE comment (
    id bigint NOT NULL,
    version bigint NOT NULL,
    body character varying(250) NOT NULL,
    timesent timestamp without time zone NOT NULL,
    authorpersonid bigint NOT NULL,
    activityid bigint NOT NULL
);

--
-- Name: comment_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE comment_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: comment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE comment_id_seq OWNED BY comment.id;


--
-- Name: comment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('comment_id_seq', 1, true);

--
-- Name: db_version; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE db_version (
    major smallint NOT NULL,
    minor smallint NOT NULL,
    patch varchar(5) NOT NULL,
    scriptname varchar(50) NOT NULL,
    description text NOT NULL,
    timestamp timestamp DEFAULT current_timestamp
);

--
-- Name: domaingroup; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE domaingroup (
    id bigint NOT NULL,
    version bigint NOT NULL,
    avatarcropsize integer,
    avatarcropx integer,
    avatarcropy integer,
    avatarid character varying(255),
    bannerbackgroundcolor character varying(255),
    bannerid character varying(255),
    dateadded timestamp without time zone NOT NULL,
    followerscount integer NOT NULL,
    missionstatement character varying(500),
    name character varying(150) NOT NULL,
    privatesearchable boolean NOT NULL,
    publicgroup boolean NOT NULL,
    shortname character varying(150) NOT NULL,
    url character varying(255),
    parentorganizationid bigint,
    profiletabgroupid bigint NOT NULL,
    updatescount integer NOT NULL,
    themeid bigint,
    overview text,
    entitystreamviewid bigint,
    ispending boolean,
    createdbyid bigint NOT NULL,
    streamscopeid bigint,
    commentable boolean DEFAULT true NOT NULL,
    streampostable boolean DEFAULT true NOT NULL
);

--
-- Name: domaingroup_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE domaingroup_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: domaingroup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE domaingroup_id_seq OWNED BY domaingroup.id;


--
-- Name: domaingroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('domaingroup_id_seq', 1, true);


--
-- Name: enrollment; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE enrollment (
    id bigint NOT NULL,
    version bigint NOT NULL,
    additionaldetails character varying(200) NOT NULL,
    datefrom date,
    dateto date,
    degree character varying(255) NOT NULL,
    personid bigint,
    schoolnameid bigint,
    graddate date
);

--
-- Name: enrollment_activities; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE enrollment_activities (
    enrollment_id bigint NOT NULL,
    backgrounditem_id bigint NOT NULL,
    activitiesindex integer NOT NULL
);

--
-- Name: enrollment_areasofstudy; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE enrollment_areasofstudy (
    enrollment_id bigint NOT NULL,
    backgrounditem_id bigint NOT NULL,
    areasofstudyindex integer NOT NULL
);

--
-- Name: enrollment_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE enrollment_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: enrollment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE enrollment_id_seq OWNED BY enrollment.id;


--
-- Name: enrollment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('enrollment_id_seq', 1, true);


--
-- Name: feedreader; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE feedreader (
    id bigint NOT NULL,
    dateadded timestamp without time zone NOT NULL,
    moduleid character varying(255),
    opensocialid character varying(255),
    url character varying(255),
    feedtitle character varying(255) NOT NULL
);

--
-- Name: feedreader_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE feedreader_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: feedreader_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE feedreader_id_seq OWNED BY feedreader.id;


--
-- Name: feedreader_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('feedreader_id_seq', 1, false);


--
-- Name: follower; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE follower (
    followerid bigint NOT NULL,
    followingid bigint NOT NULL
);

--
-- Name: gadget; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE gadget (
    id bigint NOT NULL,
    version bigint NOT NULL,
    datedeleted timestamp without time zone,
    deleted boolean NOT NULL,
    minimized boolean NOT NULL,
    zoneindex integer NOT NULL,
    zonenumber integer NOT NULL,
    gadgetdefinitionid bigint,
    ownerid bigint,
    tabtemplateid bigint,
    gadgetuserpref character varying(100000)
);

--
-- Name: gadget_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE gadget_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: gadget_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE gadget_id_seq OWNED BY gadget.id;


--
-- Name: gadget_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('gadget_id_seq', 1, true);


--
-- Name: gadgetdefinition; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE gadgetdefinition (
    id bigint NOT NULL,
    version bigint NOT NULL,
    created timestamp without time zone NOT NULL,
    url character varying(255) NOT NULL,
    uuid character varying(255) NOT NULL,
    gadgetcategoryid bigint,
    ownerid bigint,
    showingallery boolean,
    numberofusers integer,
    gadgettitle character varying(255)
);

--
-- Name: gadgetdefinition_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE gadgetdefinition_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: gadgetdefinition_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE gadgetdefinition_id_seq OWNED BY gadgetdefinition.id;


--
-- Name: gadgetdefinition_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('gadgetdefinition_id_seq', 1, true);


--
-- Name: galleryitemcategory; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE galleryitemcategory (
    id bigint NOT NULL,
    version bigint NOT NULL,
    galleryitemtype character varying(255),
    name character varying(50) NOT NULL
);

--
-- Name: galleryitemcategory_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE galleryitemcategory_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: galleryitemcategory_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE galleryitemcategory_id_seq OWNED BY galleryitemcategory.id;


--
-- Name: galleryitemcategory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('galleryitemcategory_id_seq', 1, true);


--
-- Name: group_capability; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE group_capability (
    domaingroupid bigint NOT NULL,
    capabilityid bigint NOT NULL
);

--
-- Name: group_coordinators; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE group_coordinators (
    domaingroup_id bigint NOT NULL,
    coordinators_id bigint NOT NULL
);

--
-- Name: group_task; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE group_task (
    groupid bigint NOT NULL,
    taskid bigint NOT NULL
);

--
-- Name: groupfollower; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE groupfollower (
    followerid bigint NOT NULL,
    followingid bigint NOT NULL
);

--
-- Name: job; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE job (
    id bigint NOT NULL,
    version bigint NOT NULL,
    companyname character varying(50) NOT NULL,
    datefrom date,
    dateto date,
    description character varying(200) NOT NULL,
    industry character varying(255) NOT NULL,
    title character varying(50) NOT NULL,
    personid bigint
);

--
-- Name: job_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE job_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: job_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE job_id_seq OWNED BY job.id;


--
-- Name: job_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('job_id_seq', 1, true);


--
-- Name: linkinformation; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE linkinformation (
    id bigint NOT NULL,
    version bigint NOT NULL,
    created timestamp without time zone,
    description character varying(255),
    largestimageurl character varying(255),
    title character varying(255),
    url character varying(2048),
    source character varying(255)
);

--
-- Name: linkinformation_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE linkinformation_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: linkinformation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE linkinformation_id_seq OWNED BY linkinformation.id;


--
-- Name: linkinformation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('linkinformation_id_seq', 1, true);


--
-- Name: linkinformation_imageurls; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE linkinformation_imageurls (
    linkinformation_id bigint NOT NULL,
    element character varying(255)
);

--
-- Name: membershipcriteria; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE membershipcriteria (
    id bigint NOT NULL,
    version bigint NOT NULL,
    criteria character varying(255) NOT NULL,
    systemsettingsid bigint
);

--
-- Name: membershipcriteria_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE membershipcriteria_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: membershipcriteria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE membershipcriteria_id_seq OWNED BY membershipcriteria.id;


--
-- Name: membershipcriteria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('membershipcriteria_id_seq', 1, true);


--
-- Name: message; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE message (
    id bigint NOT NULL,
    version bigint NOT NULL,
    body character varying(250) NOT NULL,
    recipientid character varying(255) NOT NULL,
    recipienttype integer NOT NULL,
    timesent timestamp without time zone NOT NULL,
    title character varying(50),
    recipientparentorgid bigint NOT NULL,
    senderpersonid bigint NOT NULL,
    streamitemid bigint NOT NULL,
    attachment text,
    sharedactivityid bigint,
    sharerpersonid bigint
);

--
-- Name: message_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE message_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE message_id_seq OWNED BY message.id;


--
-- Name: message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('message_id_seq', 1, true);


--
-- Name: oauthconsumer; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE oauthconsumer (
    id bigint NOT NULL,
    version bigint NOT NULL,
    callbackurl character varying(255),
    consumerkey character varying(255) NOT NULL,
    consumersecret character varying(255) NOT NULL,
    gadgeturl character varying(255) NOT NULL,
    serviceprovidername character varying(255) NOT NULL,
    signaturemethod character varying(255) NOT NULL,
    title character varying(255)
);

--
-- Name: oauthconsumer_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE oauthconsumer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: oauthconsumer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE oauthconsumer_id_seq OWNED BY oauthconsumer.id;


--
-- Name: oauthconsumer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('oauthconsumer_id_seq', 1, false);


--
-- Name: oauthdomainentry; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE oauthdomainentry (
    id bigint NOT NULL,
    version bigint NOT NULL,
    appid character varying(255),
    authorized boolean NOT NULL,
    callbacktoken character varying(255),
    callbacktokenattempts integer NOT NULL,
    callbackurl character varying(255),
    callbackurlsigned boolean NOT NULL,
    container character varying(255) NOT NULL,
    domain character varying(255) NOT NULL,
    issuetime timestamp without time zone NOT NULL,
    oauthversion character varying(255) NOT NULL,
    token character varying(255) NOT NULL,
    tokensecret character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    userid character varying(255),
    consumerid bigint
);

--
-- Name: oauthdomainentry_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE oauthdomainentry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: oauthdomainentry_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE oauthdomainentry_id_seq OWNED BY oauthdomainentry.id;


--
-- Name: oauthdomainentry_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('oauthdomainentry_id_seq', 1, false);


--
-- Name: oauthentrydto; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE oauthentrydto (
    id bigint NOT NULL,
    version bigint NOT NULL,
    appid character varying(255),
    authorized boolean NOT NULL,
    callbacktoken character varying(255),
    callbacktokenattempts integer NOT NULL,
    callbackurl character varying(255),
    callbackurlsigned boolean NOT NULL,
    container character varying(255) NOT NULL,
    domain character varying(255) NOT NULL,
    issuetime timestamp without time zone NOT NULL,
    oauthversion character varying(255) NOT NULL,
    token character varying(255) NOT NULL,
    tokensecret character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    userid character varying(255),
    consumerid bigint
);

--
-- Name: oauthentrydto_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE oauthentrydto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: oauthentrydto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE oauthentrydto_id_seq OWNED BY oauthentrydto.id;


--
-- Name: oauthentrydto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('oauthentrydto_id_seq', 1, false);


--
-- Name: oauthtoken; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE oauthtoken (
    id bigint NOT NULL,
    version bigint NOT NULL,
    accesstoken character varying(255) NOT NULL,
    ownerid character varying(255) NOT NULL,
    tokenexpiremillis bigint,
    tokensecret character varying(255) NOT NULL,
    viewerid character varying(255) NOT NULL,
    consumerid bigint
);

--
-- Name: oauthtoken_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE oauthtoken_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: oauthtoken_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE oauthtoken_id_seq OWNED BY oauthtoken.id;


--
-- Name: oauthtoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('oauthtoken_id_seq', 1, false);


--
-- Name: organization; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE organization (
    id bigint NOT NULL,
    version bigint NOT NULL,
    avatarid character varying(255),
    avatarcropsize integer,
    avatarcropx integer,
    avatarcropy integer,
    bannerbackgroundcolor character varying(255) NOT NULL,
    bannerid character varying(255),
    descendantemployeecount integer NOT NULL,
    descendantgroupcount integer NOT NULL,
    descendantorganizationcount integer NOT NULL,
    employeefollowercount integer NOT NULL,
    missionstatement character varying(500),
    name character varying(150) NOT NULL,
    overview text,
    shortname character varying(150) NOT NULL,
    url character varying(255),
    parentorganizationid bigint,
    profiletabgroupid bigint NOT NULL,
    themeid bigint,
    updatescount integer NOT NULL,
    entitystreamviewid bigint,
    alluserscancreategroups boolean DEFAULT true NOT NULL,
    streamscopeid bigint
);

--
-- Name: organization_capability; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE organization_capability (
    organizationid bigint NOT NULL,
    capabilityid bigint NOT NULL
);

--
-- Name: organization_coordinators; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE organization_coordinators (
    organization_id bigint NOT NULL,
    coordinators_id bigint NOT NULL
);

--
-- Name: organization_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE organization_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: organization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE organization_id_seq OWNED BY organization.id;


--
-- Name: organization_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('organization_id_seq', 1, true);


--
-- Name: organization_leaders; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE organization_leaders (
    organization_id bigint NOT NULL,
    leaders_id bigint NOT NULL
);

--
-- Name: organization_task; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE organization_task (
    organizationid bigint NOT NULL,
    taskid bigint NOT NULL
);

--
-- Name: persistentlogin; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE persistentlogin (
    id bigint NOT NULL,
    version bigint NOT NULL,
    accountid character varying(255) NOT NULL,
    tokenexpirationdate bigint NOT NULL,
    tokenvalue character varying(255) NOT NULL
);

--
-- Name: persistentlogin_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE persistentlogin_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: persistentlogin_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE persistentlogin_id_seq OWNED BY persistentlogin.id;


--
-- Name: persistentlogin_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('persistentlogin_id_seq', 1, true);


--
-- Name: person; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE person (
    id bigint NOT NULL,
    version bigint NOT NULL,
    accountid character varying(255) NOT NULL,
    avatarcropsize integer,
    avatarcropx integer,
    avatarcropy integer,
    avatarid character varying(255),
    biography character varying(10000),
    cellphone character varying(255),
    dateadded timestamp without time zone NOT NULL,
    email character varying(255) NOT NULL,
    fax character varying(255),
    firstname character varying(50) NOT NULL,
    followerscount integer,
    followingcount integer,
    groupscount integer,
    lastname character varying(50) NOT NULL,
    location character varying(255),
    middlename character varying(50),
    opensocialid character varying(255),
    overview character varying(10000),
    preferredname character varying(255) NOT NULL,
    quote character varying(200),
    title character varying(150),
    workphone character varying(255),
    parentorganizationid bigint,
    profiletabgroupid bigint NOT NULL,
    starttabgroupid bigint NOT NULL,
    themeid bigint,
    personid bigint,
    updatescount integer,
    entitystreamviewid bigint,
    streamsearchhiddenlineindex integer,
    streamviewhiddenlineindex integer,
    lastacceptedtermsofservice timestamp without time zone,
    streamscopeid bigint,
    commentable boolean DEFAULT true NOT NULL,
    streampostable boolean DEFAULT true NOT NULL,
    accountlocked boolean DEFAULT false NOT NULL
);

--
-- Name: person_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE person_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE person_id_seq OWNED BY person.id;


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('person_id_seq', 1, true);


--
-- Name: person_relatedorganization; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE person_relatedorganization (
    organizationid bigint NOT NULL,
    personid bigint NOT NULL
);

--
-- Name: person_streamsearch; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE person_streamsearch (
    person_id bigint NOT NULL,
    streamsearches_id bigint NOT NULL,
    streamsearchindex integer NOT NULL
);

--
-- Name: person_streamview; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE person_streamview (
    person_id bigint NOT NULL,
    streamviews_id bigint NOT NULL,
    streamviewindex integer NOT NULL
);

--
-- Name: person_task; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE person_task (
    personid bigint NOT NULL,
    taskid bigint NOT NULL
);

--
-- Name: recommendation; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE recommendation (
    id bigint NOT NULL,
    version bigint NOT NULL,
    authoropensocialid character varying(255) NOT NULL,
    date timestamp without time zone NOT NULL,
    subjectopensocialid character varying(255) NOT NULL,
    text character varying(500) NOT NULL
);

--
-- Name: recommendation_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE recommendation_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: recommendation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE recommendation_id_seq OWNED BY recommendation.id;


--
-- Name: recommendation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('recommendation_id_seq', 1, true);


--
-- Name: starredactivity; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE starredactivity (
    activityid bigint NOT NULL,
    personid bigint NOT NULL
);

--
-- Name: streamitemid; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE streamitemid (
    id bigint NOT NULL,
    version bigint NOT NULL
);

--
-- Name: streamitemid_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE streamitemid_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: streamitemid_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE streamitemid_id_seq OWNED BY streamitemid.id;


--
-- Name: streamitemid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('streamitemid_id_seq', 1, true);


--
-- Name: streamscope; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE streamscope (
    id bigint NOT NULL,
    version bigint NOT NULL,
    scopetype character varying(255) NOT NULL,
    uniquekey character varying(255) NOT NULL
);

--
-- Name: streamscope_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE streamscope_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: streamscope_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE streamscope_id_seq OWNED BY streamscope.id;


--
-- Name: streamscope_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('streamscope_id_seq', 1, true);


--
-- Name: streamsearch; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE streamsearch (
    id bigint NOT NULL,
    version bigint NOT NULL,
    name character varying(255) NOT NULL,
    streamview_id bigint
);

--
-- Name: streamsearch_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE streamsearch_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: streamsearch_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE streamsearch_id_seq OWNED BY streamsearch.id;


--
-- Name: streamsearch_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('streamsearch_id_seq', 1, true);


--
-- Name: streamsearch_keywords; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE streamsearch_keywords (
    streamsearch_id bigint NOT NULL,
    element character varying(255)
);

--
-- Name: streamview; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE streamview (
    id bigint NOT NULL,
    version bigint NOT NULL,
    name character varying(255) NOT NULL,
    type character varying(255)
);

--
-- Name: streamview_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE streamview_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: streamview_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE streamview_id_seq OWNED BY streamview.id;


--
-- Name: streamview_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('streamview_id_seq', 1, true);


--
-- Name: streamview_streamscope; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE streamview_streamscope (
    streamview_id bigint NOT NULL,
    includedscopes_id bigint NOT NULL
);

--
-- Name: systemsettings; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE systemsettings (
    id bigint NOT NULL,
    version bigint NOT NULL,
    contentexpiration integer,
    contentwarningtext character varying(255),
    sitelabel character varying(255),
    termsofservice character varying(255),
    istosdisplayedeverysession boolean,
    tospromptinterval integer,
    sendwelcomeemails boolean DEFAULT false NOT NULL
);

--
-- Name: systemsettings_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE systemsettings_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: systemsettings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE systemsettings_id_seq OWNED BY systemsettings.id;


--
-- Name: systemsettings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('systemsettings_id_seq', 1, true);


--
-- Name: systemsettings_ldapgroups; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE systemsettings_ldapgroups (
    systemsettings_id bigint NOT NULL,
    element character varying(255)
);

--
-- Name: tab; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE tab (
    id bigint NOT NULL,
    version bigint NOT NULL,
    datedeleted timestamp without time zone,
    deleted boolean NOT NULL,
    tabindex integer,
    tabgroupid bigint,
    templateid bigint,
    CONSTRAINT tab_tabindex_check CHECK ((tabindex >= 0))
);

--
-- Name: tab_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE tab_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: tab_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE tab_id_seq OWNED BY tab.id;


--
-- Name: tab_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('tab_id_seq', 1, true);


--
-- Name: tabgroup; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE tabgroup (
    id bigint NOT NULL,
    version bigint NOT NULL
);

--
-- Name: tabgroup_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE tabgroup_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: tabgroup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE tabgroup_id_seq OWNED BY tabgroup.id;


--
-- Name: tabgroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('tabgroup_id_seq', 1, true);


--
-- Name: tabtemplate; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE tabtemplate (
    id bigint NOT NULL,
    version bigint NOT NULL,
    datedeleted timestamp without time zone,
    deleted boolean NOT NULL,
    tablayout character varying(255) NOT NULL,
    tabname character varying(50) NOT NULL,
    type character varying(255)
);

--
-- Name: tabtemplate_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE tabtemplate_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: tabtemplate_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE tabtemplate_id_seq OWNED BY tabtemplate.id;


--
-- Name: tabtemplate_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('tabtemplate_id_seq', 1, true);


--
-- Name: task; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE task (
    id bigint NOT NULL,
    version bigint NOT NULL,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    gadgetdefinitionid bigint
);

--
-- Name: task_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE task_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE task_id_seq OWNED BY task.id;


--
-- Name: task_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('task_id_seq', 1, true);


--
-- Name: theme; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE theme (
    id bigint NOT NULL,
    version bigint NOT NULL,
    bannerid character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    cssfile character varying(255) NOT NULL,
    dategenerated timestamp without time zone,
    description character varying(200) NOT NULL,
    name character varying(255) NOT NULL,
    themeurl character varying(255) NOT NULL,
    uuid character varying(255) NOT NULL,
    themecategoryid bigint,
    ownerid bigint,
    authoremail character varying(255) NOT NULL,
    authorname character varying(255) NOT NULL,
    numberofusers integer
);

--
-- Name: theme_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE theme_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: theme_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE theme_id_seq OWNED BY theme.id;


--
-- Name: theme_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('theme_id_seq', 1, true);

--
-- Name: feed; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE feed (
    id bigint NOT NULL,
    version bigint NOT NULL,
    pending boolean NOT NULL,
    updated integer,
    updatefrequency integer,
    url character varying(255) NOT NULL,
    streampluginid bigint NOT NULL
);

--
-- Name: feed_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE feed_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: feed_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE feed_id_seq OWNED BY feed.id;


--
-- Name: feed_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('feed_id_seq', 1, true);

--
-- Name: plugin; Type: TABLE; Schema: public; Owner: eurekastreams; Tablespace: 
--

CREATE TABLE plugin (
    id bigint NOT NULL,
    version bigint NOT NULL,
    updatefrequency integer
);

--
-- Name: plugin_id_seq; Type: SEQUENCE; Schema: public; Owner: eurekastreams
--

CREATE SEQUENCE plugin_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: plugin_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: eurekastreams
--

ALTER SEQUENCE plugin_id_seq OWNED BY plugin.id;


--
-- Name: plugin_id_seq; Type: SEQUENCE SET; Schema: public; Owner: eurekastreams
--

SELECT pg_catalog.setval('plugin_id_seq', 1, true);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE activity ALTER COLUMN id SET DEFAULT nextval('activity_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE appdata ALTER COLUMN id SET DEFAULT nextval('appdata_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE appdatavalue ALTER COLUMN id SET DEFAULT nextval('appdatavalue_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE background ALTER COLUMN id SET DEFAULT nextval('background_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE backgrounditem ALTER COLUMN id SET DEFAULT nextval('backgrounditem_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE comment ALTER COLUMN id SET DEFAULT nextval('comment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE domaingroup ALTER COLUMN id SET DEFAULT nextval('domaingroup_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE enrollment ALTER COLUMN id SET DEFAULT nextval('enrollment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE feedreader ALTER COLUMN id SET DEFAULT nextval('feedreader_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE gadget ALTER COLUMN id SET DEFAULT nextval('gadget_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE gadgetdefinition ALTER COLUMN id SET DEFAULT nextval('gadgetdefinition_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE galleryitemcategory ALTER COLUMN id SET DEFAULT nextval('galleryitemcategory_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE job ALTER COLUMN id SET DEFAULT nextval('job_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE linkinformation ALTER COLUMN id SET DEFAULT nextval('linkinformation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE membershipcriteria ALTER COLUMN id SET DEFAULT nextval('membershipcriteria_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE message ALTER COLUMN id SET DEFAULT nextval('message_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE oauthconsumer ALTER COLUMN id SET DEFAULT nextval('oauthconsumer_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE oauthdomainentry ALTER COLUMN id SET DEFAULT nextval('oauthdomainentry_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE oauthentrydto ALTER COLUMN id SET DEFAULT nextval('oauthentrydto_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE oauthtoken ALTER COLUMN id SET DEFAULT nextval('oauthtoken_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE organization ALTER COLUMN id SET DEFAULT nextval('organization_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE persistentlogin ALTER COLUMN id SET DEFAULT nextval('persistentlogin_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE person ALTER COLUMN id SET DEFAULT nextval('person_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE recommendation ALTER COLUMN id SET DEFAULT nextval('recommendation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE streamitemid ALTER COLUMN id SET DEFAULT nextval('streamitemid_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE streamscope ALTER COLUMN id SET DEFAULT nextval('streamscope_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE streamsearch ALTER COLUMN id SET DEFAULT nextval('streamsearch_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE streamview ALTER COLUMN id SET DEFAULT nextval('streamview_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE systemsettings ALTER COLUMN id SET DEFAULT nextval('systemsettings_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE tab ALTER COLUMN id SET DEFAULT nextval('tab_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE tabgroup ALTER COLUMN id SET DEFAULT nextval('tabgroup_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE tabtemplate ALTER COLUMN id SET DEFAULT nextval('tabtemplate_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE task ALTER COLUMN id SET DEFAULT nextval('task_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE theme ALTER COLUMN id SET DEFAULT nextval('theme_id_seq'::regclass);

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE feed ALTER COLUMN id SET DEFAULT nextval('feed_id_seq'::regclass);

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: eurekastreams
--

ALTER TABLE plugin ALTER COLUMN id SET DEFAULT nextval('plugin_id_seq'::regclass);