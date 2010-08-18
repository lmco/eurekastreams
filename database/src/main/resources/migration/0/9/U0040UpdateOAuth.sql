--
-- Set database version to 0.9.0040
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0040', 'U0040UpdateOAuth.sql', 'Remove unused oauthentrydto table and add sample oauth consumers.');

-- No need to explicitly drop constraints or sequence since they will be dropped automatically
DROP TABLE IF EXISTS oauthentrydto;

-- Add two consumers to support sample 3-legged and 2-legged oauth gadgets 
insert into oauthconsumer (version, callbackurl, consumerkey, consumersecret, gadgeturl, serviceprovidername, signaturemethod, title)
                   values (0, 'http://localhost/gadgets/oauthcallback', '38b78531332ea268', '9aa1b244cc61da7abaf287c70dfc', 'http://localhost/gadgets/threeleggedoauthdemo.xml', 'sevengoslings', 'HMAC-SHA1', 'seven goslings 3-legged');
                   
insert into oauthconsumer (version, callbackurl, consumerkey, consumersecret, gadgeturl, serviceprovidername, signaturemethod, title)
                   values (0, null, '8c75c97c1ee51315', '6fd5e3f41a8b91b9cfdb80b59e5c', 'http://localhost/gadgets/twoleggedoauthdemo.xml', 'shindig', 'HMAC-SHA1', 'seven goslings 2-legged');