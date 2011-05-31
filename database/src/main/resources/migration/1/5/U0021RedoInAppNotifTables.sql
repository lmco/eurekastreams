insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0021', 'U0021RedoInAppNotifTables', 'Replace in-app notification table');

drop table applicationalertnotification;

create table InAppNotification (
	id  bigserial not null,
	version int8 not null,
	avatarOwnerType varchar(255) not null,
	avatarOwnerUniqueId varchar(255),
	highPriority bool not null,
	isRead bool not null,
	message varchar(255) not null,
	notificationDate timestamp not null,
	notificationType varchar(255) not null,
	sourceName varchar(255),
	sourceType varchar(255) not null,
	sourceUniqueId varchar(255),
	url varchar(2048),
	recipientId int8,
	primary key (id)
);

alter table InAppNotification
	add constraint FK157489A7880D76C6
	foreign key (recipientId)
	references Person;
