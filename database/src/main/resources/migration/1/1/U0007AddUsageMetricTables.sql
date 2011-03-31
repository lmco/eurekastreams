insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0007', 'U0007AddUsageMetricTables', 'add tables for usage metrics');

create table UsageMetric (
    id  bigserial not null,
    actorPersonId int8 not null,
    isPageView bool not null,
    isStreamView bool not null,
    updated timestamp not null,
    primary key (id)
);

create table DailyUsageSummary (
    id  bigserial not null,
    messageCount int8 not null,
    pageViewCount int8 not null,
    streamContributorCount int8 not null,
    streamViewCount int8 not null,
    streamViewerCount int8 not null,
    uniqueVisitorCount int8 not null,
    primary key (id)
);
