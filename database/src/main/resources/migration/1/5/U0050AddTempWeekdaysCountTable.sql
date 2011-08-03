insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0050', 
'U0050AddTempWeekdaysCountTable', 'Add TempWeekdaysSinceDate table');

    create table TempWeekdaysSinceDate (
        id  bigserial not null,
        dateTimeStampInMilliseconds int8 not null,
        numberOfWeekdaysSinceDate int8 not null,
        primary key (id),
        unique (dateTimeStampInMilliseconds)
    );
    