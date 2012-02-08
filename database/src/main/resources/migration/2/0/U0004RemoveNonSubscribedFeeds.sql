insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0004', 'U0004RemoveNonSubscribedFeeds', 'Deletes all Feed entries that do not have subscribers.');

-- Delete all feeds that have no subscribers
DELETE FROM Feed WHERE id IN 
(
	SELECT f.id FROM Feed f LEFT OUTER JOIN FeedSubscriber fs ON f.id = fs.feedId GROUP BY f.id HAVING COUNT(fs.feedId)=0
);
