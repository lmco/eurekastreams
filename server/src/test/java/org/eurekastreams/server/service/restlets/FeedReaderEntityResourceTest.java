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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.FeedReader;
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
public class FeedReaderEntityResourceTest
{

    /**
     * Instance ID.
     */
    private long testId = 0;

    /**
     * Date format for SimpleDateFormat input.
     */
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    /**
     * OS ID.
     */
    private String testOSId = "1234";

    /**
     * module ID.
     */
    private String testModuleId = "45";

    /**
     * feed title.
     */
    private String testFeedTitle = "testTitle";

    /**
     * url ID.
     */
    private String testUrl = "www.google.com";

    /**
     * test Date.
     */
    private String testDate = "12/22/2009";

    /**
     * Subject under test.
     */
    private FeedReaderEntityResource sut;

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
     * Feedreader to share.
     */
    final FeedReader fr = context.mock(FeedReader.class);

    /**
     * Date mock.
     */
    final Date testDateMock = context.mock(Date.class);

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
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new FeedReaderEntityResource();
        sut.setEntityMapper(mapper);
        sut.setAdaptedResponse(adaptedResponse);
    }

    /**
     * Test the remove representation.
     * 
     * @throws ResourceException
     *             Not expected.
     */
    @Test
    public void testRemoveRepresentations() throws ResourceException
    {
        setupCommonInitExpectations();
        setupCommonRepresentExpectations();

        sut.init(restContext, request, response);

        context.checking(new Expectations()
        {
            {
                oneOf(fr).getId();
                will(returnValue(0L));
                oneOf(mapper).delete(0);
                oneOf(mapper).flush();
            }
        });

        sut.removeRepresentations();
        context.assertIsSatisfied();
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

        setupCommonInitExpectations();
        setupCommonRepresentExpectations();

        context.checking(new Expectations()
        {
            {
                oneOf(fr).getId();
                will(returnValue(0L));
                oneOf(fr).getFeedTitle();
                will(returnValue(testFeedTitle));
                oneOf(fr).getUrl();
                will(returnValue(testUrl));
                oneOf(fr).getOpenSocialId();
                will(returnValue(testOSId));
                oneOf(fr).getModuleId();
                will(returnValue(testModuleId));
                oneOf(fr).getDateAdded();
            }
        });

        sut.init(restContext, request, response);

        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be application/json", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        assertNotNull("JSON text isn't right", json);

        context.assertIsSatisfied();
    }

    /**
     * Handle PUT requests.
     * 
     * @throws ResourceException
     *             Not expected.
     * @throws IOException
     *             Not expected.
     * @throws ParseException
     *             Not expected.
     */
    @Test
    public void testStoreRepresentation() throws ResourceException, IOException, ParseException
    {

        final Representation entity = context.mock(Representation.class);
        final String json = buildJSONString();

        context.checking(new Expectations()
        {
            {
                oneOf(entity).getText();
                will(returnValue(json));
                setupCommonRepresentExpectations();
                oneOf(fr).setUrl(testUrl);
                oneOf(fr).setFeedTitle(testFeedTitle);
                oneOf(mapper).flush();

            }
        });

        sut.storeRepresentation(entity);

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

        final Representation entity = context.mock(Representation.class);
        final String json = buildJSONString();

        context.checking(new Expectations()
        {
            {
                oneOf(entity).getText();
                will(returnValue(json));
                oneOf(mapper).insert(with(any(FeedReader.class)));
            }
        });

        sut.acceptRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * This sets up the expectations if represent method is going to be called on sut.
     */
    private void setupCommonRepresentExpectations()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findFeedByOpenSocialIdAndModuleId(testOSId, testModuleId);
                will(returnValue(fr));
            }
        });

    }

    /**
     * This sets up the expectations if init() method is going to be called on sut. This is pulled out so it can be
     * resused independently of restlet type calls.
     */
    private void setupCommonInitExpectations()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("moduleId", "45");
        attributes.put("uuId", "1234");

        context.checking(new Expectations()
        {
            {
                one(request).getAttributes();
                will(returnValue(attributes));
                one(request).getAttributes();
                will(returnValue(attributes));
            }
        });
    }

    /**
     * Build up the JSON string that the storeRepresentation method will interpret.
     * 
     * @return a JSON string representing the input data
     */
    private String buildJSONString()
    {
        JSONObject jsonJob = new JSONObject();
        jsonJob.put(FeedReaderResource.ID_KEY, testId);
        jsonJob.put(FeedReaderResource.TITLE_KEY, testFeedTitle);
        jsonJob.put(FeedReaderResource.USER_ID_KEY, testOSId);
        jsonJob.put(FeedReaderResource.INSTANCE_ID_KEY, testModuleId);
        jsonJob.put(FeedReaderResource.URL_KEY, testUrl);
        jsonJob.put(FeedReaderResource.DATE_KEY, testDate.toString());
        return jsonJob.toString();
    }

}
