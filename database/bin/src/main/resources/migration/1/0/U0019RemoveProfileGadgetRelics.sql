insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0019', 'U0019RemoveProfileGadgetRelics.sql', 'Remove all traces of the old gadget-based profile pages and clean up gadget owners.');

-- Update function to eliminate profile tabs
DROP FUNCTION IF EXISTS CreatePerson (_accountid VARCHAR, _firstname VARCHAR, _middlename VARCHAR,  _lastname VARCHAR, _email VARCHAR, _opensocialid VARCHAR);

CREATE FUNCTION CreatePerson (_accountid VARCHAR, _firstname VARCHAR, _middlename VARCHAR,  _lastname VARCHAR, _email VARCHAR, _opensocialid VARCHAR) RETURNS VOID AS
$$
DECLARE
    _startPageTabGroupId BIGINT;
    _profileTabGroupId BIGINT;
    _tabTemplateId BIGINT;
    _streamScopeId BIGINT;
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
    
    -- create stream
    FOR rec IN INSERT INTO streamscope (version, scopetype, uniquekey) values (0, 'PERSON', _accountid) RETURNING id LOOP
        _streamScopeId := rec.id;
    END LOOP;
    
    -- create person
    FOR rec IN INSERT INTO person (version, accountid, dateadded, email, firstname, followerscount, followingcount, groupscount, 
            lastname, middlename, opensocialid, preferredname, parentorganizationid, starttabgroupid, 
            updatescount, streamviewhiddenlineindex, streamscopeid, 
            commentable, streampostable, accountlocked)
        VALUES (0, _accountid, now(), _email, _firstname, 0, 0, 0, _lastname, _middlename, _opensocialid, _firstname, 1, 
            _startPageTabGroupId, 0, 3, _streamScopeId, true, true, false) RETURNING id LOOP
        _personId := rec.id;
    END LOOP;

    -- add views for activity page
    INSERT INTO person_stream (personid, streamid, streamindex) 
    VALUES (_personId, 1, 0), (_personId, 2, 1), (_personId, 3, 2), (_personId, 4, 3);
    
    -- follow self
    INSERT INTO follower (followerid, followingid) VALUES (_personId, _personId);
END;
$$ LANGUAGE plpgsql;

-- ---- Remove gadget task infrastructure ----

DROP TABLE person_task;
DROP TABLE organization_task;
DROP TABLE group_task;
DROP TABLE task;

-- ---- Remove all tabs, tab groups, tab templates, and gadgets that do not correspond to the start page (e.g. dead or profile page) ----

-- Create temporary functions for droping foreign keys
-- Some old environments have extraneous duplicate foreign key constraints, so we need to drop them dynamically instead of specifically by name.
DROP FUNCTION IF EXISTS DropAllInboundFKDynamically (_toTable VARCHAR);
CREATE FUNCTION DropAllInboundFKDynamically (_toTable VARCHAR) RETURNS VOID AS
$$
DECLARE
    rec RECORD;
BEGIN
	FOR rec IN
		SELECT tc.table_name, tc.constraint_name, kcu.column_name
		FROM information_schema.table_constraints tc
			LEFT JOIN information_schema.key_column_usage kcu 
				ON tc.constraint_catalog = kcu.constraint_catalog
					AND tc.constraint_schema = kcu.constraint_schema
					AND tc.constraint_name = kcu.constraint_name
			LEFT JOIN information_schema.referential_constraints rc
				ON tc.constraint_catalog = rc.constraint_catalog
					AND tc.constraint_schema = rc.constraint_schema
					AND tc.constraint_name = rc.constraint_name
			LEFT JOIN information_schema.constraint_column_usage ccu
				ON rc.unique_constraint_catalog = ccu.constraint_catalog
					AND rc.unique_constraint_schema = ccu.constraint_schema
					AND rc.unique_constraint_name = ccu.constraint_name
		WHERE ccu.table_name = _toTable
			AND tc.constraint_type = 'FOREIGN KEY'
	LOOP
		RAISE INFO 'Dropping constraint % from table % column %', rec.constraint_name, rec.table_name, rec.column_name;
		EXECUTE 'ALTER TABLE ' || rec.table_name || ' DROP CONSTRAINT ' || rec.constraint_name;
	END LOOP;
END;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS DropGadgetFKDynamically ();
CREATE FUNCTION  DropGadgetFKDynamically () RETURNS VOID AS
$$
DECLARE
    rec RECORD;
BEGIN
	FOR rec IN
		SELECT tc.table_name, tc.constraint_name, kcu.column_name
		FROM information_schema.table_constraints tc
			LEFT JOIN information_schema.key_column_usage kcu 
				ON tc.constraint_catalog = kcu.constraint_catalog
					AND tc.constraint_schema = kcu.constraint_schema
					AND tc.constraint_name = kcu.constraint_name
		WHERE tc.table_name = 'gadget'
			AND kcu.column_name IN ('gadgetdefinitionid','ownerid')
			AND tc.constraint_type = 'FOREIGN KEY'
	LOOP
		RAISE INFO 'Dropping constraint % from table % column %', rec.constraint_name, rec.table_name, rec.column_name;
		EXECUTE 'ALTER TABLE ' || rec.table_name || ' DROP CONSTRAINT ' || rec.constraint_name;
	END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Eliminate profile tab group column from person
ALTER TABLE person DROP COLUMN profiletabgroupid;

-- capture list of valid tab groups for use in removing obsolete tabs and tab groups
SELECT DISTINCT starttabgroupid AS id INTO TEMPORARY validtabgroups FROM person;

-- Remove tabs
-- Note:  Using temp table and outer join because performance was abysmal when using WHERE NOT IN on the delete.
SELECT t.id INTO TEMPORARY obsoletetabs
FROM tab t
LEFT OUTER JOIN validtabgroups vtg ON t.tabgroupid = vtg.id
WHERE vtg.id IS NULL;

DELETE FROM tab WHERE id IN (SELECT id FROM obsoletetabs);

DROP TABLE obsoletetabs;

-- Remove tab groups
SELECT tg.id INTO TEMPORARY obsoletetabgroups
FROM tabgroup tg
LEFT OUTER JOIN validtabgroups vtg ON tg.id = vtg.id
WHERE vtg.id IS NULL;

SELECT DropAllInboundFKDynamically ('tabgroup');

DELETE FROM tabgroup USING obsoletetabgroups WHERE tabgroup.id = obsoletetabgroups.id;

ALTER TABLE tab ADD CONSTRAINT tab_tabgroupid_fkey FOREIGN KEY (tabgroupid) REFERENCES tabgroup (id) ON DELETE CASCADE;
ALTER TABLE person ADD CONSTRAINT person_starttabgroupid_fkey FOREIGN KEY (starttabgroupid) REFERENCES tabgroup (id) ON DELETE RESTRICT;

DROP TABLE obsoletetabgroups;
DROP TABLE validtabgroups;

-- capture list of probably obsolete gadget definitions
SELECT gd.id INTO TEMPORARY obsoletegadgetdefinitions 
FROM gadget g 
INNER JOIN gadgetdefinition gd ON g.gadgetdefinitionid = gd.id
WHERE g.tabtemplateid IN (SELECT id FROM tabtemplate WHERE type LIKE '%_ABOUT') 
AND gd.showingallery = 'f';

-- capture list of valid tab templates
SELECT DISTINCT templateid AS id INTO TEMPORARY validtemplates FROM tab;
INSERT INTO validtemplates SELECT id FROM tabtemplate WHERE type IS NOT NULL AND type NOT LIKE '%_ABOUT';

-- Remove gadgets
SELECT g.id INTO TEMPORARY obsoletegadgets
FROM gadget g
LEFT OUTER JOIN validtemplates vt ON g.tabtemplateid = vt.id
WHERE vt.id IS NULL;

DELETE FROM gadget USING obsoletegadgets WHERE gadget.id = obsoletegadgets.id;

DROP TABLE obsoletegadgets;

-- Remove tab templates
SELECT tt.id INTO TEMPORARY obsoletetabtemplates
FROM tabtemplate tt
LEFT OUTER JOIN validtemplates vtt ON tt.id = vtt.id
WHERE vtt.id IS NULL;

SELECT DropAllInboundFKDynamically ('tabtemplate');

DELETE FROM tabtemplate USING obsoletetabtemplates WHERE tabtemplate.id = obsoletetabtemplates.id;

ALTER TABLE tab ADD CONSTRAINT tab_templateid_fkey FOREIGN KEY (templateid) REFERENCES tabtemplate (id) ON DELETE CASCADE;
ALTER TABLE gadget ADD CONSTRAINT gadget_tabtemplateid_fkey FOREIGN KEY (tabtemplateid) REFERENCES tabtemplate (id) ON DELETE CASCADE;

DROP TABLE obsoletetabtemplates;
DROP TABLE validtemplates;

-- Remove gadget definitions
DELETE FROM gadgetdefinition 
WHERE id IN (SELECT id FROM obsoletegadgetdefinitions)
AND id NOT IN (SELECT DISTINCT gadgetdefinitionid FROM gadget WHERE gadgetdefinitionid IS NOT NULL); 

-- Remove temp table
DROP TABLE obsoletegadgetdefinitions;

-- ---- Clean up definition-less gadgets ----

-- give each a gadget definition (must pick an arbitrary one, since there's no data to tell which is correct)
UPDATE gadget
SET gadgetdefinitionid = (SELECT MIN(id) FROM gadgetdefinition)
WHERE gadgetdefinitionid IS NULL;

-- add constraints to prevent this scenario from recurring
SELECT DropGadgetFKDynamically ();
ALTER TABLE gadget ALTER COLUMN gadgetdefinitionid SET NOT NULL;
ALTER TABLE gadget ADD CONSTRAINT gadget_gadgetdefinitionid_fkey FOREIGN KEY (gadgetdefinitionid) REFERENCES gadgetdefinition (id) ON DELETE CASCADE;
ALTER TABLE gadget ADD CONSTRAINT gadget_ownerid_fkey FOREIGN KEY (ownerid) REFERENCES person (id) ON DELETE CASCADE;

-- ---- Clean up owner-less gadgets ----
UPDATE gadget 
SET ownerid=p.id
FROM tabtemplate tt 
INNER JOIN tab t ON t.templateid = tt.id
INNER JOIN tabgroup tg ON t.tabgroupid = tg.id
INNER JOIN person p ON p.starttabgroupid = tg.id
WHERE gadget.tabtemplateid = tt.id
AND tt.type IS NULL
AND gadget.ownerid IS NULL;

---- Clean up ----

DROP FUNCTION DropAllInboundFKDynamically (_toTable VARCHAR);
DROP FUNCTION DropGadgetFKDynamically ();
