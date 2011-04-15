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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Test for StreamIdValidationResource.
 * 
 */
public class StreamIdValidationResourceTest
{
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
     * Person model view mapper.
     */
    private final DomainMapper<String, PersonModelView> getPersonMVByAccountId = context.mock(DomainMapper.class);

    /**
     * Groups by shortName DAO.
     */
    private final GetDomainGroupsByShortNames groupByShortNameDAO = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Instance of the {@link PlatformTransactionManager}.
     */
    private final PlatformTransactionManager transManager = context.mock(PlatformTransactionManager.class);

    /**
     * Subject under test.
     */
    private StreamIdValidationResource sut = new StreamIdValidationResource(getPersonMVByAccountId,
            groupByShortNameDAO, transManager);

    /**
     * Person model view mock.
     */
    private PersonModelView pmv = context.mock(PersonModelView.class);

    /**
     * Transaction status mock.
     */
    private TransactionStatus ts = context.mock(TransactionStatus.class);

    /**
     * The mocked web request.
     */
    private Request request = context.mock(Request.class);

    /**
     * The mocked response.
     */
    private Response response = context.mock(Response.class);

    /**
     * The mocked context of the request.
     */
    private Context restContext = context.mock(Context.class);

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

        setupCommonInitExpectations("coolStreamId", "person");

        context.checking(new Expectations()
        {
            {
                allowing(transManager).getTransaction(with(any(TransactionDefinition.class)));
                will(returnValue(ts));

                oneOf(getPersonMVByAccountId).execute("coolStreamId");
                will(returnValue(pmv));

                oneOf(transManager).commit(ts);
            }
        });

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be text/html", MediaType.TEXT_HTML, actual.getMediaType());
        assertTrue(actual.getText().contains("Valid Person"));

        context.assertIsSatisfied();
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
    public void testRepresentNullPerson() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);

        setupCommonInitExpectations("coolStreamId", "person");

        context.checking(new Expectations()
        {
            {
                allowing(transManager).getTransaction(with(any(TransactionDefinition.class)));
                will(returnValue(ts));

                oneOf(getPersonMVByAccountId).execute("coolStreamId");
                will(returnValue(null));

                oneOf(transManager).commit(ts);
            }
        });

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be text/html", MediaType.TEXT_HTML, actual.getMediaType());
        assertTrue(actual.getText().contains("Invalid Person"));

        context.assertIsSatisfied();
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
    public void testRepresentError() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);

        setupCommonInitExpectations("coolStreamId", "person");

        context.checking(new Expectations()
        {
            {
                allowing(transManager).getTransaction(with(any(TransactionDefinition.class)));
                will(returnValue(ts));

                oneOf(getPersonMVByAccountId).execute("coolStreamId");
                will(throwException(new Exception()));

                oneOf(transManager).rollback(ts);
            }
        });

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be text/html", MediaType.TEXT_HTML, actual.getMediaType());
        assertTrue(actual.getText().contains("Invalid Person"));

        context.assertIsSatisfied();
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
    public void testRepresentBadType() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);

        setupCommonInitExpectations("coolStreamId", "resource");

        context.checking(new Expectations()
        {
            {
                allowing(transManager).getTransaction(with(any(TransactionDefinition.class)));
                will(returnValue(ts));

                oneOf(transManager).rollback(ts);
            }
        });

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be text/html", MediaType.TEXT_HTML, actual.getMediaType());
        assertTrue(actual.getText().contains("Invalid Type"));

        context.assertIsSatisfied();
    }

    /**
     * This sets up the expectations if init() method is going to be called on sut. This is pulled out so it can be
     * resused independently of restlet type calls.
     * 
     * @param key
     *            key.
     * @param type
     *            type.
     */
    private void setupCommonInitExpectations(final String key, final String type)
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("uniqueKey", key);
        attributes.put("type", type);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });
    }

}
