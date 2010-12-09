insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0019', 'U0019RemoveProfileGadgetRelics.sql', 'Remove all traces of the old gadget-based profile pages and clean up gadget owners.');

-- Update function to eliminate profile tabs
 
CREATE OR REPLACE FUNCTION CreatePerson (_accountid VARCHAR, _firstname VARCHAR, _middlename VARCHAR,  _lastname VARCHAR, _email VARCHAR, _opensocialid VARCHAR) RETURNS VOID AS
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

-- Eliminate profile tab group column from person
ALTER TABLE person DROP COLUMN profiletabgroupid;

-- Remove tabs
DELETE FROM tab WHERE tabgroupid NOT IN (SELECT DISTINCT starttabgroupid FROM person);

-- Remove tab groups
DELETE FROM tabgroup WHERE id NOT IN (SELECT DISTINCT starttabgroupid FROM person);

-- capture list of probably obsolete gadget definitions
SELECT gd.id INTO TEMPORARY obsoletegadgetdefinitions 
FROM gadget g 
INNER JOIN gadgetdefinition gd ON g.gadgetdefinitionid = gd.id
WHERE tabtemplateid IN (SELECT id FROM tabtemplate WHERE type LIKE '%_ABOUT') 
AND gd.showingallery = 'f';

-- Remove gadgets
DELETE FROM gadget WHERE tabtemplateid NOT IN 
(SELECT DISTINCT templateid FROM tab 
 UNION 
 SELECT id FROM tabtemplate WHERE type IS NOT NULL AND type NOT LIKE '%_ABOUT');

-- Remove tab templates
DELETE FROM tabtemplate WHERE id NOT IN 
(SELECT DISTINCT templateid FROM tab 
 UNION 
 SELECT id FROM tabtemplate WHERE type IS NOT NULL AND type NOT LIKE '%_ABOUT');

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
ALTER TABLE gadget ADD CONSTRAINT gadget_gadgetdefinitionid_fkey FOREIGN KEY (gadgetdefinitionid) REFERENCES gadgetdefinition (id) ON DELETE CASCADE;
ALTER TABLE gadget ALTER COLUMN gadgetdefinitionid SET NOT NULL;
ALTER TABLE gadget DROP CONSTRAINT fk7eae006c938cac5f;

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
