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

/**
 * Request object for the GetAppData Action.
 *
 */
public class GetAppDataRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 450666108499391433L;

    /**
     * Local isntance of the application id for the request.
     */
    private Long applicationId;

    /**
     * Local instance of the opensocial id for the request.
     */
    private String openSocialId;

    /**
     * Default constructor.
     */
    public GetAppDataRequest()
    {
        //default constructor.
    }

    /**
     * Constructor for the Request object.
     * @param inApplicationId - instance of the application id making the request.
     * @param inOpenSocialId - opensocial id of the
     */
    public GetAppDataRequest(final Long inApplicationId, final String inOpenSocialId)
    {
        applicationId = inApplicationId;
        openSocialId = inOpenSocialId;
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
}
