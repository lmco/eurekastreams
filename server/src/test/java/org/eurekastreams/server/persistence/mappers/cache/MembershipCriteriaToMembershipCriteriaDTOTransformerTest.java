/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for MembershipCriteriaToMembershipCriteriaDTOTransformer.
 * 
 */
public class MembershipCriteriaToMembershipCriteriaDTOTransformerTest
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
     * System under test.
     */
    private MembershipCriteriaToMembershipCriteriaDTOTransformer sut = //
    new MembershipCriteriaToMembershipCriteriaDTOTransformer();

    /**
     * {@link MembershipCriteria}.
     */
    private MembershipCriteria mc = context.mock(MembershipCriteria.class);

    /**
     * {@link GalleryTabTemplate}.
     */
    private GalleryTabTemplate gtt = context.mock(GalleryTabTemplate.class);

    /**
     * {@link Theme}.
     */
    private Theme theme = context.mock(Theme.class);

    /**
     * Theme name.
     */
    private String themeName = "themeName";

    /**
     * GalleryTabTemplate name.
     */
    private String gttName = "gttName";

    /**
     * GalleryTabTemplate id.
     */
    private Long gttId = 1L;

    /**
     * Theme id.
     */
    private Long themeId = 2L;

    /**
     * Test criterion.
     */
    private String criterion = "criterion";

    /**
     * MembershipCriteria id.
     */
    private Long mcid = 3L;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mc).getId();
                will(returnValue(mcid));

                oneOf(mc).getCriteria();
                will(returnValue(criterion));

                oneOf(mc).getGalleryTabTemplate();
                will(returnValue(null));

                oneOf(mc).getTheme();
                will(returnValue(null));
            }
        });

        List<MembershipCriteriaDTO> mcdtos = sut.transform(Arrays.asList(mc));
        assertEquals(mcid, mcdtos.get(0).getId());
        assertEquals(criterion, mcdtos.get(0).getCriteria());
        assertNull(mcdtos.get(0).getGalleryTabTemplateId());
        assertNull(mcdtos.get(0).getGalleryTabTemplateName());
        assertNull(mcdtos.get(0).getThemeId());
        assertNull(mcdtos.get(0).getThemeName());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testNoNulls()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mc).getId();
                will(returnValue(mcid));

                oneOf(mc).getCriteria();
                will(returnValue(criterion));

                oneOf(mc).getGalleryTabTemplate();
                will(returnValue(gtt));

                oneOf(mc).getTheme();
                will(returnValue(theme));

                oneOf(theme).getId();
                will(returnValue(themeId));

                oneOf(theme).getName();
                will(returnValue(themeName));

                oneOf(gtt).getId();
                will(returnValue(gttId));

                oneOf(gtt).getTitle();
                will(returnValue(gttName));
            }
        });

        List<MembershipCriteriaDTO> mcdtos = sut.transform(Arrays.asList(mc));
        assertEquals(mcid, mcdtos.get(0).getId());
        assertEquals(criterion, mcdtos.get(0).getCriteria());
        assertEquals(gttId, mcdtos.get(0).getGalleryTabTemplateId());
        assertEquals(gttName, mcdtos.get(0).getGalleryTabTemplateName());
        assertEquals(themeId, mcdtos.get(0).getThemeId());
        assertEquals(themeName, mcdtos.get(0).getThemeName());

        context.assertIsSatisfied();
    }

}
