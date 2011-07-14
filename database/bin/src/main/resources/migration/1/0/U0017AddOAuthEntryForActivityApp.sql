insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0017', 'U0017AddOAuthEntryForActivityApp.sql', 'Adds the OAuth consumer entry for the Activity App.');

INSERT INTO oauthconsumer (version, callbackurl, consumerkey, consumersecret, gadgeturl, serviceprovidername, signaturemethod, title) 
VALUES (1, NULL, 'PUT_CONSUMER_KEY_HERE', 'PUT_CONSUMER_SECRET_HERE', 'http://localhost:8080/org/eurekastreams/gadgets/activitygadget.xml', 'eurekastreams', 'HMAC-SHA1', NULL);
