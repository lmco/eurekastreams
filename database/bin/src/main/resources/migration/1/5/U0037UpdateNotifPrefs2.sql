insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0037', 
'U0037UpdateNotifPrefs2', 'Update the notification preferences to rename APP_ALERT to IN_APP');

UPDATE notificationfilterpreference SET notifiertype = 'IN_APP' WHERE notifiertype = 'APP_ALERT';
