insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0035', 
'U0035UpdateNotifPrefs', 'Update the notification preferences due to category changes');

INSERT INTO notificationfilterpreference (personid, notifiertype, notificationcategory, version)
SELECT fp1.personid, fp1.notifiertype, 'FOLLOW', 0
FROM notificationfilterpreference fp1
	INNER JOIN notificationfilterpreference fp2
	ON fp1.notifiertype = fp2.notifiertype AND fp1.personid = fp2.personid
WHERE fp1.notificationcategory = 'FOLLOW_PERSON' AND fp2.notificationcategory = 'FOLLOW_GROUP';

DELETE FROM notificationfilterpreference
WHERE notificationcategory NOT IN ('POST_TO_PERSONAL_STREAM', 'COMMENT', 'LIKE', 'FOLLOW');
