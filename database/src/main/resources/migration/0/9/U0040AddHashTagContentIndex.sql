
insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0040', 'U0040AddHashTagContentIndex.sql', 'Add hashtag.content index.');

create index hashtag_content_idx on HashTag (content);