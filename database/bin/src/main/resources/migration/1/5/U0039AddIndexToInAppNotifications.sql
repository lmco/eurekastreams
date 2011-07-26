insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0039', 'U0039AddIndexToInAppNotifications', 'add index to InAppNotification for list and count queries');

-- used for both the count query and the list retrieval query, so recipientId is first to help with the latter
CREATE INDEX InAppNotification_recipientId_highPriority_isRead ON InAppNotification (recipientId, highPriority, isRead);
