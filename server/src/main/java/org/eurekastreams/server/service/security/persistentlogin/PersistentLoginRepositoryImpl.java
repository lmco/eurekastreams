/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.persistentlogin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.persistence.PersistentLoginMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Implementation of PersistentLogin data store interface. Methods here are
 * where @Transactional is used if required as it cannot be placed in
 * PersistentLoginService.
 * 
 */
public class PersistentLoginRepositoryImpl implements PersistentLoginRepository
{
    /**
     * The PersistentLoginMapper used to access the data store.
     */
    private PersistentLoginMapper loginMapper = null;

    /**
     * logger.
     */
    private static Log logger = LogFactory
            .getLog(PersistentLoginRepositoryImpl.class);

    /**
     * Constructor.
     * 
     * @param inLoginMapper
     *            The PersistentLoginMapper used to access the data store.
     */
    public PersistentLoginRepositoryImpl(final PersistentLoginMapper inLoginMapper)
    {
        Assert.notNull(inLoginMapper);
        loginMapper = inLoginMapper;
    }

    /**
     * Creates or updates PersistentLogin info in data store.
     * 
     * @param persistentLogin
     *            The info to store.
     */
    @Transactional
    public void createOrUpdatePersistentLogin(final PersistentLogin persistentLogin)
    {
        loginMapper.createOrUpdate(persistentLogin);
    }

    /**
     * Returns PersistentLogin record from data store associated with specific
     * user or null if one not present.
     * 
     * @param username
     *            The username.
     * @return PersistentLogin record from data store associated with specific
     *         user or null if one not present.
     */
    public PersistentLogin getPersistentLogin(final String username)
    {
        try
        {
            return loginMapper.findByAccountId(username);
        } 
        catch (Exception e)
        {
            // swallow exception and return null, not getting "remember-me"
            // token should not present exception as it only forces them
            // to login again.
            logger.error("Error loading persistent login information for: "
                    + username, e);
            return null;
        }
    }

    /**
     * Removes all PersistentLogin info associated with a specific user from
     * data store.
     * 
     * @param username
     *            The username.
     */
    @Transactional
    public void removePersistentLogin(final String username)
    {
        loginMapper.deletePersistentLogin(username);
    }

}
