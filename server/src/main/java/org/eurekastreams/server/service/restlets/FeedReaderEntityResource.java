/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.FeedReader;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * The Resource used for representing and manipulating single feed readers.
 */
public class FeedReaderEntityResource extends FeedReaderResource
{

    /**
     * The log logger for logging logs.
     */
    private static Log logger = LogFactory.getLog(FeedReaderEntityResource.class);

    /**
     * The gadget module Id.
     */
    private String moduleId;

    /**
     * Open SocialId of the user that owns the feed.
     */
    private String userId;

    /**
     * Default constructor.
     */
    protected FeedReaderEntityResource()
    {
        super();
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    /**
     * @param request
     *            Request from client.
     */
    protected void initParams(final Request request)
    {
        moduleId = (String) request.getAttributes().get("moduleId");
        userId = (String) request.getAttributes().get("uuId");
    }

    /**
     * Delete a FeedReader.
     * 
     * @throws ResourceException
     *             error.
     */
    @Override
    public void removeRepresentations() throws ResourceException
    {
        FeedReader myEntity = entityMapper.findFeedByOpenSocialIdAndModuleId(userId, moduleId);
        entityMapper.delete(myEntity.getId());
        entityMapper.flush();
    }

    /**
     * returns representation.
     * 
     * @param variant
     *            type of thingo.
     * 
     * @return representation of element.
     * @throws ResourceException
     *             if there is a problem with the resource.
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        try
        {
            FeedReader feedReader = entityMapper.findFeedByOpenSocialIdAndModuleId(userId, moduleId);

            String stringRep;

            JSONObject json = new JSONObject();

            json = convertFeedToJSON(feedReader);
            stringRep = json.toString();

            Representation rep = new StringRepresentation(stringRep, MediaType.APPLICATION_JSON);

            rep.setExpirationDate(new Date(0L));

            return rep;
        }
        catch (Exception e)
        {
            logger.error("Error when retrieving feed", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error when retrieving feed");
        }
    }

    /**
     * Handle PUT requests.
     * 
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {

        try
        {
            JSONObject jsonObject = JSONObject.fromObject(entity.getText());
            FeedReader feedReader = convertJSONObjectToFeed(jsonObject);
            FeedReader toUpdate = entityMapper.findFeedByOpenSocialIdAndModuleId(feedReader.getOpenSocialId(),
                    feedReader.getModuleId());
            toUpdate.setUrl(feedReader.getUrl());
            toUpdate.setFeedTitle(feedReader.getFeedTitle());
            entityMapper.flush();
        }
        catch (IOException e)
        {
            logger.error("No Entity sent with request", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No Entity sent with request");
        }
        catch (ParseException e)
        {
            logger.error("Unable to Parse Json of entity", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unable to Parse Json of entity");
        }

    }

    /**
     * Handle POST requests to create new entity.
     * 
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException
    {
        try
        {
            JSONObject jsonObject = JSONObject.fromObject(entity.getText());
            FeedReader feedReader = convertJSONObjectToFeed(jsonObject);
            entityMapper.insert(feedReader);
        }
        catch (IOException e)
        {
            logger.error("No Entity sent with request", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No Entity sent with request");
        }
        catch (ParseException e)
        {
            logger.error("Unable to Parse Json of entity", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unable to Parse Json of entity");
        }

    }

}
