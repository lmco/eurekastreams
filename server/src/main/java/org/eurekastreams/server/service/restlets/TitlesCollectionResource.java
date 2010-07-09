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
package org.eurekastreams.server.service.restlets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.persistence.JobMapper;

/**
 * Resource to provide a list of job titles based on a specified prefix.
 */
public class TitlesCollectionResource extends SmpResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(TitlesCollectionResource.class);
    
    /**
     * Key for the titles array in the JSON results.
     */
    public static final String TITLES_KEY = "titles";

    /**
     * The title prefix to look for.
     */
    private String prefix;

    /**
     * The maximum number of titles to return.
     */
    public static final int MAX_TITLES = 10;

    /**
     * Mapper used to look up the titles.
     */
    private JobMapper jobMapper;

    /**
     * 
     */
    public TitlesCollectionResource()
    {
    }

    /**
     * Get the prefix to search against.
     * 
     * @param request
     *            the restlet request object
     */
    @Override
    protected void initParams(final Request request)
    {
        //Attempt to decode using W3C standard encoding, if failure, 
        //try to retrieve string and pass through without decoding.
        try
        {
            prefix = URLDecoder.decode((String) request.getAttributes().get("query"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            prefix = (String) request.getAttributes().get("query");
            log.error("Unsupported encoding on input for: " + prefix);
        }
    }

    /**
     * Getter.
     * 
     * @return the jobMapper
     */
    public JobMapper getJobMapper()
    {
        return jobMapper;
    }

    /**
     * Setter.
     * 
     * @param inJobMapper
     *            the jobMapper to set
     */
    public void setJobMapper(final JobMapper inJobMapper)
    {
        this.jobMapper = inJobMapper;
    }

    /**
     * Handle GET request.
     * 
     * @param variant
     *            for the available representations for the resource
     * @throws ResourceException
     *             should not occur
     * @return the JSON representation of this resource
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        log.debug("Searching for job titles by prefix: " + prefix);
        List<String> titles = jobMapper.findTitlesByPrefix(prefix, MAX_TITLES);

        JSONArray jsonArray = new JSONArray();
        if (null != titles)
        {
            for (String title : titles)
            {
                jsonArray.add(title);
            }
        }
        JSONObject json = new JSONObject();
        json.put(TITLES_KEY, jsonArray);
        log.debug("Returning titles found: " + json.toString());
        Representation rep = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

}
