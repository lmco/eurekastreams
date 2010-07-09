/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.search.directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.hibernate.transform.ResultTransformer;

/**
 * Result transformer that transforms tuples of directory object (person, group, organization) property names and values
 * into ModelViews using cache/db mappers.
 */
public class CachedModelViewResultTransformer implements ResultTransformer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CachedModelViewResultTransformer.class);

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -3492085599171644653L;

    /**
     * The property name that Hibernate uses to store the Hibernate Class of the entity.
     */
    private static final String HIBERNATE_CLASS_PROPERTY_NAME = "_hibernate_class";

    /**
     * The property name of the id.
     */
    private static final String HIBERNATE_ID_PROPERTY_NAME = "__HSearch_id";

    /**
     * The mapper to get the organizations by ids.
     */
    private GetOrganizationsByIds getOrgsByIdsMapper;

    /**
     * The mapper to get the domain groups by ids.
     */
    private GetDomainGroupsByIds getDomainGroupsByIdsMapper;

    /**
     * The mapper to get the people by Ids.
     */
    private GetPeopleByIds getPeopleByIdsMapper;

    /**
     * Handle list transformation, receiving a list of Maps of property name to value.
     * 
     * @param inCollection
     *            list of maps (property name to value)
     * @return a list of ModelViews from the input list of maps (property name to value)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List transformList(final List inCollection)
    {
        log.trace("transformList.");

        // keep track of the order - we need to return the ModelViews in the same order as the mapped entities
        Map<String, Integer> positionList = new HashMap<String, Integer>();

        // group the different types to consolidate the fetches
        List<Long> orgIds = new ArrayList<Long>();
        List<Long> groupIds = new ArrayList<Long>();
        List<Long> peopleIds = new ArrayList<Long>();

        int listPos = 0;
        for (Object mapObj : inCollection)
        {
            Map<String, Object> map = (Map<String, Object>) mapObj;
            Class< ? > clazz = (Class< ? >) map.get(HIBERNATE_CLASS_PROPERTY_NAME);
            Long entityId = (Long) map.get(HIBERNATE_ID_PROPERTY_NAME);
            if (clazz == Organization.class)
            {
                orgIds.add(entityId);
                positionList.put("O" + entityId, listPos);
                listPos++;
            }
            else if (clazz == DomainGroup.class)
            {
                groupIds.add(entityId);
                positionList.put("G" + entityId, listPos);
                listPos++;
            }
            else if (clazz == Person.class)
            {
                peopleIds.add(entityId);
                positionList.put("P" + entityId, listPos);
                listPos++;
            }
        }

        ModelView[] results = new ModelView[inCollection.size()];

        // make the requests to get the entities in bulk by type, then put them back in order
        if (orgIds.size() > 0)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Checking cache/db for Orgs:" + orgIds.toString());
            }
            for (ModelView org : getOrgsByIdsMapper.execute(orgIds))
            {
                results[positionList.get("O" + org.getEntityId())] = org;
            }
        }

        if (groupIds.size() > 0)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Checking cache/db for Groups:" + groupIds.toString());
            }
            for (ModelView group : getDomainGroupsByIdsMapper.execute(groupIds))
            {
                results[positionList.get("G" + group.getEntityId())] = group;
            }
        }

        if (peopleIds.size() > 0)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Checking cache/db for People:" + peopleIds.toString());
            }

            for (ModelView person : getPeopleByIdsMapper.execute(peopleIds))
            {
                results[positionList.get("P" + person.getEntityId())] = person;
            }
        }

        // NOTE: don't use Arrays.asList here - there's a GWT serialization bug that surfaces with the return value
        ArrayList<ModelView> returnList = new ArrayList<ModelView>();
        int arraySize = results.length;
        for (int i = 0; i < arraySize; i++)
        {
            // discard nulls for any unknown entities or unhandled entity types
            if (results[i] != null)
            {
                returnList.add(results[i]);
            }
            else
            {
                Map<String, Object> map = (Map<String, Object>) inCollection.get(i);
                Class< ? > clazz = (Class< ? >) map.get(HIBERNATE_CLASS_PROPERTY_NAME);
                Long entityId = (Long) map.get(HIBERNATE_ID_PROPERTY_NAME);
                log.warn("Null entry in transformed list discarded.  Index " + i + " contained an entry of type "
                        + clazz.getName() + " with id " + entityId + ". Possible duplicate or unhandled entity type ");
            }
        }
        return returnList;
    }

    /**
     * Transform the input list of properties into a Map of property to value. transformList will group these and
     * request the ModelViews in bulk.
     * 
     * @param inTuple
     *            the field values
     * @param inAliases
     *            the field names
     * @return a list of Maps of field name to value.
     */
    @Override
    public Object transformTuple(final Object[] inTuple, final String[] inAliases)
    {
        return getMapFromTuplesAndAliases(inTuple, inAliases);
    }

    /**
     * Convert the input tuple and aliases to a key-value mapping.
     * 
     * @param tuple
     *            values returned from Hibernate Search.
     * 
     * @param aliases
     *            result keys returned from Hibernate Search.
     * 
     * @return a key-value mapping of the Hibernate search tupple/aliases.
     */
    protected Map<String, Object> getMapFromTuplesAndAliases(final Object[] tuple, final String[] aliases)
    {
        Map<String, Object> result = new HashMap<String, Object>(tuple.length);
        for (int i = 0; i < tuple.length; i++)
        {
            result.put(aliases[i], tuple[i]);
        }
        return result;
    }

    /**
     * Set the GetOrganizationsByIds mapper.
     * 
     * @param inGetOrgsByIdsMapper
     *            the getOrgsByIdsMapper to set
     */
    public void setGetOrgsByIdsMapper(final GetOrganizationsByIds inGetOrgsByIdsMapper)
    {
        this.getOrgsByIdsMapper = inGetOrgsByIdsMapper;
    }

    /**
     * Set the GetDomainGroupsByIds mapper.
     * 
     * @param inGetDomainGroupsByIdsMapper
     *            the getDomainGroupsByIdsMapper to set
     */
    public void setGetDomainGroupsByIdsMapper(final GetDomainGroupsByIds inGetDomainGroupsByIdsMapper)
    {
        this.getDomainGroupsByIdsMapper = inGetDomainGroupsByIdsMapper;
    }

    /**
     * Set the GetPeopleByIds mapper.
     * 
     * @param inGetPeopleByIdsMapper
     *            the getPeopleByIdsMapper to set
     */
    public void setGetPeopleByIdsMapper(final GetPeopleByIds inGetPeopleByIdsMapper)
    {
        this.getPeopleByIdsMapper = inGetPeopleByIdsMapper;
    }

}
