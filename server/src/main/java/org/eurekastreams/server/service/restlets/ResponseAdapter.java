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
package org.eurekastreams.server.service.restlets;

import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * This class is an adapter for the Restlet Response object so that
 * it can be properly tested.
 *
 */
public class ResponseAdapter
{
    /**
     * Local instance of the Response object.
     */
    private final Response response;
    
    /**
     * Default constructor for the Response adapter.
     * @param inResponse - instance of the response to adapt.
     */
    public ResponseAdapter(final Response inResponse)
    {
        response = inResponse;
    }
    
    /**
     * Adapter for the Restlet Response.setEntity method.
     * @param inValue - string value of the response to set.
     * @param inMediaType - media type of the response value.
     */
    public void setEntity(final String inValue, final MediaType inMediaType)
    {
        response.setEntity(inValue, inMediaType);
    }
    
    /**
     * Adapter for the Restlet Response.setStatus method.
     * @param inStatus - status to set the current response to.
     */
    public void setStatus(final Status inStatus)
    {
        
        response.setStatus(inStatus);
    }
}
