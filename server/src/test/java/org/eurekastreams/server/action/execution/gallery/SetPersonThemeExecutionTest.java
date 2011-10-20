/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import static junit.framework.Assert.assertEquals;

import java.util.Set;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.ThemeMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SetPersonThemeExecution.
 */
public class SetPersonThemeExecutionTest
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
     * The account id of the user.
     */
    private final String username = "validuser";

    /**
     * the id of the user.
     */
    private final Long userId = 38271L;

    /**
     * The mock mapper to be used by the action.
     */
    private final PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * The mock mapper to be used by the action.
     */
    private final ThemeMapper themeMapper = context.mock(ThemeMapper.class);

    /**
     * Mocked user whose Theme is being set.
     */
    private final Person person = context.mock(Person.class);

    /**
     * {@link DomainMapper}.
     */
    private final DomainMapper<Set<String>, Boolean> deleteCacheKeysMapper = context.mock(DomainMapper.class);

    /**
     * Subject under test.
     */
    private SetPersonThemeExecution sut = null;

    /**
     *
     */
    @Before
    public final void setup()
    {
        sut = new SetPersonThemeExecution(personMapper, themeMapper, deleteCacheKeysMapper);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     *
     * @throws Exception
     *             shouldn't happen
     */
    @Test
    public final void testExecuteWithUrl() throws Exception
    {
        final String themeId = "http://localhost:8080/themes/myTheme.xml";

        final Theme testTheme = new Theme(themeId, "My Theme", "My Theme Description",
                "http://localhost:8080/themes/myTheme.css", "FAKE-UUID-0000-0000-0000", "My Theme Banner Id",
                "authorName", "authorEmail");

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findById(userId);
                will(returnValue(person));

                oneOf(themeMapper).findByUrl(themeId);
                will(returnValue(testTheme));

                oneOf(person).setTheme(testTheme);

                allowing(person).getTheme();

                // persist
                oneOf(personMapper).flush();

                never(themeMapper).findByUUID(with(any(String.class)));

                oneOf(deleteCacheKeysMapper).execute(with(any(Set.class)));
            }
        });

        // Make the call
        String actual = (String) sut.execute(buildServerActionContext(themeId));

        assertEquals(testTheme.getCssFile(), actual);

        context.assertIsSatisfied();
    }

    /**
     * Call the execute method when the theme was passed through the state from the validator.
     *
     * @throws Exception
     *             shouldn't happen
     */
    @Test
    public final void testWithThemePassedThroughInState() throws Exception
    {
        final String themeId = "http://localhost:8080/themes/myTheme.xml";

        final Theme testTheme = new Theme(themeId, "My Theme", "My Theme Description",
                "http://localhost:8080/themes/myTheme.css", "FAKE-UUID-0000-0000-0000", "My Theme Banner Id",
                "authorName", "authorEmail");

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findById(userId);
                will(returnValue(person));

                oneOf(person).setTheme(testTheme);

                allowing(person).getTheme();

                // persist
                oneOf(personMapper).flush();

                never(themeMapper).findByUUID(with(any(String.class)));

                oneOf(deleteCacheKeysMapper).execute(with(any(Set.class)));
            }
        });

        // Make the call
        PrincipalActionContext actionContext = buildServerActionContext(themeId);
        actionContext.getState().put("THEME", testTheme);
        String actual = (String) sut.execute(actionContext);

        assertEquals(testTheme.getCssFile(), actual);

        context.assertIsSatisfied();
    }

    /**
     * Call the execute method and make sure it produces what it should.
     *
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test
    public final void testExecuteWithUUID() throws Exception
    {
        final Theme testTheme = new Theme("http://localhost:8080/themes/myTheme.xml", "My Theme",
                "My Theme Description", "http://localhost:8080/themes/myTheme.css", "FAKE-UUID-0000-0000-0000",
                "My Theme Banner Id", "authorName", "authorEmail");

        final String uuid = "FAKE-UUID-0000-0000-0000";

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findById(userId);
                will(returnValue(person));

                oneOf(themeMapper).findByUUID(uuid);
                will(returnValue(testTheme));

                oneOf(person).setTheme(testTheme);

                allowing(person).getTheme();

                // persist
                oneOf(personMapper).flush();

                never(themeMapper).findByUrl((with(any(String.class))));

                oneOf(deleteCacheKeysMapper).execute(with(any(Set.class)));
            }
        });

        // Make the call
        String actual = (String) sut.execute(buildServerActionContext("{" + uuid + "}"));

        assertEquals(testTheme.getCssFile(), actual);

        context.assertIsSatisfied();
    }

    /**
     * Call the execute method and make sure it produces what it should.
     *
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test(expected = ExecutionException.class)
    public final void testExecuteWithBadUUID() throws Exception
    {
        final String themeUuid = "BAD-BAD-BAD";
        final Theme testTheme = new Theme("http://localhost:8080/themes/myTheme.xml", "My Theme",
                "My Theme Description", "http://localhost:8080/themes/myTheme.css", "FAKE-UUID-0000-0000-0000",
                "My Theme Banner Id", "authorName", "authorEmail");

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findById(userId);
                will(returnValue(person));

                oneOf(themeMapper).findByUUID(themeUuid);
                will(returnValue(null));

                never(themeMapper).findByUrl((with(any(String.class))));

                oneOf(person).setTheme(testTheme);
            }
        });

        // Make the call
        String actual = (String) sut.execute(buildServerActionContext("{" + themeUuid + "}"));

        assertEquals(testTheme.getCssFile(), actual);

        context.assertIsSatisfied();
    }

    /**
     * Build a server action context for testing.
     *
     * @param themeId
     *            the theme id to set as the parameter
     * @return a server action context for testing
     */
    private PrincipalActionContext buildServerActionContext(final String themeId)
    {
        return new ServiceActionContext(themeId, TestContextCreator.createPrincipal(username, userId));
    }
}
