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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Get the Theme xml url from db by uuid.
 * 
 */
public class GetThemeXmlUrlByUuidDbMapper extends BaseArgDomainMapper<String, String>
{

    /**
     * Get the Theme xml url from db by uuid.
     * 
     * @param inRequest
     *            theme uuid.
     * @return theme xml url.
     */
    @Override
    public String execute(final String inRequest)
    {
        // grab theme url from db
        return (String) getEntityManager().createQuery("SELECT t.themeUrl from Theme t where uuid = :uuid")
                .setParameter("uuid", inRequest.toLowerCase()).getSingleResult();
    }

}
