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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * Request that carries a DomainGroup short name.
 */
public class DomainGroupShortNameRequest implements Serializable
{
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 2732605825098923809L;

    /**
     * Short name of the group.
     */
    private String groupShortName;

    /**
     * Empty constructor for domain group.
     */
    public DomainGroupShortNameRequest()
    {
    }

    /**
     * Empty constructor for domain group.
     *
     * @param inGroupShortName
     *            the short name of the domain group
     */
    public DomainGroupShortNameRequest(final String inGroupShortName)
    {
        groupShortName = inGroupShortName;
    }

    /**
     * Get the short name of the group.
     *
     * @return the short name of the group
     */
    public String getGroupShortName()
    {
        return groupShortName;
    }

    /**
     * Set the short name of the domain group.
     *
     * @param inGroupShortName
     *            the short name of the domain group
     */
    public void setGroupShortName(final String inGroupShortName)
    {
        groupShortName = inGroupShortName;
    }
}
