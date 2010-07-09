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
package org.eurekastreams.server.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Container for a list of ResourceSortCriterion objects.
 */
public class ResourceSortCriteria implements Serializable
{
    /**
     * serial Version UID.
     */
    private static final long serialVersionUID = 1646579071059869146L;

    /**
     * The criteria.
     */
    private List<ResourceSortCriterion> criteria;

    /**
     * Empty constructor for serialization.
     */
    protected ResourceSortCriteria()
    {
    }
    
    /**
     * Constructor.
     * 
     * @param inCriteria
     *            the list of criteria
     */
    public ResourceSortCriteria(final List<ResourceSortCriterion> inCriteria)
    {
        criteria = inCriteria;
    }

    /**
     * Get the criteria.
     * 
     * @return the criteria
     */
    public List<ResourceSortCriterion> getCriteria()
    {
        return criteria;
    }

    /**
     * Set the criteria.
     * 
     * @param inCriteria
     *            the criteria to set
     */
    public void setCriteria(final List<ResourceSortCriterion> inCriteria)
    {
        this.criteria = inCriteria;
    }

}
