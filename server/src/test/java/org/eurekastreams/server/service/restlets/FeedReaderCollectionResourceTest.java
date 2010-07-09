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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.FeedReaderUrlCount;
import org.eurekastreams.server.persistence.FeedReaderMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

/**
 * Test class for BackgroundResource.
 */
@SuppressWarnings("unchecked")
public class FeedReaderCollectionResourceTest
{
    /**
     * test url count.
     */
    private long testCount = 5;

    /**
     * test title.
     */
    private String testFeedTitle = "testTitle";

    /**
     * url ID.
     */
    private String testUrl = "www.google.com";

    /**
     * Subject under test.
     */
    private FeedReaderCollectionResource sut;

    /**
     * FeedReaderUrlCount to share.
     */
    final FeedReaderUrlCount frc = new FeedReaderUrlCount();

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked mapper the SUT will use to look up background data.
     */
    private FeedReaderMapper mapper = context.mock(FeedReaderMapper.class);

    /**
     * The mocked context of the request.
     */
    private Context restContext = context.mock(Context.class);

    /**
     * The mocked web request.
     */
    private Request request = context.mock(Request.class);

    /**
     * The mocked response.
     */
    private Response response = context.mock(Response.class);

    /**
     * Mocked ResponseAdapter.
     */
    private Response adaptedResponse = context.mock(Response.class, "adaptedResponse");

    /**
     * mock list of feedurlcount.
     */
    private List<FeedReaderUrlCount> listOfFRC;

    /**
     * FeedReaderUrlCount Mock.
     */
    private FeedReaderUrlCount feedReaderCount = context.mock(FeedReaderUrlCount.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        listOfFRC = new LinkedList<FeedReaderUrlCount>();
        listOfFRC.add(feedReaderCount);

        sut = new FeedReaderCollectionResource();
        sut.setEntityMapper(mapper);

        frc.setFeedTitle(testFeedTitle);
        frc.setUrl(testUrl);
        frc.setCount(testCount);

        sut.setAdaptedResponse(adaptedResponse);
    }

    /**
     * test GetEntity.
     */
    @Test
    public void testGetEntity()
    {
        FeedReaderMapper result = sut.getEntityMapper();
        assertEquals("Entity mapper doesn't match", mapper, result);
    }

    /**
     * Test the represent method.
     * 
     * @throws ResourceException
     *             Not expected.
     * @throws IOException
     *             Not expected.
     */
    @Test
    public void testRepresent() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findTop10PublicFeeds();
                will(returnValue(listOfFRC));
                oneOf(feedReaderCount).getFeedTitle();
                oneOf(feedReaderCount).getUrl();
                oneOf(feedReaderCount).getCount();
                // will(returnValue(feedReaderCount));
                // oneOf(listOfFRC).add(frc);

            }
        });

        sut.init(restContext, request, response);

        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be application/json", MediaType.APPLICATION_JSON, actual.getMediaType());

        context.assertIsSatisfied();
    }

    /**
     * Handle POST requests.
     * 
     * @throws ResourceException
     *             Not expected.
     * @throws IOException
     *             Not expected.
     * @throws ParseException
     *             Not expected.
     */
    @Test
    public void testAcceptRepresentation() throws ResourceException, IOException, ParseException
    {

        final Representation entity = context.mock(Representation.class, "rep");
        final String json = buildJSONString();

        context.checking(new Expectations()
        {
            {
                oneOf(entity).getText();
                will(returnValue(json));
                oneOf(mapper).findTop10FriendFeeds(json);
                will(returnValue(listOfFRC));
                oneOf(feedReaderCount).getFeedTitle();
                will(returnValue(testFeedTitle));
                oneOf(feedReaderCount).getUrl();
                will(returnValue(testUrl));
                oneOf(feedReaderCount).getCount();
                will(returnValue(testCount));
                oneOf(adaptedResponse).setEntity("[" + buildJSONString() + "]", MediaType.APPLICATION_JSON);
            }
        });

        sut.acceptRepresentation(entity);
        context.assertIsSatisfied();
    }

    /**
     * Test building of Json.
     */
    @Test
    public void testbuildJson()
    {

        sut.convertFeedCountToJSON(frc);
    }

    /**
     * Build up the JSON string that the methods will interpret.
     * 
     * @return a JSON string representing the input data
     */
    private String buildJSONString()
    {
        JSONObject jsonJob = new JSONObject();

        jsonJob.put(FeedReaderResource.TITLE_KEY, testFeedTitle);
        jsonJob.put(FeedReaderResource.URL_KEY, testUrl);
        jsonJob.put("COUNT", testCount);
        return jsonJob.toString();
    }

}
