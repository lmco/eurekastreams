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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.eurekastreams.server.persistence.JobMapper;

/**
 * Test class for CompaniesCollectionResource. 
 */
public class CompaniesCollectionResourceTest
{
    /**
     * Subject under test.
     */
    private CompaniesCollectionResource sut;

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
     * Mocked request.
     */
    private Request request = context.mock(Request.class);

    /**
     * Mocked job mapper.
     */
    private JobMapper jobMapper = context.mock(JobMapper.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("query", (Object) "abc");

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new CompaniesCollectionResource();
        sut.setJobMapper(jobMapper);
        sut.initParams(request);
    }

    /**
     * Test the GET method.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     */
    @Test
    public void represent() throws ResourceException, IOException
    {
        Variant variant = context.mock(Variant.class);
        
        String prefix = "abc";

        setupExpectations(prefix, CompaniesCollectionResource.MAX_COMPANIES);

        Representation actual = sut.represent(variant);

        JSONObject json = JSONObject.fromObject(actual.getText());
        JSONArray companies = json.getJSONArray(CompaniesCollectionResource.COMPANIES_KEY);

        assertEquals(CompaniesCollectionResource.MAX_COMPANIES, companies.size());
        
        for (int i = 0; i < companies.size(); i++)
        {
            assertEquals(prefix, companies.getString(i).substring(0, prefix.length()));
        }
        
        context.assertIsSatisfied();
    }

    /**
     * Utility method to set up some results from the mapper.
     * 
     * @param prefix
     *            the prefix to search for
     * @param limit
     *            the maximum number of results
     */
    private void setupExpectations(final String prefix, final int limit)
    {
        final List<String> companies = new ArrayList<String>();
        for (int i = 0; i < limit; i++)
        {
            companies.add(prefix + String.valueOf(i));
        }
        
        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).findCompaniesByPrefix(prefix, limit);
                will(returnValue(companies));
            }
        });
        
    }
}
