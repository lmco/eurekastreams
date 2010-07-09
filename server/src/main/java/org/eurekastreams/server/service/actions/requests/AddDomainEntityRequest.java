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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;
import java.util.Map;

/**
 * Action Request for inserting a Domain Entity.
 *
 */
public class AddDomainEntityRequest
{
    /**
     * id of the user that owns the requested domain entities.
     */
    private String userId;
    
    /**
     * the fields values that should be stored in the new domain entity.
     */
    private Map<String, Serializable> fields;
    
    /**
     * Default constructor responsible for assembling the job item.
     * 
     * @param inUserId
     *            the user Id
     * @param inFields
     *            the fields 
     */
    public AddDomainEntityRequest(
            final String inUserId, 
            final Map<String, Serializable> inFields)
    {
        userId = inUserId;
        fields = inFields;
    }

    /**
     * Get the user Id.
     * 
     * @return the user Id
     */
    public String getUserId()
    {
        return this.userId;
    }

    /**
     * Get the fields used to populate the new domain entity.
     * 
     * @return the user Id
     */
    public Map<String, Serializable> getFields()
    {
        return this.fields;
    }
}
