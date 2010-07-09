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
package org.eurekastreams.server.persistence.exceptions;

/**
 * Exception thrown when a problem occurs when the user tries to undelete a Tab.
 * 
 */
public class TabUndeletionException extends Exception
{
    /**
     * Serializer version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The the tab id the user attempted to undelete.
     */
    private long tabId;

    /**
     * Constructor, taking the error message, the accountId of the requesting
     * user, the tab id to undelete.
     * 
     * @param message
     *            The error message.
     * @param inTabId
     *            The tab id the user attempted to undelete.
     */
    public TabUndeletionException(final String message, final long inTabId)
    {
        super(message);
        this.tabId = inTabId;
    }

    /**
     * Constructor, taking the embedded Exception, the accountId of the
     * requesting user, the tab id to undelete.
     * 
     * @param message
     *            The error message.
     * @param cause
     *            The root exception.
     * @param inTabId
     *            The tab id the user attempted to undelete.
     */
    public TabUndeletionException(final String message, final Throwable cause, final long inTabId)
    {
        super(message, cause);
        this.tabId = inTabId;
    }

    /**
     * Get the tab id the user attempted to undelete.
     * 
     * @return the tab id the user attempted to undelete.
     */
    public long getTabId()
    {
        return tabId;
    }

}
