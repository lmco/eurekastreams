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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer.
 * 
 */
public class ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformerTest
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
     * Theme mapper.
     */
    private DomainMapper<Long, Theme> themeProxyMapper = context.mock(DomainMapper.class, "themeProxyMapper");

    /**
     * Theme.
     */
    private Theme theme = context.mock(Theme.class);

    /**
     * GalleryTabTemplate mapper.
     */
    private DomainMapper<Long, GalleryTabTemplate> galleryTabTemplateProxyMappery = context.mock(DomainMapper.class,
            "galleryTabTemplateProxyMappery");

    /**
     * GalleryTabTemplate.
     */
    private GalleryTabTemplate gtt = context.mock(GalleryTabTemplate.class);

    /**
     * {@link ActionContext}.
     */
    private ActionContext ac = context.mock(ActionContext.class);

    /**
     * Criteria string used in test.
     */
    private String criteria = "criteria";

    /**
     * System under test.
     */
    private ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer sut = //
    new ActionContextMembershipCriteriaDTOToMembershipCriteriaPersistenceRequestTransformer(themeProxyMapper,
            galleryTabTemplateProxyMappery);

    /**
     * Test.
     */
    @Test
    public void testNullThemeAndGttIds()
    {
        final MembershipCriteriaDTO mcdto = new MembershipCriteriaDTO();
        mcdto.setCriteria(criteria);

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(mcdto));
            }
        });

        PersistenceRequest<MembershipCriteria> result = sut.transform(ac);

        assertEquals(criteria, result.getDomainEnity().getCriteria());
        assertNull(result.getDomainEnity().getTheme());
        assertNull(result.getDomainEnity().getGalleryTabTemplate());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testDefaultThemeAndGttIds()
    {
        final Long defaultId = -1L;
        final MembershipCriteriaDTO mcdto = new MembershipCriteriaDTO();
        mcdto.setCriteria(criteria);
        mcdto.setThemeId(defaultId);
        mcdto.setGalleryTabTemplateId(defaultId);

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(mcdto));
            }
        });

        PersistenceRequest<MembershipCriteria> result = sut.transform(ac);

        assertEquals(criteria, result.getDomainEnity().getCriteria());
        assertNull(result.getDomainEnity().getTheme());
        assertNull(result.getDomainEnity().getGalleryTabTemplate());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testWithThemeAndGttIds()
    {
        final Long themeId = 5L;
        final Long gttId = 6L;
        final MembershipCriteriaDTO mcdto = new MembershipCriteriaDTO();
        mcdto.setCriteria(criteria);
        mcdto.setThemeId(themeId);
        mcdto.setGalleryTabTemplateId(gttId);

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(mcdto));

                oneOf(themeProxyMapper).execute(themeId);
                will(returnValue(theme));

                oneOf(galleryTabTemplateProxyMappery).execute(gttId);
                will(returnValue(gtt));
            }
        });

        PersistenceRequest<MembershipCriteria> result = sut.transform(ac);

        assertEquals(criteria, result.getDomainEnity().getCriteria());
        assertNotNull(result.getDomainEnity().getTheme());
        assertNotNull(result.getDomainEnity().getGalleryTabTemplate());

        context.assertIsSatisfied();
    }

}
