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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TabType;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.DomainEntityMapperTest;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.GetStreamViewByType;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the create person strategy.
 */
public class PersonCreatorTest extends DomainEntityMapperTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private PersonCreator sut;

    /**
     * Person Mapper Mock.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * Person Mapper Mock.
     */
    private TabMapper tabMapperMock = context.mock(TabMapper.class);

    /**
     * Person Mapper Mock.
     */
    private OrganizationMapper organizationMapperMock = context.mock(OrganizationMapper.class);

    /**
     * for real.
     */
    @Autowired
    private GetStreamViewByType getStreamViewByType;

    /**
     * actual person mapper.
     */
    @Autowired
    private PersonMapper personMapper;
    
    /**
     * actual org mapper.
     */
    @Autowired
    private OrganizationMapper organizationMapper;
    
    /**
     * Actual Tab Mapper.
     */
    @Autowired
    private TabMapper tabMapper;
    
    /**
     * Setup fixtures.
     */
    @Before
    public final void load()
    {
        List<String> startPageTabTypes = new ArrayList<String>(Arrays.asList(TabType.WELCOME));
        sut = new PersonCreator(personMapperMock, tabMapperMock, organizationMapperMock, 
                getStreamViewByType, startPageTabTypes);
    }

    /**
     * Test the get method.
     */
    @Test
    public final void testGetNoOrg()
    {
        final HashMap<String, Serializable> inFields = new HashMap<String, Serializable>();
        inFields.put("accountId", "nflanders");
        inFields.put("firstName", "Ned");
        inFields.put("middleName", "");
        inFields.put("lastName", "Flanders");
        inFields.put("preferredName", "Ned-diddly");

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapperMock).getTabTemplate(TabType.PERSON_ABOUT);
                oneOf(tabMapperMock).getTabTemplate(TabType.WELCOME);
                oneOf(organizationMapperMock).getRootOrganization();
            }
        });

        Person p = sut.get(null, inFields);
        context.assertIsSatisfied();

        assertNotNull(p);
        assertNotNull(p.getProfileTabGroup());
        assertNotNull(p.getStartTabGroup());
        assertEquals("nflanders", p.getAccountId());
        assertEquals("Ned", p.getFirstName());
        assertEquals("", p.getMiddleName());
        assertEquals("Flanders", p.getLastName());
        assertEquals("Ned-diddly", p.getPreferredName());
        assertNotNull(p.getEntityStreamView());
    }

    /**
     * Test getting a person with an org.
     */
    @Test
    public final void testGetWithOrg()
    {
        final HashMap<String, Serializable> inFields = new HashMap<String, Serializable>();

        final Organization orgMock = context.mock(Organization.class);
        inFields.put("accountId", "nflanders");
        inFields.put("firstName", "Ned");
        inFields.put("middleName", "");
        inFields.put("lastName", "Flanders");
        inFields.put("preferredName", "Ned-diddly");
        inFields.put("organization", orgMock);

        context.checking(new Expectations()
        {
            {
                oneOf(tabMapperMock).getTabTemplate(TabType.PERSON_ABOUT);
                oneOf(tabMapperMock).getTabTemplate(TabType.WELCOME);
            }
        });

        Person p = sut.get(null, inFields);
        context.assertIsSatisfied();

        assertNotNull(p);
        assertNotNull(p.getProfileTabGroup());
        assertNotNull(p.getStartTabGroup());
        assertEquals("nflanders", p.getAccountId());
        assertEquals("Ned", p.getFirstName());
        assertEquals("", p.getMiddleName());
        assertEquals("Flanders", p.getLastName());
        assertEquals("Ned-diddly", p.getPreferredName());
        assertEquals(orgMock, p.getParentOrganization());
        assertNotNull(p.getEntityStreamView());

      //Test Order of stream views for new users
        assertEquals(StreamView.Type.PEOPLEFOLLOW, p.getStreamViews().get(0).getType());
        assertEquals(StreamView.Type.PARENTORG, p.getStreamViews().get(1).getType());
        assertEquals(StreamView.Type.EVERYONE, p.getStreamViews().get(2).getType());
        assertEquals(StreamView.Type.STARRED, p.getStreamViews().get(3).getType());

    }

    /**
     * Test the persist method.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testPersist() throws Exception
    {
        final Person testPerson = context.mock(Person.class);
        final Person testPersonResult = context.mock(Person.class, "p2");

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).insert(testPerson);
                exactly(2).of(testPerson).getId();
                will(returnValue(2L));
                oneOf(personMapperMock).addFollower(2L, 2L);
            }
        });

        sut.persist(null, null, testPerson);

        context.assertIsSatisfied();
    }
    
    /**
     * Test the persist method.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public final void testPersistFromDB() throws Exception
    {
        List<String> startPageTabTypes = new ArrayList<String>(Arrays.asList(TabType.WELCOME));
    
        sut = new PersonCreator(personMapper, tabMapper, organizationMapper, getStreamViewByType, startPageTabTypes);

        final Organization org = organizationMapper.getRootOrganization();
        
        final HashMap<String, Serializable> inFields = new HashMap<String, Serializable>();
       
        inFields.put("accountId", "nflanders");
        inFields.put("firstName", "Ned");
        inFields.put("email", "Ned@dude.com");
        inFields.put("middleName", "s");
        inFields.put("lastName", "Flanders");
        inFields.put("preferredName", "Neddiddly");
        inFields.put("organization", org);
        
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("somekey", "somevalue");
        inFields.put("additionalProperties", props);

        final Person testPerson = sut.get(null, inFields);
        
        sut.persist(null, null, testPerson);
        Person testResult = personMapper.findByAccountId("nflanders");
        assertTrue(personMapper.isFollowing(testResult.getAccountId(), testResult.getAccountId()));
        
    }
}
