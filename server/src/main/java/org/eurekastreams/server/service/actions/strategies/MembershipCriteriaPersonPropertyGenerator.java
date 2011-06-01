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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.server.action.response.settings.PersonPropertiesResponse;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * Returns tab templates and them for user based on person's sourceList parameters. If no source list parameter is
 * found, defaults are returned.
 * 
 */
public class MembershipCriteriaPersonPropertyGenerator implements PersonPropertiesGenerator
{
    /**
     * Name for default tab.
     */
    private String defaultTabName;

    /**
     * Layout for default tab.
     */
    private Layout defaultTabLayout;

    /**
     * Source list key in parameter map.
     */
    private String sourceListKey = "sourceList";

    /**
     * The SystemSettings mapper.
     */
    @SuppressWarnings("unchecked")
    private DomainMapper<MapperRequest, List<MembershipCriteria>> membershipCriteriaDAO;

    /**
     * @param inDefaultTabName
     *            Default tab name.
     * @param inDefaultTabLayout
     *            Default tab layout.
     * @param inMembershipCriteriaDAO
     *            used to look up the system settings.
     */
    @SuppressWarnings("unchecked")
    public MembershipCriteriaPersonPropertyGenerator(final String inDefaultTabName, final Layout inDefaultTabLayout,
            final DomainMapper<MapperRequest, List<MembershipCriteria>> inMembershipCriteriaDAO)
    {
        defaultTabName = inDefaultTabName;
        defaultTabLayout = inDefaultTabLayout;
        membershipCriteriaDAO = inMembershipCriteriaDAO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PersonPropertiesResponse getPersonProperties(final Map<String, Serializable> inParameters)
    {
        ArrayList<TabTemplate> tabTemplateResults = new ArrayList<TabTemplate>();
        Theme themeResult = null;

        // if params contain source list and it's not empty, try to create tab templates by matching against
        // membership criteria.
        if (inParameters.containsKey(sourceListKey) && !((ArrayList<String>) inParameters.get(sourceListKey)).isEmpty())
        {
            // grab source list from params.
            ArrayList<String> sourceList = (ArrayList<String>) inParameters.get(sourceListKey);

            // grab all membershipCriteria from datastore.
            List<MembershipCriteria> membershipCriteria = membershipCriteriaDAO.execute(null);

            // loop through criteria checking against the persons source list values to see if the criteria applies, if
            // so, create copy of MC's tab template (if present) and put in result list.
            for (MembershipCriteria mc : membershipCriteria)
            {
                for (String source : sourceList)
                {
                    if (StringUtils.containsIgnoreCase(source, mc.getCriteria()))
                    {
                        // if criteria has tab template specified add it to results.
                        if (mc.getGalleryTabTemplate() != null)
                        {
                            // These tabs create their own templates based on other templates.
                            tabTemplateResults.add(new TabTemplate(mc.getGalleryTabTemplate().getTabTemplate()));
                        }

                        // set theme.
                        themeResult = mc.getTheme();
                    }
                }
            }
        }

        // if no tab templates were found via matching membership criteria, return default tab.
        if (tabTemplateResults.size() == 0)
        {
            tabTemplateResults.add(getDefaultTabTemplate());
        }

        return new PersonPropertiesResponse(tabTemplateResults, themeResult);
    }

    /**
     * Return new TabTemplate instance with default name and layout.
     * 
     * @return New TabTemplate instance with default name and layout.
     */
    private TabTemplate getDefaultTabTemplate()
    {
        return new TabTemplate(defaultTabName, defaultTabLayout);
    }
}
