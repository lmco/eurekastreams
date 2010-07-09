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
package org.eurekastreams.web.client.model.requests;

import java.io.Serializable;
import java.util.Set;

import org.eurekastreams.server.action.request.PageableRequest;
import org.eurekastreams.server.domain.Person;

/**
 * Get Group Coordinators Request.
 *
 */
public class GetGroupCoordinatorsRequest implements Serializable, PageableRequest
{
    /**
     * Coordinators.
     */
    private Set<Person> coordinators;
    /**
     * Start index.
     */
    private Integer startIndex;
    /**
     * End index.
     */
    private Integer endIndex;
    /**
     * Group short name.
     */
    private String groupShortName;

    /**
     * Default constructor.
     * @param inGroupShortName group short name,
     * @param inCoordinators coordinators.
     */
    public GetGroupCoordinatorsRequest(final String inGroupShortName, final Set<Person> inCoordinators)
    {
        groupShortName = inGroupShortName;
        coordinators = inCoordinators;
    }

    /**
     * Get coordinators.
     * @return coordinators.
     */
    public Set<Person> getCoordinators()
    {
        return coordinators;
    }

    /**
     * Get group short name.
     * @return group short name.
     */
    public String getGroupShortName()
    {
        return groupShortName;
    }

    /**
     * Get end index.
     * @return index.
     */
    public Integer getEndIndex()
    {
        return endIndex;
    }

    /**
     * Get start index.
     * @return index.
     */
    public Integer getStartIndex()
    {
        return startIndex;
    }

    /**
     * Set end index.
     * @param inEndIndex the end index.
     */
    public void setEndIndex(final Integer inEndIndex)
    {
        endIndex = inEndIndex;
    }

    /**
     * Set start index.
     * @param inStartIndex the start index.
     */
    public void setStartIndex(final Integer inStartIndex)
    {
        startIndex = inStartIndex;
    }

}
