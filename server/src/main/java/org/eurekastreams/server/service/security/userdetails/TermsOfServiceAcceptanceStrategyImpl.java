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
package org.eurekastreams.server.service.security.userdetails;

import java.util.Date;

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * Strategy for determining validity of ToS acceptance date.
 *
 */
public class TermsOfServiceAcceptanceStrategyImpl implements TermsOfServiceAcceptanceStrategy
{
    /**
     * The system settings mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> systemSettingsDAO;

    /**
     * The number of milliseconds in a day.
     */
    private static final long MILLISECONDS_IN_A_DAY = 86400000L;

    /**
     * Constructor.
     *
     * @param inSystemSettingsDAO
     *            DAO for getting SystemSettings.
     */
    public TermsOfServiceAcceptanceStrategyImpl(final DomainMapper<MapperRequest, SystemSettings> inSystemSettingsDAO)
    {
        systemSettingsDAO = inSystemSettingsDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidTermsOfServiceAcceptanceDate(final Date inDateLastAccepted)
    {
        // grab system settings.
        SystemSettings settings = systemSettingsDAO.execute(null);

        // short circuit if no ToS to display.
        if (null == settings.getTermsOfService())
        {
            return true;
        }

        // short circuit if ToS is required every session.
        if (inDateLastAccepted == null || settings.getIsTosDisplayedEverySession())
        {
            return false;
        }

        long validFor = MILLISECONDS_IN_A_DAY * settings.getTosPromptInterval();
        Date now = new Date();

        return validFor >= (now.getTime() - inDateLastAccepted.getTime());
    }
}
