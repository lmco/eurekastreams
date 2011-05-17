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
package org.eurekastreams.server.action.execution.gallery;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for AddTabFromGalleryTabTemplateIdExecution.
 * 
 */
public class AddTabFromGalleryTabTemplateIdExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Find by id mapper.
     */
    private DomainMapper findById = context.mock(DomainMapper.class, "findById");

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper = context.mock(DomainMapper.class, "deleteKeysMapper");

    /**
     * System under test.
     */
    private AddTabFromGalleryTabTemplateIdExecution sut = new AddTabFromGalleryTabTemplateIdExecution(findById,
            deleteKeysMapper);

    /**
     * {@link GalleryTabTemplate}.
     */
    private GalleryTabTemplate gtt = context.mock(GalleryTabTemplate.class);

    /**
     * {@link TabTemplate}.
     */
    private TabTemplate tt = context.mock(TabTemplate.class);

    /**
     * {@link Person}.
     */
    private Person person = context.mock(Person.class);

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * User id for tests.
     */
    private Long userId = 6L;

    /**
     * GalleryTabTemplate id.
     */
    private Long gttId = 7L;

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final Sequence sequence = context.sequence("sequence-name");

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(userId));

                allowing(actionContext).getParams();
                will(returnValue(gttId));

                oneOf(gtt).getTabTemplate();
                will(returnValue(tt));

                ignoring(tt);
                ignoring(person);

                oneOf(findById).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));
                inSequence(sequence);

                oneOf(findById).execute(with(any(FindByIdRequest.class)));
                will(returnValue(gtt));
                inSequence(sequence);

                oneOf(deleteKeysMapper).execute(with(any(Set.class)));
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();

    }

}
