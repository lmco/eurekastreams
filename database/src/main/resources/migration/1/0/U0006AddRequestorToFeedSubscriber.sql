insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0006', 'U0006AddRequestorToFeedSubscriber.sql', 'Add Requestor column To Feed Subscriber table.');

ALTER TABLE FeedSubscriber
	ADD COLUMN requestorId int8 null
	REFERENCES Person ON DELETE CASCADE ON UPDATE CASCADE;

UPDATE FeedSubscriber
	SET requestorId = entityId
	WHERE type = 'PERSON';

UPDATE FeedSubscriber
	SET requestorId = i.coordinatorId
	FROM (SELECT domaingroup_id groupId, min(coordinators_id) coordinatorId from group_coordinators GROUP BY domaingroup_id) as i
	WHERE type = 'GROUP' AND FeedSubscriber.entityId = i.groupId;

ALTER TABLE FeedSubscriber 
	ALTER COLUMN requestorId SET NOT NULL;
