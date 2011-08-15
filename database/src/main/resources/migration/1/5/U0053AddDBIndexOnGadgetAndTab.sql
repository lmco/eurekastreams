insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0053', 
'U0053AddDBIndexOnGadgetAndTab', 'Add indexes for gadget and tab tables to speed up cache warming.');

drop index if exists tabtemplateid_deleted_idx;

drop index if exists deleted_tabgroupid_idx;

create index tabtemplateid_deleted_idx on gadget(tabtemplateid, deleted);

create index deleted_tabgroupid_idx on tab(deleted, tabgroupid);
