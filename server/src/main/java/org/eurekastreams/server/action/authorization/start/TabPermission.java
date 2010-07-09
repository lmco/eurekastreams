/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.authorization.start;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.db.GetTabPermissionByPersonAndTab;

/**
 * Class for tab permission checking.
 *
 */
public class TabPermission
{
    /**
     * Local instance of {@link GetTabPermissionByPersonAndTab}.
     */
    private GetTabPermissionByPersonAndTab tabPermissionsMapper;

    /**
     * logger.
     */
    private static Log logger = LogFactory.getLog(TabPermission.class);

    /**
     * Constructor.
     *
     * @param inTabPermissionsMapper
     *            The instance of {@link GetTabPermissionByPersonAndTab}.
     */
    public TabPermission(final GetTabPermissionByPersonAndTab inTabPermissionsMapper)
    {
        tabPermissionsMapper = inTabPermissionsMapper;
    }

    /**
     * Determines if current user can delete a given tab id. If the tab id represents a tab on the user's start page,
     * true is returned, false or AuthorizationException (depending on throwException param) otherwise.
     *
     * @param inAccountId
     *            Current user's string based account id.
     * @param inTabId
     *            The tab to be checked.
     * @param throwException
     *            Flag if method should throw AuthorizationException rather than returning false.
     * @return If the tab id represents a tab on the user's start page, true is returned, false or
     *         AuthorizationException (depending on throwException param) otherwise.
     */
    public boolean canDeleteStartPageTab(final String inAccountId, final Long inTabId, final boolean throwException)
    {

        AuthorizationException exception = throwException ? new AuthorizationException(
                "Insufficient permissions to delete tab.") : null;
        return isCurrentUserStartTab(inAccountId, inTabId, exception);
    }

    /**
     * Determines if current user can change layout of a given tab id. If the tab id represents a tab on the user's
     * start page, true is returned, false or AuthorizationException (depending on throwException param) otherwise.
     *
     * @param inAccountId
     *            Current user's String based account id..
     * @param inTabId
     *            The tab to be checked.
     * @param throwException
     *            Flag if method should throw AuthorizationException rather than returning false.
     * @return If the tab id represents a tab on the user's start page, true is returned, false or
     *         AuthorizationException (depending on throwException param) otherwise.
     */
    public boolean canChangeTabLayout(final String inAccountId, final Long inTabId, final boolean throwException)
    {
        AuthorizationException exception = throwException ? new AuthorizationException(
                "Insufficient permissions to change tab layout.") : null;
        return isCurrentUserStartTab(inAccountId, inTabId, exception);
    }

    /**
     * Determines if current user can modify gadgets of a given tab id. If the tab id represents a tab on the user's
     * start page, true is returned, false or AuthorizationException (depending on throwException param) otherwise.
     *
     * @param inAccountId
     *            Current user's account id.
     * @param inTabId
     *            The tab to be checked.
     * @param throwException
     *            Flag if method should throw AuthorizationException rather than returning false.
     * @return If the tab id represents a tab on the user's start page, true is returned, false or
     *         AuthorizationException (depending on throwException param) otherwise.
     */
    public boolean canModifyGadgets(final String inAccountId, final Long inTabId, final boolean throwException)
    {
        AuthorizationException exception = throwException ? new AuthorizationException(
                "Insufficient permissions to modify gadgets on tab.") : null;
        return isCurrentUserStartTab(inAccountId, inTabId, exception);
    }

    /**
     * Determines if current user can modify gadgets of a given tab id. If the tab id represents a tab on the user's
     * start page, true is returned, false or AuthorizationException (depending on throwException param) otherwise.
     *
     * @param inAccountId
     *            Current user's account id.
     * @param inTabId
     *            The tab to be checked.
     * @param throwException
     *            Flag if method should throw AuthorizationException rather than returning false.
     * @return If the tab id represents a tab on the user's start page, true is returned, false or
     *         AuthorizationException (depending on throwException param) otherwise.
     */
    public boolean canRenameTab(final String inAccountId, final Long inTabId, final boolean throwException)
    {
        AuthorizationException exception = throwException ? new AuthorizationException(
                "Insufficient permissions to rename tab.") : null;
        return isCurrentUserStartTab(inAccountId, inTabId, exception);
    }

    // TODO: Pull private methods out into strategies if this starts growing, but ok for now.

    /**
     * Returns true if tab is in user's start tab group, false (or exception) otherwise.
     *
     * @param inAccountId
     *            string based account id for the account to check permissions on.
     * @param inTabId
     *            tab to check.
     * @param exception
     *            Exception to throw if tab is not current users, null indicates no execption is to be thrown.
     * @return True if user has tab id in their start TabGroup, false (or exception) otherwise.
     */
    private boolean isCurrentUserStartTab(final String inAccountId, final Long inTabId, // \n
            final RuntimeException exception)
    {
        boolean hasPermission = false;
        try
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Entering isCurrentUserStartTab with tab id of: " + inTabId
                        + "and UserDetails for account id: " + inAccountId);
            }
            hasPermission = tabPermissionsMapper.execute(inAccountId, inTabId);
        }
        catch (Exception e)
        {
            logger.error("Caught exception checking StartPage tab permission: Denied", e);
        }

        if (!hasPermission && exception != null)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Permissions check failed.  Throwing runtime exception.", exception);
            }
            throw exception;
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Completed isCurrentUserStartTab with tab id of: " + inTabId
                    + "and UserDetails for account id: " + inAccountId + " and permissions " + hasPermission);
        }
        return hasPermission;
    }
}
