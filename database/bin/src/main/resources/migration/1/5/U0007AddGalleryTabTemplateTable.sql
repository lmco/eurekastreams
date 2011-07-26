insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0007', 
'U0007AddGalleryTabTemplateTable.', 'Add Tab galleryTabTemplate table');

    create table GalleryTabTemplate (
        id  bigserial not null,
        version int8 not null,
        created timestamp not null,
        description varchar(200) not null,
        title varchar(255) not null,
        categoryId int8,
        tabTemplateId int8,
        primary key (id)
    );
    
    alter table GalleryTabTemplate 
        add constraint FKFEE4109DA30070DF 
        foreign key (categoryId) 
        references GalleryItemCategory;

    alter table GalleryTabTemplate 
        add constraint FKFEE4109D6413F3C 
        foreign key (tabTemplateId) 
        references TabTemplate;