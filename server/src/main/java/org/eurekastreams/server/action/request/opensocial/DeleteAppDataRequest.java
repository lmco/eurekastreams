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
package org.eurekastreams.server.action.request.opensocial;

import java.io.Serializable;
import java.util.Set;

/**
 * Request object for the GetAppData Action.
 *
 */
public class DeleteAppDataRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 450266103499391433L;

    /**
     * Local instance of the application id for the request.
     */
    private Long applicationId;

    /**
     * Local instance of the opensocial id for the request.
     */
    private String openSocialId;
    
    /**
     * Set of app data values to delete by key.
     */
    private Set<String> appDataValueKeys;

    /**
     * Default constructor.
     */
    public DeleteAppDataRequest()
    {
        //default constructor.
    }

    /**
     * Constructor for the Request object.
     * @param inApplicationId - instance of the application id making the request.
     * @param inOpenSocialId - opensocial id of the
     * @param inAppDataValueKeys - the set of strings that are the keys to delete
     */
    public DeleteAppDataRequest(final Long inApplicationId, final String inOpenSocialId, 
            final Set<String> inAppDataValueKeys)
    {
        applicationId = inApplicationId;
        openSocialId = inOpenSocialId;
        appDataValueKeys = inAppDataValueKeys;
    }

    /**
     * @return the applicationId
     */
    public Long getApplicationId()
    {
        return applicationId;
    }

    /**
     * @param inApplicationId the applicationId to set
     */
    public void setApplicationId(final Long inApplicationId)
    {
        this.applicationId = inApplicationId;
    }

    /**
     * @return the openSocialId
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * @param inOpenSocialId the openSocialId to set
     */
    public void setOpenSocialId(final String inOpenSocialId)
    {
        this.openSocialId = inOpenSocialId;
    }
    
    /**
     * @return the set of the app data value keys
     */
    public Set<String> getAppDataValueKeys()
    {
        return appDataValueKeys;
    }
    
    /**
     * @param inAppDataValueKeys the set of the app data value keys
     */
    public void setAppDataValueKeys(final Set<String> inAppDataValueKeys)
    {
        appDataValueKeys = inAppDataValueKeys;
    }
}
