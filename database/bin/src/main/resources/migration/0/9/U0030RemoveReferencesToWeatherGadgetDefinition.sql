--
-- Set database version to 0.9.0030
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0030', 'U0030RemoveReferencesToWeatherGadgetDefinition.sql', 'Remove all references to the weather gadget in the database.');

CREATE FUNCTION remove_references_to_gadget_definition(gadget_definition_id integer) RETURNS INTEGER AS '
DECLARE
    gadget_instance RECORD;
    count INTEGER DEFAULT 0;
BEGIN
    FOR gadget_instance IN SELECT id, zoneindex, zonenumber, tabtemplateid from gadget where gadgetdefinitionid = gadget_definition_id
    LOOP
        RAISE NOTICE ''Found a gadget instance % '',gadget_instance.id;
        UPDATE gadget SET zoneindex = zoneindex - 1 
        WHERE zoneindex > gadget_instance.zoneindex 
            AND zonenumber = gadget_instance.zonenumber
            AND tabtemplateid = gadget_instance.tabtemplateid;
        count = count +1;
    END LOOP;

    RAISE NOTICE ''Deleted % gadget instances for the supplied gadget definition id %'', count, gadget_definition_id;
    
    RAISE NOTICE ''Removing gadget instances for the supplied gadget_definition_id '';
    DELETE FROM gadget where gadgetdefinitionid = gadget_definition_id;

    RAISE NOTICE ''Removing gadget definition for the supplied gadget_definition_id '';
    DELETE FROM gadgetdefinition where id = gadget_definition_id;

    RETURN 1;
END;
' LANGUAGE plpgsql;
-- Id 18 is provided from our population scripts so it is safe to be hardcoded in this script.
SELECT remove_references_to_gadget_definition(18);
DROP FUNCTION remove_references_to_gadget_definition(gadget_definition_id integer);