/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Person.
 *
 * Note: for tabGroups testing, tab groups are named after electron shells 1s, 2s, 2p, 3s, 3p, 3d
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class PersonTest
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
     * StreamScope mock.
     */
    private final StreamScope streamScopeMock = context.mock(StreamScope.class);

    /**
     * test fixture.
     */
    private Person sut;

    /**
     * test fixture.
     */
    private String message;

    /**
     * Last accepted terms of service date.
     */
    private final Date lastAcceptedTos = new Date();

    /**
     * Bookmarks.
     */
    private List<StreamScope> bookmarks;

    /**
     *
     */
    @Before
    public final void setup()
    {
        final HashSet<Long> optoutvideos = new HashSet<Long>();
        optoutvideos.add(5L);

        sut = new Person("homers", "homer", "jay", "simpson", "MaxPower");
        sut.setOptOutVideos(optoutvideos);
        sut.setTitle("engineer");
        sut.setJobDescription("I am so smart, S M R T");
        sut.setWorkPhone("6105551212");
        sut.setEmail("homer@gmail.com");
        sut.setLastAcceptedTermsOfService(lastAcceptedTos);
        sut.setBookmarks(bookmarks);
        message = null;
    }

    /**
     * Test teardown.
     */
    @After
    public final void teardown()
    {
        Person.setEntityCacheUpdater(null);
    }

    /**
     * Test for creation of person object from model view.
     */
    @Test
    public void testCreateFromModelView()
    {
        Date aDate = new Date(new Date().getTime() - 9 * 9 * 9);
        PersonModelView modelView = new PersonModelView();
        HashSet<Long> optoutvideos = new HashSet<Long>();
        optoutvideos.add(5L);
        modelView.setOptOutVideos(optoutvideos);
        modelView.setAccountId("test");
        modelView.setOpenSocialId("opensocial");
        modelView.setAvatarId("avatarId");
        modelView.setEntityId(7L);
        modelView.setEmail("user@domain.com");
        modelView.setDateAdded(aDate);
        modelView.setLastName("Doe");
        modelView.setPreferredName("John");

        sut = new Person(modelView);

        assertEquals("test", sut.getAccountId());
        assertEquals("opensocial", sut.getOpenSocialId());
        assertEquals("avatarId", sut.getAvatarId());
        assertEquals(7L, sut.getId());
        assertEquals("user@domain.com", sut.getEmail());
        assertEquals(optoutvideos, sut.getOptOutVideos());
        assertEquals(aDate, sut.getDateAdded());
        assertEquals("Doe", sut.getLastName());
        assertEquals("John", sut.getPreferredName());
        assertEquals(bookmarks, sut.getBookmarks());
        assertEquals(EntityType.PERSON, sut.getEntityType());
    }

    /**
     * Tests creating a PersonModelView from a Person.
     */
    @Test
    public void testToPersonModelView()
    {
        Date aDate = new Date(new Date().getTime() - 9 * 9 * 9);

        sut.setOpenSocialId("opensocial");
        sut.setAvatarId("avatarId");
        sut.setId(7L);
        sut.setEmail("user@domain.com");
        sut.setDateAdded(aDate);

        PersonModelView mv = sut.toPersonModelView();

        assertEquals(7L, mv.getId());
        assertEquals("avatarId", mv.getAvatarId());
        assertEquals("homers", mv.getAccountId());
        assertEquals("opensocial", mv.getOpenSocialId());

        assertEquals("MaxPower", mv.getPreferredName());
        assertEquals("simpson", mv.getLastName());
        assertEquals("MaxPower simpson", mv.getDisplayName());
        // TODO: assertEquals(, mv.getFollowersCount());
        assertEquals("engineer", mv.getTitle());
        assertEquals("user@domain.com", mv.getEmail());
        assertEquals(aDate, mv.getDateAdded());
        assertEquals(EntityType.PERSON, sut.getEntityType());
        assertEquals(7L, sut.getEntityId());
        assertEquals(bookmarks, sut.getBookmarks());

        assertEquals(1, mv.getOptOutVideos().size());
        assertEquals((Long) 5L, mv.getOptOutVideos().iterator().next());
    }

    /**
     * Test getDisplayName().
     */
    @Test
    public void testGetDisplayName()
    {
        sut = new Person("abcdefg", "Jim", "D", "Bar", "Foo");
        assertEquals("Foo Bar", sut.getDisplayName());
    }

    /**
     * Test that when you change the preferred name, the display name updates.
     */
    @Test
    public void testChangingPreferredNameUpdatesDisplayName()
    {
        sut = new Person("abcdefg", "Jim", "D", "Bar", "Foo");
        assertEquals("Foo Bar", sut.getDisplayName());

        sut.setPreferredName("FOOOOOO");
        assertEquals("FOOOOOO Bar", sut.getDisplayName());
    }

    /**
     * Test open social id field.
     */
    @Test
    public final void testOSIDField()
    {
        message = "openSocialID should be appropriately get and set";
        String openSocialId = "1234567890";

        Assert.assertNotSame(message, openSocialId, sut.getOpenSocialId());
        sut.setOpenSocialId(openSocialId);
        Assert.assertEquals(message, openSocialId, sut.getOpenSocialId());
    }

    /**
     * Constructor test for firstName parameter.
     */
    @Test
    public final void testConstructorFirstName()
    {
        assertEquals("First name passed into constructor not returned by getFirstName()", "homer", sut.getFirstName());
    }

    /**
     * Test getters/setters for simple string properties.
     */
    @Test
    public final void testBasicProperties()
    {
        message = " setter/getter not functioning correctly";

        String title = "title";
        String jobDescription = "jobDescription";
        String email = "email@example.com";
        String workPhone = "6666666666";
        String cellPhone = "5555555555";
        String faxNumber = "4444444444";
        String avatar = "abc";
        String overview = "my overview";
        Date dateAdded = new Date();
        final int updatesCount = 23832;
        ArrayList<String> source = new ArrayList<String>(Arrays.asList("blah"));

        Integer avatarCropX = 1;
        Integer avatarCropY = 2;
        Integer avatarCropSize = 3;

        sut.setTitle(title);
        sut.setJobDescription(jobDescription);
        sut.setEmail(email);
        sut.setWorkPhone(workPhone);
        sut.setCellPhone(cellPhone);
        sut.setFax(faxNumber);
        sut.setAvatarId(avatar);
        sut.setAvatarCropX(avatarCropX);
        sut.setAvatarCropY(avatarCropY);
        sut.setAvatarCropSize(avatarCropSize);
        sut.setOverview(overview);
        sut.setDateAdded(dateAdded);
        sut.setUpdatesCount(updatesCount);
        sut.setGroupCount(3);
        sut.setLastAcceptedTermsOfService(lastAcceptedTos);
        sut.setStreamScope(streamScopeMock);
        sut.setSourceList(source);
        sut.setBookmarks(bookmarks);

        assertTrue(0 == sut.getStreamViewHiddenLineIndex());
        assertTrue(0 == sut.getGroupStreamHiddenLineIndex());

        sut.setStreamViewHiddenLineIndex(updatesCount);
        sut.setGroupStreamHiddenLineIndex(updatesCount);

        assertTrue(updatesCount == sut.getStreamViewHiddenLineIndex());
        assertTrue(updatesCount == sut.getGroupStreamHiddenLineIndex());

        assertEquals("title" + message, title, sut.getTitle());
        assertEquals("jobDescription" + message, jobDescription, sut.getJobDescription());
        assertEquals("email" + message, email, sut.getEmail());
        assertEquals("workPhone" + message, workPhone, sut.getWorkPhone());
        assertEquals("cellPhone" + message, cellPhone, sut.getCellPhone());
        assertEquals("faxNumber" + message, faxNumber, sut.getFax());
        assertEquals("avatar" + message, avatar, sut.getAvatarId());
        assertEquals(0, sut.getFollowersCount());
        assertEquals(0, sut.getFollowingCount());
        assertEquals(avatarCropX, sut.getAvatarCropX());
        assertEquals(avatarCropY, sut.getAvatarCropY());
        assertEquals(avatarCropSize, sut.getAvatarCropSize());
        assertEquals(overview, sut.getOverview());
        assertEquals(dateAdded, sut.getDateAdded());
        assertEquals(updatesCount, sut.getUpdatesCount());
        assertEquals(3, sut.getGroupCount());
        assertEquals(lastAcceptedTos, sut.getLastAcceptedTermsOfService());
        assertEquals(bookmarks, sut.getBookmarks());


        // verify getProperties() method.
        HashMap<String, Serializable> props = sut.getProperties();
        assertEquals("homers", (String) props.get("accountId"));
        assertEquals("homer", (String) props.get("firstName"));
        assertEquals("jay", (String) props.get("middleName"));
        assertEquals("simpson", (String) props.get("lastName"));
        assertEquals("MaxPower", (String) props.get("preferredName"));
        assertEquals(email, (String) props.get("email"));
        assertEquals(workPhone, (String) props.get("workPhone"));
        assertEquals(faxNumber, (String) props.get("fax"));
        assertEquals(cellPhone, (String) props.get("cellPhone"));
        assertEquals(jobDescription, (String) props.get("jobDescription"));
        assertEquals(title, (String) props.get("title"));
        assertEquals(streamScopeMock, sut.getStreamScope());
        assertEquals(source, sut.getSourceList());

        // verify that null values are dropped.
        sut.setTitle(null);
        props = sut.getProperties();
        assertFalse(props.containsKey("title"));

        // verify that empty strings are dropped.
        sut.setJobDescription("");
        props = sut.getProperties();
        assertFalse(props.containsKey("jobDescription"));

        sut.setStreamPostable(false);
        assertFalse(sut.isStreamPostable());

        sut.setCommentable(false);
        assertFalse(sut.isCommentable());

        sut.setAccountLocked(true);
        assertTrue(sut.isAccountLocked());
        sut.setAccountLocked(false);
        assertFalse(sut.isAccountLocked());

        sut.setAccountDeactivated(true);
        assertTrue(sut.isAccountDeactivated());
        sut.setAccountDeactivated(false);
        assertFalse(sut.isAccountDeactivated());
    }

    /**
     * Constructor test for lastName parameter.
     */
    @Test
    public final void testContructorLastName()
    {
        assertEquals("Last name passed into constructor not returned by getLastName()", "simpson", sut.getLastName());

    }

    /**
     * Constructor test for middleName parameter.
     */
    @Test
    public final void testContructorMiddleName()
    {
        assertEquals("Middle name passed into constructor not returned by getMiddleName()", "jay", sut.getMiddleName());

    }

    /**
     * Constructor test for accountId parameter.
     */
    @Test
    public final void testContructorAccountId()
    {
        assertEquals("AccountId passed into constructor not returned by getAccountId()", "homers", sut.getAccountId());
    }

    /**
     * Constructor test for preferredName parameter.
     */
    @Test
    public final void testContructorWithPreferredName()
    {
        assertEquals("Preferred name passed into constructor not returned by getPreferredName()", "MaxPower", sut
                .getPreferredName());
    }

    /**
     * Constructor test for null preferredName parameter (should replace with firstName parameter).
     */
    @Test
    public final void testConstructorWithNullPreferredName()
    {
        Person homer = new Person("homers", "homer", "jay", "simpson", null);
        assertEquals("Constructor not converting <null> preferred name to first name", "homer", homer
                .getPreferredName());
    }

    /**
     * Constructor test for empty preferredName parameter (should replace with firstName parameter).
     */
    @Test
    public final void testConstructorWithEmptyPreferredName()
    {
        Person homer = new Person("homers", "homer", "jay", "simpson", " ");
        assertEquals("Constructor not converting empty preferred " + "name to first name", "homer", homer
                .getPreferredName());
    }

    /**
     * jobDescription too long
     *
     * hibernate validation.
     */
    @Test
    public void testValidationJobDescription()
    {
        message = "validation on Person properties";

        // jobDescription too long
        char[] jobDescriptionChars = new char[Person.MAX_JOB_DESCRIPTION_LENGTH + 1];
        Arrays.fill(jobDescriptionChars, 'a');
        StringBuffer buffer = new StringBuffer();
        buffer.append(jobDescriptionChars);
        sut.setJobDescription(buffer.toString());
        ClassValidator<Person> validator = new ClassValidator<Person>(Person.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);
        assertEquals(message, 1, invalidValues.length);
    }

    /**
     * test phone numbers
     *
     * hibernate validation.
     */
    @Test
    public void testValidationPhoneNumbers()
    {
        // optional fields can be set to null (NOT empty string)
        corePhoneNumberTest(null, true);

        // check that reasonable fields work
        corePhoneNumberTest("800-555-1212", true);
        corePhoneNumberTest("800-555-1212 x1111", true);
        corePhoneNumberTest("+44 1865 123456", true);

        // check for too long
        String tooLong = String.format("%1" + (Person.MAX_PHONE_NUMBER_LENGTH + 1) + "s", "");
        corePhoneNumberTest(tooLong, false);
    }

    /**
     * Core of phone number test: insures the given value is acceptable or not.
     *
     * @param testValue
     *            Test value for a phone nubmer.
     * @param ok
     *            If it should be valid.
     */
    private void corePhoneNumberTest(final String testValue, final boolean ok)
    {
        sut.setWorkPhone(testValue);
        sut.setCellPhone(testValue);
        sut.setFax(testValue);
        ClassValidator<Person> validator = new ClassValidator<Person>(Person.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);
        assertEquals(message, ok ? 0 : 3, invalidValues.length);
    }

    /**
     * Test adding a tab to the START group.
     */
    @Test
    public void addTab()
    {
        Tab tab = new Tab("new tab", Layout.TWOCOLUMN, new Long(0));
        sut.addTab(tab, TabGroupType.START);
        List<Tab> actual = sut.getTabs(TabGroupType.START);
        assertTrue("new tab is missing", actual.contains(tab));

        tab = new Tab("new tab", Layout.TWOCOLUMN, new Long(1));
        sut.addTab(tab, TabGroupType.START);
        actual = sut.getTabs(TabGroupType.START);
        assertTrue("new tab is missing", actual.contains(tab));
    }

    /**
     * person translates a string of Longs into a set. this test to make sure that is working.
     */
    @Test
    public void testOptidIdTranslation()
    {
        assertTrue(sut.getOptOutVideos().contains(5L));
        HashSet<Long> tutvids = sut.getOptOutVideos();
        tutvids.add(3L);
        sut.setOptOutVideos(tutvids);
        assertTrue(sut.getOptOutVideos().contains(5L));
        assertTrue(sut.getOptOutVideos().contains(3L));
    }

    /**
     * Test the theme getter & setter.
     */
    @Test
    public void testThemes()
    {
        Theme theme = new Theme();
        sut.setTheme(theme);

        assertEquals("Theme doesn't match", theme, sut.getTheme());
    }

    /**
     * Test equality.
     */
    @Test
    public void testEquality()
    {
        Person p1 = new Person("p", "f", "m", "l", "p");
        Person p2 = new Person("p", "f", "m", "l", "p");
        p2.setId(p1.getId());

        assertFalse(p1 == p2);
        assertTrue(p1.equals(p2));
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    /**
     * Touch the profile properties for coverage - they're also tested in integration tests.
     */
    @Test
    public void testUninitializedProfileProperties()
    {
        Person p = new Person("p", "f", "m", "l", "p");
        assertNull(p.getBackground());
    }
}
