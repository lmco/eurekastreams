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
package org.eurekastreams.commons.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.transform.ResultTransformer;

/**
 * Result transformer that collapses the tuple into an object by picking a
 * single field from it.
 */
public class SingleFieldResultTransformer implements ResultTransformer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(SingleFieldResultTransformer.class);

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -8121219247857989006L;

    /**
     * The field name to extract.
     */
    private String fieldName;

    /**
     * Do nothing but return the input list.
     *
     * @param collection
     *            the collection to transform
     * @return the input collection, unmodified
     */
    @SuppressWarnings("unchecked")
    @Override
    public List transformList(final List collection)
    {
        return collection;
    }

    /**
     * Transform the input tuple, extracting the field with the name stored in
     * fieldName.
     *
     * @param tuple
     *            the values
     * @param aliases
     *            the aliases
     * @return the field with the name stored in fieldName
     */
    @Override
    public Object transformTuple(final Object[] tuple, final String[] aliases)
    {
        if (fieldName == null)
        {
            throw new RuntimeException("fieldName is null");
        }
        log.trace("Transforming tuple - looking for field '" + fieldName + "'");

        for (int i = 0; i < aliases.length; i++)
        {
            if (aliases[i].toLowerCase().equals(fieldName.toLowerCase()))
            {
                if (log.isTraceEnabled())
                {
                    if (tuple[i] != null)
                    {
                        log.trace("Found the correct field '" + fieldName
                                + "', value: " + tuple[i] + "'");
                    }
                    else
                    {
                        log.trace("Found the correct field '" + fieldName
                                + "', and it's null");
                    }
                }
                return tuple[i];
            }
            log.trace("Skipping field '" + aliases[i] + "'");
        }
        log.trace("Couldn't find the correct field '" + fieldName + "'");
        return null;
    }

    /**
     * Set the field name to extract from the tuple.
     *
     * @param inFieldName
     *            to extract from the tuple
     */
    public void setFieldName(final String inFieldName)
    {
        this.fieldName = inFieldName;
    }
}
