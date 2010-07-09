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
 * Org tree DTO.
 */
public class OrganizationTreeDTO implements Serializable
{
    /**
     * Serial version ID.
     */
    private static final long serialVersionUID = -2889564057142399939L;

    /**
     * This orgs children.
     */
    private List<OrganizationTreeDTO> children;
    
    /**
     * Org short name.
     */
    private String shortName;
    
    /**
     * Org display name.
     */
    private String displayName;
    
    /**
     * Org id.
     */
    private Long orgId;
    
    /**
     * @param inChildren the children to set
     */
    public void setChildren(final List<OrganizationTreeDTO> inChildren)
    {
        this.children = inChildren;
    }
    
    /**
     * @return the children
     */
    public List<OrganizationTreeDTO> getChildren()
    {
        return children;
    }

    /**
     * @param inDisplayName the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        this.displayName = inDisplayName;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param inShortName the shortName to set
     */
    public void setShortName(final String inShortName)
    {
        this.shortName = inShortName;
    }

    /**
     * @return the shortName
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * @param inOrgId the orgId to set
     */
    public void setOrgId(final Long inOrgId)
    {
        this.orgId = inOrgId;
    }

    /**
     * @return the orgId
     */
    public Long getOrgId()
    {
        return orgId;
    }
}
