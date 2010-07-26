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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * Request object for setting person's account locked status.
 * 
 */
public class SetPersonLockedStatusRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1508368124319273001L;

    /**
     * person id.
     */
    private String personAccountId;

    /**
     * locked status.
     */
    private Boolean lockedStatus;

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            Person id.
     * @param inLockedStatus
     *            Locked status.
     */
    public SetPersonLockedStatusRequest(final String inPersonId, final Boolean inLockedStatus)
    {
        personAccountId = inPersonId;
        lockedStatus = inLockedStatus;
    }

    /**
     * @return the personId
     */
    public String getPersonAccountId()
    {
        return personAccountId;
    }

    /**
     * @return the lockedStatus
     */
    public Boolean getLockedStatus()
    {
        return lockedStatus;
    }

}
