--
-- PostgreSQL 
--

--
-- Perform the count updates to the Person table
--
UPDATE person p SET followerscount = ( SELECT count( f.followerid ) FROM follower f WHERE f.followingid = p.id );

UPDATE person p SET followingcount = ( SELECT count( f.followerid ) FROM follower f WHERE f.followerid = p.id );

UPDATE person p SET groupscount = ( SELECT count( gf.followerid ) FROM groupfollower gf WHERE gf.followerid = p.id );

--
-- Perform the count updates to the Domain Group table
--
UPDATE domaingroup dg SET followerscount = ( SELECT count( gf.followerid ) FROM groupfollower gf WHERE gf.followingid = dg.id );

--
-- Perform the count updates to the Organization table
--

CREATE OR REPLACE FUNCTION ComputeOrganizationCounts () RETURNS VOID AS
$$
BEGIN

	-- clear counts
	UPDATE organization SET descendantemployeecount=-1, descendantgroupcount=-1, descendantorganizationcount = -1;

	-- repeat until all orgs updated
	WHILE EXISTS (SELECT * FROM organization WHERE descendantorganizationcount = -1) LOOP

		UPDATE organization
		SET 
			-- get descendant org count from inner query
			descendantorganizationcount = COALESCE(i1.numorg,0),
			-- descendant group count = children's descendant groups + this org's immedate groups
			descendantgroupcount = COALESCE(i1.numgroup,0) + COALESCE(i2.num,0),
			-- descendant employee count = children's descendant employees + this org's immedate employees
			descendantemployeecount = COALESCE(i1.numemp,0) + COALESCE(i3.num,0)
		FROM 
			organization o
			LEFT OUTER JOIN 
			(
				-- get collective info about an org's children
				SELECT
					parentorganizationid, 
					-- recursive descendant org count:  children's descendant orgs + number of child orgs
					SUM(descendantorganizationcount) + COUNT(*) as numorg, 
					-- children's descendant group count
					SUM(descendantgroupcount) as numgroup,
					-- children's descendant employee count
					SUM(descendantemployeecount) as numemp
				FROM organization
				WHERE
					-- exclude root org so it doesn't count itself as it's own child
					id <> parentorganizationid
				GROUP BY parentorganizationid
			) AS i1 ON o.id = i1.parentorganizationid
			LEFT OUTER JOIN 
			(
				-- get collective info about an org's immediate groups
				SELECT parentorganizationid, COUNT(*) AS num 
				FROM domaingroup
				GROUP BY parentorganizationid
			) AS i2 ON o.id = i2.parentorganizationid
			LEFT OUTER JOIN 
			(
				-- get collective info about an org's immediate employees
				SELECT parentorganizationid, COUNT(*) AS num 
				FROM person
				GROUP BY parentorganizationid
			) AS i3 ON o.id = i3.parentorganizationid
		WHERE 
			-- needed for doing left outer joins in updates on Postgres
			organization.id = o.id 
			-- only update orgs which have not been updated yet
			AND organization.descendantorganizationcount = -1 
			-- don't update orgs until all their (immediate) children have been updated.  
			-- otherwise stated, don't update any orgs which have un-updated children
			AND NOT EXISTS 
			(
				-- try to get a list of un-updated org children of the orgs being considered for update
				SELECT *
				FROM organization o1
				WHERE 
					-- if the org hasn't been updated
					o1.descendantorganizationcount = -1
					-- if the org isn't the root org
					AND o1.id <> o1.parentorganizationid
					-- if the org is a child of the org in the parent query
					AND o1.parentorganizationid = o.id
			);

	END LOOP;
end;
$$ LANGUAGE plpgsql;

SELECT ComputeOrganizationCounts();

DROP FUNCTION ComputeOrganizationCounts();
