insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0023',
'U0023AddColumnsToMemshpCriteria', 'Add galleryTabTemplateid and themeid columns to MembershipCriteria table.');

    alter table MembershipCriteria
        add column galleryTabTemplateId int8, 
        add column themeId int8;
        
    alter table MembershipCriteria 
        add constraint FK30D35175831629D2 
        foreign key (galleryTabTemplateId) 
        references GalleryTabTemplate;

    alter table MembershipCriteria 
        add constraint FK30D351752ABE850 
        foreign key (themeId) 
        references Theme;
              
        