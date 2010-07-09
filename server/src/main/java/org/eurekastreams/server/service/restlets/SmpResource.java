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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;

/**
 * REST abstract endpoint class for all SMP resources.
 */
public abstract class SmpResource extends Resource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(SmpResource.class);
    
    /**
     * Response adapter to make Responses from Restlet testable.
     */
    private ResponseAdapter adaptedResponse = null;


    /**
     * Default constructor. 
     */
    protected SmpResource()
    {
        super();
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    }

    /**
     * This gets paired with the default constructor. Now that this is built with Spring, the other constructor doesn't
     * get called anymore.
     * 
     * @param context
     *            the context of the request
     * @param request
     *            the client's request
     * @param response
     *            the response object
     */
    public void init(final Context context, final Request request, final Response response)
    {
        this.setRequest(request);
        this.setContext(context);
        this.setResponse(response);
        
        setAdaptedResponse(new ResponseAdapter(response));
        
        initParams(request);
    }
    
    /**
     * This method wraps the Restlet Response so that it is easier to test.
     * @return current instance of the Response.
     */
    protected ResponseAdapter getAdaptedResponse()
    {
        return adaptedResponse;
    }
    
    /**
     * This method sets the AdaptedResponse.    
     * @param inResponse - instance of ResponseAdapter to use for responses.
     */
    protected void setAdaptedResponse(final ResponseAdapter inResponse)
    {
        adaptedResponse = inResponse;
    }
    
    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected abstract void initParams(final Request request);

}
