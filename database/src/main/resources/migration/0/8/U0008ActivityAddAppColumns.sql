insert into db_version (major, minor, patch, scriptname, description)
   values (0,8,'0008','U0008ActivityAddAppColumns.sql','adds lastpostdate to feed');
    
ALTER TABLE activity
DROP column appid,
ADD column appid bigint,
ADD column appsource character varying(255),
ADD column appname character varying(255),
ADD column apptype character varying(255);


