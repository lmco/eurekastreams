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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.FeedReader;
import org.eurekastreams.server.persistence.FeedReaderMapper;
import org.restlet.data.Request;

/**
 * Abstract class that contains the commonalities between the Entry and Collection endpoints for Feed Readers.
 * 
 */
public abstract class FeedReaderResource extends FeedReaderWritableResource
{
    /**
     * Mapper for getting recommendations.
     */
    FeedReaderMapper entityMapper;

    /**
     * The JSON objects key.
     */
    public static final String INSTANCE_ID_KEY = "moduleId";

    /**
     * JSON field for feed title.
     */
    public static final String TITLE_KEY = "feedTitle";
    
    /**
     * JSON field for user id.
     */
    public static final String USER_ID_KEY = "openSocialId";

    /**
     * JSON field for the recommendation text.
     */
    public static final String URL_KEY = "url";

    /**
     * JSON field for the date.
     */
    public static final String DATE_KEY = "date";

    /**
     * Date format for SimpleDateFormat input.
     */
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    /**
     * JSON field for the id.
     */
    public static final String ID_KEY = "id";

    /**
     * Pass this abstract method onto subclasses for implementations.
     * 
     * @param request
     *            - request that is handled by the restlet.
     */
    protected abstract void initParams(Request request);

    /**
     * Getter for the recommendation mapper.
     * 
     * @return recommendation mapper.
     */
    public FeedReaderMapper getEntityMapper()
    {
        return entityMapper;
    }

    /**
     * sets Entity mapper.
     * 
     * @param inEntityMapper
     *            mapper.
     */
    public void setEntityMapper(final FeedReaderMapper inEntityMapper)
    {
        entityMapper = inEntityMapper;
    }

    /**
     * This method converts a feed to JSON.
     * 
     * @param inFeed
     *            - Feed object.
     * @return - JSONObject representing the FeedReader object.
     */
    protected JSONObject convertFeedToJSON(final FeedReader inFeed)
    {
        
        JSONObject jsonReco = new JSONObject();
        
        if (inFeed != null)
        {
            jsonReco.put(ID_KEY, inFeed.getId());
            jsonReco.put(TITLE_KEY, inFeed.getFeedTitle());
            jsonReco.put(USER_ID_KEY, inFeed.getOpenSocialId());
            jsonReco.put(INSTANCE_ID_KEY, inFeed.getModuleId());
            jsonReco.put(URL_KEY, inFeed.getUrl());
            String date = inFeed.getDateAdded().toString();
            jsonReco.put(DATE_KEY, date);
        }
        return jsonReco;
    }

    /**
     * Converts a JSONObject into a FeedReader object.
     * 
     * @param inJsonFeedReader
     *            - json representation of a FeedReader to try and parse.
     * @return - FeedReader object.
     * @throws ParseException
     *             - if a dateformat parsing error occurs.
     */
    protected FeedReader convertJSONObjectToFeed(final JSONObject inJsonFeedReader) throws ParseException
    {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);

        String id = inJsonFeedReader.getString(ID_KEY);
        String feedTitle = inJsonFeedReader.getString(TITLE_KEY);
        String userId = inJsonFeedReader.getString(USER_ID_KEY);
        String moduleId = inJsonFeedReader.getString(INSTANCE_ID_KEY);
        String url = inJsonFeedReader.getString(URL_KEY);
        String dateAddedString = inJsonFeedReader.getString(DATE_KEY);
        Date dateAdded = df.parse(dateAddedString);

        FeedReader feed = new FeedReader();
        feed.setId(Long.parseLong(id));
        feed.setFeedTitle(feedTitle);
        feed.setOpenSocialId(userId);
        feed.setModuleId(moduleId);
        feed.setUrl(url);
        feed.setDateAdded(dateAdded);

        return feed;
    }

}
