insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0019',
'U0019AddGalleryTabTemplateIdToTabTemplate', 'Add galleryTabTemplateColumn to TabTemplate table.');

    alter table TabTemplate
        add column galleryTabTemplateId int8;
        
    alter table TabTemplate 
        add constraint FKE226330F831629D2 
        foreign key (galleryTabTemplateId) 
        references GalleryTabTemplate;        
        