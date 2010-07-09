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
package org.eurekastreams.server.search.modelview;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.junit.Test;

/**
 * Test fixture for OrganizationModelView.
 */
public class OrganizationModelViewTest
{
    /**
     * Organization ID.
     */
    private final long organizationId = 74747L;

    /**
     * Search score.
     */
    private final float searchScore = 775759L;

    /**
     * The organization name.
     */
    private final String name = "Coolest Org in the Multiverse";

    /**
     * The organization name.
     */
    private final String description = "To do cool stuff";

    /**
     * The number of followers for this organization.
     */
    private final int followersCount = 7847;

    /**
     * The number of employees in this organization.
     */
    private final int employeesCount = 7721;

    /**
     * The number of child groups in this organization.
     */
    private final int groupsCount = 171;

    /**
     * The number of updates for this Organization.
     */
    private final int updatesCount = 3837;

    /**
     * The number of child organizations in this organization.
     */
    private final int organizationsCount = 747;

    /**
     * The shortname.
     */
    private final String shortName = "shortName";

    /**
     * The avatar id.
     */
    private final String avatarId = "sljdfsDLK";

    /**
     * Parent org id.
     */
    private final long parentOrganizationId = 32883L;

    /**
     * Banner id.
     */
    private String bannerId = "sldkjfsdlfj";

    /**
     * Test helper method to assert all properties.
     *
     * @param sut
     *            the SUT
     */
    private void assertAll(final OrganizationModelView sut)
    {
        assertEquals(name, sut.getName());
        assertEquals(description, sut.getDescription());
        assertEquals(followersCount, sut.getFollowersCount());
        assertEquals(employeesCount, sut.getDescendantEmployeeCount());
        assertEquals(groupsCount, sut.getDescendantGroupCount());
        assertEquals(organizationsCount, sut.getChildOrganizationCount());
        assertEquals(shortName, sut.getShortName());
        assertEquals(avatarId, sut.getAvatarId());
        assertEquals(updatesCount, sut.getUpdatesCount());
        assertEquals(parentOrganizationId, sut.getParentOrganizationId());
        assertEquals(bannerId, sut.getBannerId());
    }

    /**
     * Test helper method to assert the default values.
     *
     * @param sut
     *            the SUT
     */
    private void assertDefaultValues(final OrganizationModelView sut)
    {
        assertEquals(ModelView.UNINITIALIZED_LONG_VALUE, sut.getEntityId());
        assertEquals(ModelView.UNINITIALIZED_FLOAT_VALUE, sut.getSearchIndexScore(), 0);
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getName());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getDescription());
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getFollowersCount());
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getDescendantEmployeeCount());
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getDescendantGroupCount());
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getChildOrganizationCount());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getShortName());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getAvatarId());
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getUpdatesCount());
        assertEquals(ModelView.UNINITIALIZED_LONG_VALUE, sut.getParentOrganizationId());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getBannerId());
    }

    /**
     * Test the setters.
     */
    @Test
    public void testSetters()
    {
        OrganizationModelView sut = new OrganizationModelView();
        sut.setName(name);
        sut.setDescription(description);
        sut.setFollowersCount(followersCount);
        sut.setDescendantEmployeeCount(employeesCount);
        sut.setDescendantGroupCount(groupsCount);
        sut.setChildOrganizationCount(organizationsCount);
        sut.setShortName(shortName);
        sut.setAvatarId(avatarId);
        sut.setUpdatesCount(updatesCount);
        sut.setParentOrganizationId(parentOrganizationId);
        sut.setBannerId(bannerId);
        assertAll(sut);
    }

    /**
     * Test the default property values.
     */
    @Test
    public void testDefaultValues()
    {
        OrganizationModelView sut = new OrganizationModelView();
        assertDefaultValues(sut);
    }

    /**
     * Test toString().
     */
    @Test
    public void testToString()
    {
        OrganizationModelView sut = new OrganizationModelView();

        // test uninitialized toString()
        assertEquals("Organization", sut.toString());

        // load the ID
        HashMap<String, Object> p = new HashMap<String, Object>();
        p.put("__HSearch_id", organizationId);
        sut.loadProperties(p);

        // test initialized sut
        assertEquals("Organization#" + organizationId, sut.toString());
    }

    /**
     * Test loading all properties.
     */
    @Test
    public void testLoadProperties()
    {
        HashMap<String, Object> p = new HashMap<String, Object>();
        p.put("__HSearch_id", organizationId);
        p.put("__HSearch_Score", searchScore);
        p.put("name", name);
        p.put("description", description);
        p.put("followersCount", followersCount);
        p.put("descendantEmployeeCount", employeesCount);
        p.put("descendantGroupCount", groupsCount);
        p.put("childOrganizationCount", organizationsCount);
        p.put("shortName", shortName);
        p.put("avatarId", avatarId);
        p.put("updatesCount", updatesCount);
        p.put("parentOrganizationId", parentOrganizationId);
        p.put("bannerId", bannerId);

        OrganizationModelView sut = new OrganizationModelView();
        sut.loadProperties(p);
        assertAll(sut);
    }

    /**
     * Test loading an empty property map keeps default values.
     */
    @Test
    public void testLoadEmptyProperties()
    {
        HashMap<String, Object> p = new HashMap<String, Object>();
        OrganizationModelView sut = new OrganizationModelView();
        sut.loadProperties(p);
        assertDefaultValues(sut);
    }
}
