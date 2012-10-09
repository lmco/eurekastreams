/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.commons.logging.LogFactory;

import org.apache.commons.logging.Log;

/**
 * Gets a list of objects for a given list of pointer ids.
 * 
 * @param <ValueType>
 *            the object type being pointed to.
 */
public abstract class GetItemsByPointerIds<ValueType> extends BaseDomainMapper
{
    /** 
     * Logger. 
     */
    private Log log = LogFactory.make();
	
    /**
     * Mapper to get the IDs of objects by a string property.
     */
    private DomainMapper<List<String>, List<Long>> idsByStringsMapper;

    /**
     * Set the mapper to get the IDs of objects by strings.
     * 
     * @param inIdsByStringsMapper
     *            mapper to get the IDs of objects by strings.
     */
    public void setIdsByStringsMapper(final DomainMapper<List<String>, List<Long>> inIdsByStringsMapper)
    {
        idsByStringsMapper = inIdsByStringsMapper;
    }

    /**
     * Executes bulk method.
     * 
     * @param ids
     *            the ids to retrieve.
     * @return the items.
     */
    protected abstract List<ValueType> bulkExecute(final List<Long> ids);

    /**
     * Convenience wrapper around execute - fetch an object by its string id.
     * 
     * @param inId
     *            the string id
     * @return the object with the string id, or null if not found
     */
    public ValueType fetchUniqueResult(final String inId)
    {
        List<String> ids = new ArrayList<String>();
        ids.add(inId);
        
        List<ValueType> results = execute(ids);
        
        return results.size() == 0 ? null : results.get(0);
    }

    /**
     * Fetch the Long ID for the input String ID.
     * 
     * @param inId
     *            the string ID
     * @return the long ID
     */
    public Long fetchId(final String inId)
    {
        List<String> stringIds = new ArrayList<String>();
        stringIds.add(inId);

        List<Long> ids = idsByStringsMapper.execute(stringIds);
        if (ids.size() == 1)
        {
            return ids.get(0);
        }
        return null;
    }

    /**
     * Get entities by their string properties.
     * 
     * @param inStringIds
     *            the list of string ids that should be found.
     * @return list of DTO objects.
     */
    public List<ValueType> execute(final List<String> inStringIds)
    {   	
    	List<Long> ids = idsByStringsMapper.execute(inStringIds);

        // Checks to see if there's any real work to do
    	if (ids == null || ids.size() == 0 || containsAllNulls(ids))
        {
            log.debug("ids is null");
            return new ArrayList<ValueType>();
        }
        return new ArrayList<ValueType>(bulkExecute(ids));
    }
    
    /**
     * Check if every element in a list is null.
     * 
     * @param <T>
     * 	   generic
     * 
     * @param list
     * 		any list
     * 
     * @return whether list contains all null
     */
    private <T> boolean containsAllNulls(final List<T> list)
    {
        if (list != null)
        {
            Iterator<T> iterator = list.iterator();
            
            while (iterator.hasNext())
            {
            	if (iterator.next() != null)
            	{
            		return false;
            	}
            }
        }
        return true;
    }
}
