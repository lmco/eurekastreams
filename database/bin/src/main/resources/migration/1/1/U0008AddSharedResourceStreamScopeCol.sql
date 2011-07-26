insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0008', 'U0008AddSharedResourceStreamScopeCol', 'add streamScopeId col to SharedResource');

DELETE FROM Person_LikedSharedResources;

UPDATE Activity SET linkSharedResourceId = NULL;

DELETE FROM SharedResource;

ALTER TABLE SharedResource ADD COLUMN streamScopeId INT8 NOT NULL;

ALTER TABLE SharedResource
    ADD CONSTRAINT FK93856BB3956D52F2
    FOREIGN KEY (streamScopeId)
    REFERENCES StreamScope;
