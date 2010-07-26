--
-- Set database version to 0.9.0016
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0016', 'U0016UpdateStreamViewName', 'Update Streamview name column.');

update streamview set name = (select person.preferredname || ' ' || person.lastname
                from person
                where person.entitystreamviewid = streamview.id)
where exists
(select person.preferredname
from person
where person.entitystreamviewid = streamview.id);