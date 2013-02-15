/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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

import org.eurekastreams.server.action.response.settings.PersonPropertiesResponse;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.DomainEntityMapperTest;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.eurekastreams.server.persistence.mappers.db.GetReadOnlyStreamsDbMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jsecurity.util.CollectionUtils;
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
     * List of the names of readonly streams to add to a person, in order.
     */
    private List<String> readOnlyStreamsNameList;

    /**
     * System under test.
     */
    private PersonCreator sut;

    /**
     * Person Mapper Mock.
     */
    private final PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * Person Mapper Mock.
     */
    private final TabMapper tabMapperMock = context.mock(TabMapper.class);

    /**
     * {@link PersonPropertiesGenerator}.
     */
    private PersonPropertiesGenerator ppg = context.mock(PersonPropertiesGenerator.class, "ppg");

    /**
     * {@link PersonPropertiesResponse}.
     */
    private PersonPropertiesResponse ppr = context.mock(PersonPropertiesResponse.class, "ppr");

    /**
     * actual person mapper.
     */
    @Autowired
    private PersonMapper personMapper;

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
        GetReadOnlyStreamsDbMapper readonlyStreamsMapper = new GetReadOnlyStreamsDbMapper();
        (readonlyStreamsMapper).setEntityManager(getEntityManager());

        List<String> streamNames = new ArrayList<String>(CollectionUtils.asList("Everyone", "My saved items",
                "Following"));

        sut = new PersonCreator(personMapperMock, readonlyStreamsMapper, streamNames, ppg);
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
        inFields.put("displayNameSuffix", null);

        context.checking(new Expectations()
        {
            {
                oneOf(ppg).getPersonProperties(inFields);
                will(returnValue(ppr));

                oneOf(ppr).getTabTemplates();
                will(returnValue(new ArrayList<TabTemplate>(//
                        Arrays.asList(new TabTemplate("welcome", Layout.ONECOLUMN)))));

                oneOf(ppr).getTheme();
                will(returnValue(null));
            }
        });

        Person p = sut.get(null, inFields);
        context.assertIsSatisfied();

        assertNotNull(p);
        assertNotNull(p.getStartTabGroup());
        assertEquals("nflanders", p.getAccountId());
        assertEquals("Ned", p.getFirstName());
        assertEquals("", p.getMiddleName());
        assertEquals("Flanders", p.getLastName());
        assertEquals("Ned-diddly", p.getPreferredName());
        assertEquals("", p.getDisplayNameSuffix());

        // test stream order
        // IDs: 1, 5, 3
        assertEquals(3, p.getStreams().size());
        assertEquals(1, p.getStreams().get(0).getId());
        assertEquals(5, p.getStreams().get(1).getId());
        assertEquals(3, p.getStreams().get(2).getId());
    }

    /**
     * Test getting a person with an org and a displayNameSuffix.
     */
    @Test
    public final void testGetWithOrgAndDisplayNameSuffix()
    {
        final HashMap<String, Serializable> inFields = new HashMap<String, Serializable>();

        inFields.put("accountId", "nflanders");
        inFields.put("firstName", "Ned");
        inFields.put("middleName", "");
        inFields.put("lastName", "Flanders");
        inFields.put("preferredName", "Ned-diddly");
        inFields.put("displayNameSuffix", " SUFFIX");

        context.checking(new Expectations()
        {
            {
                oneOf(ppg).getPersonProperties(inFields);
                will(returnValue(ppr));

                oneOf(ppr).getTabTemplates();
                will(returnValue(new ArrayList<TabTemplate>(//
                        Arrays.asList(new TabTemplate("welcome", Layout.ONECOLUMN)))));

                oneOf(ppr).getTheme();
                will(returnValue(null));
            }
        });

        // new SUT to test different stream order
        GetReadOnlyStreamsDbMapper readonlyStreamsMapper = new GetReadOnlyStreamsDbMapper();
        (readonlyStreamsMapper).setEntityManager(getEntityManager());
        List<String> streamNames = new ArrayList<String>(CollectionUtils.asList("My saved items", "Everyone"));

        PersonCreator localSut = new PersonCreator(personMapperMock, readonlyStreamsMapper, streamNames, ppg);

        Person p = localSut.get(null, inFields);
        context.assertIsSatisfied();

        assertNotNull(p);
        assertNotNull(p.getStartTabGroup());
        assertEquals("nflanders", p.getAccountId());
        assertEquals("Ned", p.getFirstName());
        assertEquals("", p.getMiddleName());
        assertEquals("Flanders", p.getLastName());
        assertEquals("Ned-diddly", p.getPreferredName());
        assertEquals(" SUFFIX", p.getDisplayNameSuffix());

        // test stream order
        // IDs: 1, 5,
        assertEquals(2, p.getStreams().size());
        assertEquals(5, p.getStreams().get(0).getId());
        assertEquals(1, p.getStreams().get(1).getId());
    }

    /**
     * Test getting a person with an org and a displayNameSuffix.
     */
    @Test
    public final void testGetWithOrgNoDisplayNameSuffix()
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
                oneOf(ppg).getPersonProperties(inFields);
                will(returnValue(ppr));

                oneOf(ppr).getTabTemplates();
                will(returnValue(new ArrayList<TabTemplate>(//
                        Arrays.asList(new TabTemplate("welcome", Layout.ONECOLUMN)))));

                oneOf(ppr).getTheme();
                will(returnValue(null));
            }
        });

        // new SUT to test different stream order
        GetReadOnlyStreamsDbMapper readonlyStreamsMapper = new GetReadOnlyStreamsDbMapper();
        (readonlyStreamsMapper).setEntityManager(getEntityManager());
        List<String> streamNames = new ArrayList<String>(CollectionUtils.asList("My saved items", "Everyone"));

        PersonCreator localSut = new PersonCreator(personMapperMock, readonlyStreamsMapper, streamNames, ppg);

        Person p = localSut.get(null, inFields);
        context.assertIsSatisfied();

        assertNotNull(p);
        assertNotNull(p.getStartTabGroup());
        assertEquals("nflanders", p.getAccountId());
        assertEquals("Ned", p.getFirstName());
        assertEquals("", p.getMiddleName());
        assertEquals("Flanders", p.getLastName());
        assertEquals("Ned-diddly", p.getPreferredName());
        assertEquals("", p.getDisplayNameSuffix());

        // test stream order
        // IDs: 1, 5,
        assertEquals(2, p.getStreams().size());
        assertEquals(5, p.getStreams().get(0).getId());
        assertEquals(1, p.getStreams().get(1).getId());
    }

    /**
     * Test getting a person with an org and a null displayNameSuffix.
     */
    @Test
    public final void testGetWithOrgAndNullDisplayNameSuffix()
    {
        final HashMap<String, Serializable> inFields = new HashMap<String, Serializable>();

        inFields.put("accountId", "nflanders");
        inFields.put("firstName", "Ned");
        inFields.put("middleName", "");
        inFields.put("lastName", "Flanders");
        inFields.put("preferredName", "Ned-diddly");
        inFields.put("displayNameSuffix", null);

        context.checking(new Expectations()
        {
            {
                oneOf(ppg).getPersonProperties(inFields);
                will(returnValue(ppr));

                oneOf(ppr).getTabTemplates();
                will(returnValue(new ArrayList<TabTemplate>(//
                        Arrays.asList(new TabTemplate("welcome", Layout.ONECOLUMN)))));

                oneOf(ppr).getTheme();
                will(returnValue(null));
            }
        });

        // new SUT to test different stream order
        GetReadOnlyStreamsDbMapper readonlyStreamsMapper = new GetReadOnlyStreamsDbMapper();
        (readonlyStreamsMapper).setEntityManager(getEntityManager());
        List<String> streamNames = new ArrayList<String>(CollectionUtils.asList("My saved items", "Everyone"));

        PersonCreator localSut = new PersonCreator(personMapperMock, readonlyStreamsMapper, streamNames, ppg);

        Person p = localSut.get(null, inFields);
        context.assertIsSatisfied();

        assertNotNull(p);
        assertNotNull(p.getStartTabGroup());
        assertEquals("nflanders", p.getAccountId());
        assertEquals("Ned", p.getFirstName());
        assertEquals("", p.getMiddleName());
        assertEquals("Flanders", p.getLastName());
        assertEquals("Ned-diddly", p.getPreferredName());
        assertEquals("", p.getDisplayNameSuffix());

        // test stream order
        // IDs: 1, 5,
        assertEquals(2, p.getStreams().size());
        assertEquals(5, p.getStreams().get(0).getId());
        assertEquals(1, p.getStreams().get(1).getId());
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
        final StreamScope streamScope = context.mock(StreamScope.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapperMock).insert(testPerson);
                exactly(3).of(testPerson).getId();
                will(returnValue(2L));
                oneOf(personMapperMock).addFollower(2L, 2L);

                oneOf(testPerson).getStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).setDestinationEntityId(2L);
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
        GetReadOnlyStreamsDbMapper readonlyStreamsMapper = new GetReadOnlyStreamsDbMapper();
        (readonlyStreamsMapper).setEntityManager(getEntityManager());
        List<String> streamNames = new ArrayList<String>(CollectionUtils.asList("My saved items", "Everyone"));

        sut = new PersonCreator(personMapper, readonlyStreamsMapper, streamNames, ppg);

        final HashMap<String, Serializable> inFields = new HashMap<String, Serializable>();

        inFields.put("accountId", "nflanders");
        inFields.put("firstName", "Ned");
        inFields.put("email", "Ned@dude.com");
        inFields.put("middleName", "s");
        inFields.put("lastName", "Flanders");
        inFields.put("preferredName", "Neddiddly");
        inFields.put("displayNameSuffix", " SUFFIX");

        HashMap<String, String> props = new HashMap<String, String>();
        props.put("somekey", "somevalue");
        inFields.put("additionalProperties", props);

        context.checking(new Expectations()
        {
            {
                oneOf(ppg).getPersonProperties(inFields);
                will(returnValue(ppr));

                oneOf(ppr).getTabTemplates();
                will(returnValue(new ArrayList<TabTemplate>(//
                        Arrays.asList(new TabTemplate("welcome", Layout.ONECOLUMN)))));

                oneOf(ppr).getTheme();
                will(returnValue(null));
            }
        });

        final Person testPerson = sut.get(null, inFields);

        sut.persist(null, null, testPerson);
        Person testResult = personMapper.findByAccountId("nflanders");
        assertTrue(personMapper.isFollowing(testResult.getAccountId(), testResult.getAccountId()));

    }
}
