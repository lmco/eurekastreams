insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0014', 
'U0014AddStreamResourceIndexForActivityTable', 'Added index to Activity table to speed up shared resource');


CREATE INDEX activity_actortype_actorid_linksharedresourceid_idx
 ON activity
 USING btree
 (actortype, actorid, linksharedresourceid);
 