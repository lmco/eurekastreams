--
-- Set database version to 1.0.0002
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0002', 'U0002UpdateOAuth.sql', 'Remove unused oauthentrydto table and add sample oauth consumers.');

-- No need to explicitly drop constraints or sequence since they will be dropped automatically
DROP TABLE IF EXISTS oauthentrydto;

-- Add two consumers to support sample 3-legged and 2-legged oauth gadgets 
insert into oauthconsumer (version, callbackurl, consumerkey, consumersecret, gadgeturl, serviceprovidername, signaturemethod, title)
                   values (0, 'http://localhost:8080/gadgets/oauthcallback', '38b78531332ea268', '9aa1b244cc61da7abaf287c70dfc', 'http://localhost:8080/org/eurekastreams/gadgets/threeleggedoauthdemo.xml', 'sevengoslings', 'HMAC-SHA1', 'seven goslings 3-legged');
                   
insert into oauthconsumer (version, callbackurl, consumerkey, consumersecret, gadgeturl, serviceprovidername, signaturemethod, title)
                   values (0, null, '8c75c97c1ee51315', '6fd5e3f41a8b91b9cfdb80b59e5c', 'http://localhost:8080/org/eurekastreams/gadgets/twoleggedoauthdemo.xml', 'shindig', 'HMAC-SHA1', 'seven goslings 2-legged');