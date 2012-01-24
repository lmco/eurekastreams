/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.Date;

import org.restlet.data.Cookie;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * Endpoint for the API (invoking actions).
 */
public class EstablishSessionResource extends SmpResource
{
    /**
     * Session cookie name.
     */
    private String sessionCookieName;

    /**
     * Session ID.
     */
    private String sessionId = null;

    /**
     * Constructor.
     * 
     * @param inSessionCookieName
     *            session cookie name.
     */
    public EstablishSessionResource(final String inSessionCookieName)
    {
        sessionCookieName = inSessionCookieName;
    }

    /**
     * GET the session.
     * 
     * @param variant
     *            the variant.
     * @return the JSON.
     * @throws ResourceException
     *             the exception.
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        Representation rep = new StringRepresentation(sessionId, MediaType.TEXT_PLAIN);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

    @Override
    protected void initParams(final Request request)
    {
        Cookie cookie = request.getCookies().getFirst("JSESSIONID", true);

        if (cookie != null)
        {
            sessionId = cookie.getValue();
        }
        else
        {
            sessionId = "NULL";
        }
    }
}
