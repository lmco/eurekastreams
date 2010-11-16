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
package org.eurekastreams.server.action.execution.gallery;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.annotations.RequiresCredentials;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.ThemeMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.service.actions.strategies.CSSBuilderDecorator;

/**
 * Assigns a Theme to a Person based on a UUID or a URL.
 */
@RequiresCredentials
public class SetPersonThemeExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(SetPersonThemeExecution.class);

    /**
     * Mapper used to look up the person.
     */
    private PersonMapper personMapper = null;

    /**
     * Mapper used to look up the theme.
     */
    private ThemeMapper themeMapper = null;

    /**
     * Decorator for portal page.
     */
    private CSSBuilderDecorator decorator = null;

    /**
     * Domain mapper to delete keys.
     */
    private DomainMapper<Set<String>, Boolean> deleteKeysMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            injecting the PersonMapper
     * @param inThemeMapper
     *            injecting the ThemeMapper
     * @param inDecorator
     *            injecting a Decorator (or chain of)
     * @param inDeleteKeysMapper
     *            mapper to delete cache keys.
     */
    public SetPersonThemeExecution(final PersonMapper inPersonMapper, final ThemeMapper inThemeMapper,
            final CSSBuilderDecorator inDecorator, final DomainMapper<Set<String>, Boolean> inDeleteKeysMapper)
    {
        personMapper = inPersonMapper;
        themeMapper = inThemeMapper;
        decorator = inDecorator;
        deleteKeysMapper = inDeleteKeysMapper;
    }

    /**
     * Finds/Creates theme and them returns the theme's CSS.
     * 
     * @param inActionContext
     *            the logged in user
     * @return the theme's CSS file.
     * @throws ExecutionException
     *             Thrown when theme not found (Bad UUID).
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        try
        {
            String themeId = (String) inActionContext.getParams();
            Long userId = inActionContext.getPrincipal().getId();

            // try grabbing theme from validation
            Theme theme = (Theme) inActionContext.getState().get("THEME");

            if (theme == null)
            {
                if (themeId.startsWith("{") && themeId.substring(themeId.length() - 1).equals("}"))
                {
                    // theme is UUID
                    theme = themeMapper.findByUUID(themeId.substring(1, themeId.length() - 1));
                }
                else
                {
                    // Theme is a URL, find or create.
                    theme = themeMapper.findByUrl(themeId);
                }
            }
            if (theme == null)
            {
                throw new RuntimeException("Can't find theme.");
            }

            Person person = personMapper.findById(userId);
            log.debug("Got StartPage for " + inActionContext.getPrincipal().getAccountId());

            if (person.getTheme() != null && person.getTheme().getUrl().equals(theme.getUrl()))
            {
                log.debug("Forcing theme update");
                decorator.setForceUpdate(true);
            }

            person.setTheme(theme);
            personMapper.flush();
            log.debug("Set theme to " + theme.getName());

            decorator.decorate(person);

            deleteKeysMapper.execute(Collections.singleton(CacheKeys.PERSON_PAGE_PROPERTIES_BY_ID + userId));

            return theme.getCssFile();
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex);
        }
    }
}
