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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Request;

/**
 * This class represents the OAuth request message for use with restlets.
 */
public class RestletRequestMessage extends OAuthMessage
{
    /**
     * The restlet request.
     */
    private final Request request;

    /**
     * Constructor.
     * 
     * @param inRequest
     *            The restlet request.
     */
    public RestletRequestMessage(final Request inRequest)
    {
        super(inRequest.getMethod().toString(), 
                inRequest.getResourceRef().getBaseRef().toString(), 
                getParameters(inRequest));
        request = inRequest;
        copyHeaders(request, getHeaders());
    }

    /**
     * Copies HTTP headers from restlet request and returns them as OAuth parameters.
     * 
     * @param request
     *            The restlet request.
     * @param into
     *            The Map that will be populated with the retrieved parameters.
     */
    private static void copyHeaders(final Request request, final Collection<Map.Entry<String, String>> into)
    {
        Form requestHeaders = (Form) request.getAttributes().get("org.restlet.http.headers");
        Set<String> names = requestHeaders.getNames();
        if (names != null)
        {
            Iterator<String> itr = names.iterator();
            while (itr.hasNext())
            {
                String name = itr.next();
                String[] values = requestHeaders.getValuesArray(name);
                if (values != null)
                {
                    for (int i = 0; i < values.length; i++)
                    {
                        into.add(new OAuth.Parameter(name, values[i]));
                    }
                }
            }
        }
    }

    /**
     * Gets a list of parameters that are a part of the restlet request.
     * @param request
     *          the restlet request.
     * @return the list of OAuth parameters and any form or querystring parameters.
     */
    public static List<OAuth.Parameter> getParameters(final Request request)
    {
        List<OAuth.Parameter> list = new ArrayList<OAuth.Parameter>();
        Form requestHeaders = (Form) request.getAttributes().get("org.restlet.http.headers");

        String[] values = requestHeaders.getValuesArray("Authorization");
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                String header = values[i];
                for (OAuth.Parameter parameter : OAuthMessage.decodeAuthorization(header))
                {
                    if (!"realm".equalsIgnoreCase(parameter.getKey()))
                    {
                        list.add(parameter);
                    }
                }
            }
        }

        Form form = request.getEntityAsForm();
        for (Parameter parameter : form)
        {
            list.add(new OAuth.Parameter(parameter.getName(), parameter.getValue()));
        }

        Form queryString = request.getResourceRef().getQueryAsForm();
        for (Parameter parameter : queryString)
        {
            list.add(new OAuth.Parameter(parameter.getName(), parameter.getValue()));
        }

        return list;
    }
}
