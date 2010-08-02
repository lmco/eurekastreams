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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.RefreshDataSourceMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Cache refresher for SystemSettings.
 */
public class RefreshSystemSettingsCacheMapperImpl extends CachedDomainMapper implements
        RefreshDataSourceMapper<SystemSettings>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Refresh the SystemSettings cache with the input value.
     *
     * @param inSystemSettings
     *            the system settings to update cache with
     */
    @Override
    public void refresh(final SystemSettings inSystemSettings)
    {
        log.info("Updating SystemSettings in cache from " + inSystemSettings);

        getCache().set(CacheKeys.SYSTEM_SETTINGS, inSystemSettings);
    }
}
