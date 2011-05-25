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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eurekastreams.server.action.response.settings.PersonPropertiesResponse;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for MembershipCriteriaPersonPropertyGenerator.
 * 
 */
@SuppressWarnings("unchecked")
public class MembershipCriteriaPersonPropertyGeneratorTest
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
     * The SystemSettings mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> systemSettingsDAO = context.mock(DomainMapper.class,
            "systemSettingsDAO");

    /**
     * {@link SystemSettings}.
     */
    private SystemSettings systemSettings = context.mock(SystemSettings.class, "systemSettings");

    /**
     * {@link GalleryTabTemplate}.
     */
    private GalleryTabTemplate galleryTabTemplate = context.mock(GalleryTabTemplate.class, "galleryTabTemplate");

    /**
     * {@link MembershipCriteria}.
     */
    private MembershipCriteria membershipCriteria = context.mock(MembershipCriteria.class, "membershipCriteria");

    /**
     * System under test.
     */
    private MembershipCriteriaPersonPropertyGenerator sut = new MembershipCriteriaPersonPropertyGenerator("welcome",
            Layout.THREECOLUMN, systemSettingsDAO);

    /**
     * Test.
     */
    @Test
    public final void testNoKey()
    {
        HashMap<String, Serializable> inParams = new HashMap<String, Serializable>();

        PersonPropertiesResponse ppr = sut.getPersonProperties(inParams);
        context.assertIsSatisfied();

        assertEquals(1, ppr.getTabTemplates().size());
    }

    /**
     * Test.
     */
    @Test
    public final void testEmptySourceList()
    {
        HashMap<String, Serializable> inParams = new HashMap<String, Serializable>();
        inParams.put("sourceList", new ArrayList<String>());

        PersonPropertiesResponse ppr = sut.getPersonProperties(inParams);
        context.assertIsSatisfied();

        assertEquals(1, ppr.getTabTemplates().size());
    }

    /**
     * Test.
     */
    @Test
    public final void testNoMembershipCriteria()
    {
        ArrayList<String> sourceList = new ArrayList<String>(Arrays.asList("blah"));
        HashMap<String, Serializable> inParams = new HashMap<String, Serializable>();
        inParams.put("sourceList", sourceList);

        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingsDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).getMembershipCriteria();
                will(returnValue(new ArrayList<MembershipCriteria>()));
            }
        });

        PersonPropertiesResponse ppr = sut.getPersonProperties(inParams);
        context.assertIsSatisfied();

        assertEquals(1, ppr.getTabTemplates().size());
    }

    /**
     * Test.
     */
    @Test
    public final void test()
    {
        ArrayList<String> sourceList = new ArrayList<String>(Arrays.asList("blah"));
        HashMap<String, Serializable> inParams = new HashMap<String, Serializable>();
        inParams.put("sourceList", sourceList);

        final TabTemplate tt = new TabTemplate("mc tab title", Layout.ONECOLUMN);
        final ArrayList<MembershipCriteria> mcs = new ArrayList<MembershipCriteria>(Arrays.asList(membershipCriteria));

        context.checking(new Expectations()
        {
            {
                oneOf(systemSettingsDAO).execute(null);
                will(returnValue(systemSettings));

                oneOf(systemSettings).getMembershipCriteria();
                will(returnValue(mcs));

                oneOf(membershipCriteria).getCriteria();
                will(returnValue("blah"));

                allowing(membershipCriteria).getGalleryTabTemplate();
                will(returnValue(galleryTabTemplate));

                oneOf(galleryTabTemplate).getTabTemplate();
                will(returnValue(tt));

                oneOf(membershipCriteria).getTheme();
                will(returnValue(null));
            }
        });

        PersonPropertiesResponse ppr = sut.getPersonProperties(inParams);
        context.assertIsSatisfied();

        assertEquals(1, ppr.getTabTemplates().size());
        assertEquals(Layout.ONECOLUMN, ppr.getTabTemplates().get(0).getTabLayout());
    }
}
