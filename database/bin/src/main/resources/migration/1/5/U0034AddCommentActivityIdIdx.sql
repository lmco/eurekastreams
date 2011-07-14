insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0034', 
'U0034AddCommentActivityIdIdx', 'Add index on Comment.activityId');


CREATE INDEX comment_activityid_idx
  ON "comment"
  USING btree
  (activityid);
