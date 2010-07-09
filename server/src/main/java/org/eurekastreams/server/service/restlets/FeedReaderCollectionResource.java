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
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.FeedReaderUrlCount;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * Restlets used for collections of Feed Readers.
 */
public class FeedReaderCollectionResource extends FeedReaderResource
{

    /**
     * Logger used to log logs.
     */
    private static Log logger = LogFactory.getLog(FeedReaderCollectionResource.class);

    /**
     * Default constructor.
     */
    protected FeedReaderCollectionResource()
    {
        super();
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    }

    /**
     * This is an empty implementation. No params are passed into the FeedReader Collection resource.
     * 
     * @param request
     *            Request from client.
     */
    protected void initParams(final Request request)
    {
    }

    /**
     * returns representation of the top 10 most common feeds for all users.
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

            List<FeedReaderUrlCount> feedReader = entityMapper.findTop10PublicFeeds();

            String stringRep = "";

            JSONArray jsonArray = new JSONArray();

            for (FeedReaderUrlCount fr : feedReader)
            {
                jsonArray.add(convertFeedCountToJSON((FeedReaderUrlCount) fr));
            }

            stringRep += jsonArray.toString();
            Representation rep = new StringRepresentation(stringRep, MediaType.APPLICATION_JSON);
            rep.setExpirationDate(new Date(0L));

            return rep;
        }
        catch (Exception e)
        {
            logger.error("Error when retrieving top 10 public feeds", e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Error when retrieving top 10 public feeds");
        }
    }

    /**
     * Handle POST requests. Responses with representation of the top 10 most common feeds for all friends. This
     * representation is used as a POST because of the limitation GETs have when accepting large variables. A post is
     * used because a list of all of the persons friend OS IDs are sent over the wire. This can not be done via a mapper
     * because this gadget is supposed to be independent of the rest of the framework.
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
            List<FeedReaderUrlCount> feedReader;
            feedReader = entityMapper.findTop10FriendFeeds(entity.getText());
            JSONArray jsonArray = new JSONArray();

            for (FeedReaderUrlCount fr : feedReader)
            {
                jsonArray.add(convertFeedCountToJSON((FeedReaderUrlCount) fr));
            }

            getAdaptedResponse().setEntity(jsonArray.toString(), MediaType.APPLICATION_JSON);
        }
        catch (IOException e)
        {
            logger.error("No Entity sent with request", e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No Entity sent with request");
        }

    }

    /**
     * This method converts a feed to JSON.
     * 
     * @param inFeed
     *            - Feed object.
     * @return - JSONObject representing the FeedReader object.
     */
    protected JSONObject convertFeedCountToJSON(final FeedReaderUrlCount inFeed)
    {
        JSONObject jsonReco = new JSONObject();
        jsonReco.put(TITLE_KEY, inFeed.getFeedTitle());
        jsonReco.put(URL_KEY, inFeed.getUrl());
        jsonReco.put("COUNT", inFeed.getCount());
        return jsonReco;
    }

}
