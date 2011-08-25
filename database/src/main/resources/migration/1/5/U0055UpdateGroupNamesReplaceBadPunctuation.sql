insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0055', 
'U0055UpdateGroupNamesReplaceBadPunctuation', 'Replace undesirable punctuation with benign punctuation.');

UPDATE domaingroup
SET name = replace(replace(replace(replace(replace(replace(replace(replace(replace(name, '<', '('), '>', ')'), '[', '('), ']', ')'), '{', '('), '}', ')'), '|', ':'), E'\\', '/'), '`', E'\'')
WHERE name SIMILAR TO E'%[\\[\\]{}\\\\|`<>]%';
