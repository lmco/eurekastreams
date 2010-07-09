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
package org.eurekastreams.server.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Organization test class.
 *
 */
public class OrganizationTest
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
     * Subject under test.
     */
    private Organization sut;

    /**
     * Org name.
     */
    private static final String ORG_NAME = "Organization Name";

    /**
     * Short org name.
     */
    private static final String SHORT_ORG_NAME = "OrgName";

    /**
     * Mission statement.
     */
    private static final String MISSION_STATEMENT = "Mission statement goes here";

    /**
     * Overview.
     */
    private static final String OVERVIEW = "This is the organization overview";

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new Organization(ORG_NAME, SHORT_ORG_NAME);
        sut.setDescription(MISSION_STATEMENT);
    }

    /**
     * Test the name getter.
     */
    @Test
    public void testConstructor()
    {
        assertEquals(ORG_NAME, sut.getName());
        assertEquals(SHORT_ORG_NAME.toLowerCase(), sut.getShortName());
        assertEquals(MISSION_STATEMENT, sut.getDescription());
    }

    /**
     * Test the constructor that takes an OrganizationModelView.
     */
    @Test
    public void testModelViewConstructor()
    {
        final long orgId = 832L;
        String shortName = "My ShortName";
        String name = "My Name";
        String bannerId = "mybannerid";

        OrganizationModelView orgMv = new OrganizationModelView();
        orgMv.setEntityId(orgId);
        orgMv.setShortName(shortName);
        orgMv.setName(name);
        orgMv.setBannerId(bannerId);

        Organization o = new Organization(orgMv);
        assertEquals(orgId, o.getId());
        assertEquals(shortName.toLowerCase(), o.getShortName().toLowerCase());
        assertEquals(name, o.getName());
        assertEquals(bannerId, o.getBannerId());
    }

    /**
     * Test name.
     */
    @Test
    public void testName()
    {
        String message;

        message = "property should be set";
        String name = "org name here";
        sut.setName(name);
        assertEquals(message, name, sut.getName());

        String newShortName = "newShortName";
        sut.setShortName(newShortName);
        assertEquals(message, newShortName.toLowerCase(), sut.getShortName());

        // too long
        message = "validation on properties";
        char[] chars = new char[Organization.MAX_NAME_LENGTH + 1];
        Arrays.fill(chars, 'a');
        StringBuffer buffer = new StringBuffer();
        buffer.append(chars);
        sut.setName(buffer.toString());
        ClassValidator<Organization> validator = new ClassValidator<Organization>(Organization.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);
        assertEquals(message, 1, invalidValues.length);
        assertEquals(message, Organization.NAME_LENGTH_MESSAGE, invalidValues[0].getMessage());
    }

    /**
     * Test setName().
     */
    @Test
    public void testSetName()
    {
        sut.setName("Foo");
        assertEquals("Foo", sut.getName());
        sut.setName(null);
        assertEquals("", sut.getName());
    }

    /**
     * Test updatesCount.
     */
    @Test
    public void testUpdatesCount()
    {
        sut.setUpdatesCount(3);
        assertEquals(3, sut.getUpdatesCount());
    }

    /**
     * Test get/set for StreamScope.
     */
    @Test
    public void testGetSetStreamScope()
    {
        StreamScope streamScopeMock = context.mock(StreamScope.class);
        sut.setStreamScope(streamScopeMock);
        assertEquals(streamScopeMock, sut.getStreamScope());
    }

    /**
     * Test the parentOrgId property.
     */
    @Test
    public void testParentOrgId()
    {
        sut.setParentOrgId(4L);
        assertEquals(new Long(4L), sut.getParentOrgId());
    }

    /**
     * Test setMembershipCriteria.
     */
    @Test
    public void testSetMembershipCriteria()
    {
        sut.setAllUsersCanCreateGroups(true);
        assertEquals(Boolean.TRUE, sut.getAllUsersCanCreateGroups());
        sut.setAllUsersCanCreateGroups(false);
        assertEquals(Boolean.FALSE, sut.getAllUsersCanCreateGroups());
    }

    /**
     * Test the avatarId.
     */
    @Test
    public void testAvatarId()
    {
        sut.setAvatarId("IDABC");
        assertEquals("IDABC", sut.getAvatarId());
    }

    /**
     * Test setName().
     */
    @Test
    public void testSetShortName()
    {
        sut.setShortName("Fo");
        assertEquals("fo", sut.getShortName());
        sut.setShortName(null);
        assertEquals("", sut.getShortName());
    }

    /**
     * Test equals().
     */
    @Test
    public void testEquals()
    {
        final long id = 12342L;
        Organization o1 = new Organization();
        o1.setId(id);

        Organization o2 = new Organization();
        o2.setId(id);

        assertTrue(o2.equals(o1));
        assertFalse(o2.equals(id));
    }

    /**
     * Test theme getter & setter.
     */
    @Test
    public void testUrl()
    {
        String value = "url here";
        sut.setUrl(value);
        assertEquals("property should be set", value, sut.getUrl());
    }

    /**
     * Test url validation.
     */
    @Test
    public void testUrlInvalid()
    {
        String value = "httpx://www.google.com";
        sut.setUrl(value);
        ClassValidator<Organization> validator = new ClassValidator<Organization>(Organization.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);
        assertEquals("incorrect # of validation errors.", 1, invalidValues.length);
        assertEquals("incorrect url message.", Organization.WEBSITE_MESSAGE, invalidValues[0].getMessage());
    }

    /**
     * Test url validation.
     */
    @Test
    public void testUrlValid()
    {
        sut.setUrl("https://www.gOOgle_is_Nice.com/blah#anchor");
        ClassValidator<Organization> validator = new ClassValidator<Organization>(Organization.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);
        assertEquals("incorrect # of validation errors.", 0, invalidValues.length);
    }

    /**
     * test isCoordinator.
     */
    @Test
    public void testCoordinators()
    {
        Person a = new Person("a", "a", "a", "a", "a");
        Person b = new Person("b", "b", "b", "b", "b");
        sut.addCoordinator(a);
        assertTrue(sut.isCoordinator(a.getAccountId()));
        assertFalse(sut.isCoordinator(b.getAccountId()));
    }

    /**
     * Test theme getter & setter.
     */
    @Test
    public void testMission()
    {
        String value = "missionhere";
        sut.setDescription(value);
        assertEquals("property should be set", value, sut.getDescription());

        // too long
        String message = "validation on properties";
        char[] chars = new char[Organization.MAX_DESCRIPTION_LENGTH + 1];
        Arrays.fill(chars, 'a');
        StringBuffer buffer = new StringBuffer();
        buffer.append(chars);
        sut.setDescription(buffer.toString());
        ClassValidator<Organization> validator = new ClassValidator<Organization>(Organization.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);
        assertEquals(message, 1, invalidValues.length);
        assertEquals(message, Organization.DESCRIPTION_LENGTH_MESSAGE, invalidValues[0].getMessage());
    }

    /**
     * Test overview.
     */
    @Test
    public void overview()
    {
        sut.setOverview(OVERVIEW);

        assertEquals("Overview does not match", OVERVIEW, sut.getOverview());
    }

    /**
     * Test theme getter & setter.
     */
    @Test
    public void testValidateMinCoordinators()
    {

        String message = "validation on coordinator properties";
        sut = new Organization("org", "org");
        sut.setDescription("statement");
        Set<Person> coordinators = new TreeSet<Person>();
        sut.setCoordinators(coordinators);
        ClassValidator<Organization> validator = new ClassValidator<Organization>(Organization.class);
        InvalidValue[] invalidValues = validator.getInvalidValues(sut);

        // should be invalid when empty
        assertEquals(message, 0, sut.getCoordinators().size());
        assertEquals(message, 1, invalidValues.length);
        assertEquals(message, Organization.MIN_COORDINATORS_MESSAGE, invalidValues[0].getMessage());

        // should be valid when not empty
        coordinators.add(new Person("a", "b", "c", "d", "e"));
        assertEquals(message, 1, sut.getCoordinators().size());
        invalidValues = validator.getInvalidValues(sut);
        assertEquals(message, 0, invalidValues.length);

    }

    /**
     * Test getter/setter for parent org - only used in serialization.
     */
    @Test
    public void testParentOrganization()
    {
        Organization parentOrg = new Organization("ParentOrganization", "parent org.");
        sut.setParentOrganization(parentOrg);

        assertEquals("ParentOrganization", sut.getParentOrganization().getName());
    }

    /**
     * Test get/set of capabilities.
     */
    @Test
    public void testGetSetCapabilities()
    {
        final BackgroundItem backgroundItemMock = context.mock(BackgroundItem.class);

        // verify that get on null collection returns empty list, not null;
        assertNotNull(sut.getCapabilities());

        // verify that get returns what set sets.
        ArrayList<BackgroundItem> testList = new ArrayList<BackgroundItem>(1);
        testList.add(backgroundItemMock);

        sut.setCapabilities(testList);
        assertEquals("get not returning same list as set assigned", 1, sut.getCapabilities().size());
    }

    /**
     * Test the denormalization columns.
     */
    @Test
    public void testDenormalizedTotals()
    {
        final int descEmpCount = 38382;
        final int descOrgCount = 28846;
        final int descGroupCount = 84843;
        final int followerCount = 38282;

        Organization org = new Organization("foo", "Bar");
        org.setDescendantEmployeeCount(descEmpCount);
        org.setDescendantGroupCount(descGroupCount);
        org.setChildOrganizationCount(descOrgCount);
        org.setEmployeeFollowerCount(followerCount);

        assertEquals(descEmpCount, org.getDescendantEmployeeCount());
        assertEquals(descOrgCount, org.getChildOrganizationCount());
        assertEquals(descGroupCount, org.getDescendantGroupCount());
        assertEquals(followerCount, org.getEmployeeFollowerCount());
    }

    /**
     * Test banner id getter & setter.
     */
    @Test
    public void testImages()
    {
        String avatar = "avatar";
        String value = "id here";
        Integer avatarCropX = 1;
        Integer avatarCropY = 2;
        Integer avatarCropSize = 3;

        sut.setBannerId(value);
        sut.setAvatarCropSize(avatarCropSize);
        sut.setAvatarCropX(avatarCropX);
        sut.setAvatarCropY(avatarCropY);
        sut.setAvatarId(avatar);

        assertEquals("property should be set", value, sut.getBannerId());
        assertEquals(3, (int) sut.getAvatarCropSize());
        assertEquals(1, (int) sut.getAvatarCropX());
        assertEquals(2, (int) sut.getAvatarCropY());
        assertEquals("property should be set", avatar, sut.getAvatarId());
    }

    /**
     * Test root org predicate.
     */
    @Test
    public void testIsRootOrg()
    {
        Organization org1 = new Organization(ORG_NAME, SHORT_ORG_NAME);
        org1.setId(1L);
        Organization org7 = new Organization(ORG_NAME, SHORT_ORG_NAME);
        org7.setId(7L);

        sut.setId(7L);

        // no parent set
        assertFalse("Should be false if parent org is not set (hence unknown).", sut.isRootOrganization());

        // set to self
        sut.setParentOrganization(sut);
        assertTrue("Should be true if parent org is set to self.", sut.isRootOrganization());

        // set to another with same id
        sut.setParentOrganization(org7);
        assertTrue("Should be true if parent org has same id.", sut.isRootOrganization());

        // set to another with different id
        sut.setParentOrganization(org1);
        assertFalse("Should be false if parent org has different id.", sut.isRootOrganization());
    }

}
