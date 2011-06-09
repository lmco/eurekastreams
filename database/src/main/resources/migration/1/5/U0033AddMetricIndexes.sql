insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0033', 
'U0033AddMetricIndexes', 'Add indexes useful for metrics');

-- Activity
create index activity_actortype_streamscopeid_idx on Activity (actorType, streamScopeId);
create index activity_apptype_postedtime_streamscopeid_idx on Activity (appType, postedTime, streamScopeId);
create index activity_actortype_postedtime_streamscopeid_idx on Activity (postedTime, actorType, streamScopeId);

-- Comment
create index comment_timesent_activityid_idx on Comment (timeSent, activityId);
create index dailyusagesummary_usagedate_streamviewstreamscopeid_idx on DailyUsageSummary (usageDate, streamViewStreamScopeId);

-- Usage Metric
create index usagemetric_created_isstreamview_streamviewstreamscopeid_idx on UsageMetric (created, isStreamView, streamViewStreamScopeId);
create index usagemetric_created_streamviewstreamscopeid_idx on UsageMetric (created, streamViewStreamScopeId);
create index usagemetric_created_ispageview_idx on UsageMetric (created, isPageView);
