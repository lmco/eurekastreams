--
-- PostgreSQL
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: eurekastreams; Type: DATABASE; Schema: -; Owner: eurekastreams
--

DROP DATABASE IF EXISTS eurekastreams;

CREATE DATABASE eurekastreams WITH TEMPLATE = template1 ENCODING = 'UTF8';

ALTER DATABASE eurekastreams OWNER TO eurekastreams;

--- Add the pl/pgsql language

\c eurekastreams

CREATE FUNCTION plpgsql_call_handler()
	RETURNS OPAQUE AS '/usr/lib64/pgsql/plpgsql.so' LANGUAGE 'C';
	
CREATE LANGUAGE 'plpgsql' HANDLER plpgsql_call_handler
	LANCOMPILER 'PL/pgSQL';