/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;
import java.util.List;

/**
 * Request for DeleteIdsFromListsAction.
 */
public class DeleteIdsFromListsRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -2312618372701449828L;

    /**
     * List of cache keys that may contain values to be removed.
     */
    private List<String> keys;

    /**
     * List of values to be removed.
     */
    private List<Long> values;

    /**
     * Constructor.
     * 
     * @param inKeys
     *            the keys to set.
     * @param inValues
     *            the values to set.
     */
    public DeleteIdsFromListsRequest(final List<String> inKeys, final List<Long> inValues)
    {
        setKeys(inKeys);
        setValues(inValues);
    }

    /**
     * @param inKeys
     *            the keys to set
     */
    public void setKeys(final List<String> inKeys)
    {
        keys = inKeys;
    }

    /**
     * @return the keys
     */
    public List<String> getKeys()
    {
        return keys;
    }

    /**
     * @param inValues
     *            the values to set
     */
    public void setValues(final List<Long> inValues)
    {
        values = inValues;
    }

    /**
     * @return the values
     */
    public List<Long> getValues()
    {
        return values;
    }
}
