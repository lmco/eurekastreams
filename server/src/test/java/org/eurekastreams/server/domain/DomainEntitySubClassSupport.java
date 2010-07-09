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

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Support class for unit testing.
 *
 */
@SuppressWarnings("serial")
public class DomainEntitySubClassSupport extends DomainEntity
{

    /**
     * Sets the id.
     * 
     * @param id
     *            - the id to set this to.
     */
    protected void setId(final long id)
    {
        super.setId(id);
    }
    
    /**
     * Sets the version.
     * 
     * @param version
     *            - the id to set this to.
     */
    public void setVersion(final long version)
    {
        super.setVersion(version);
    }
    
    /**
     * Gets the version.
     * 
     * @return the version.
     */
    public long getVersion()
    {
        return super.getVersion();
    }
}
