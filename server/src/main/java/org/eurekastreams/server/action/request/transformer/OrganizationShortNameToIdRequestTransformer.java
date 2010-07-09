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
package org.eurekastreams.server.action.request.transformer;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;

/**
 * RequestTransformer for getting organization id via shortname..
 * 
 */
public class OrganizationShortNameToIdRequestTransformer extends HashMapValueRequestTransformer
{
    /**
     * Mapper to get organizations from short names.
     */
    private GetOrganizationsByShortNames getOrganizationsByShortNamesMapper;

    /**
     * flag to determine if return value should be string.
     */
    private Boolean returnValueAsString = false;

    /**
     * Constructor.
     * 
     * @param inGetOrganizationsByShortNamesMapper
     *            {@link GetOrganizationsByShortNames}.
     * @param inKey
     *            key to look up from hashmap.
     */
    public OrganizationShortNameToIdRequestTransformer(
            final GetOrganizationsByShortNames inGetOrganizationsByShortNamesMapper, final String inKey)
    {
        super(inKey);
        getOrganizationsByShortNamesMapper = inGetOrganizationsByShortNamesMapper;
    }

    /**
     * Returns organization id via shortName.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return the org id.
     */
    @Override
    public Serializable transform(final ActionContext inActionContext)
    {
        String shortName = (String) super.transform(inActionContext);
        Long orgId = getOrganizationsByShortNamesMapper.fetchId(shortName);
        return returnValueAsString ? orgId.toString() : orgId;
    }

    /**
     * @param inReturnValueAsString
     *            the returnValueAsString to set
     */
    public void setReturnValueAsString(final Boolean inReturnValueAsString)
    {
        returnValueAsString = inReturnValueAsString;
    }
}
