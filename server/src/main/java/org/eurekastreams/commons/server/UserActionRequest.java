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
package org.eurekastreams.commons.server;

import java.io.Serializable;

import org.springframework.security.userdetails.UserDetails;

/**
 * This class encapsulates in one object everything an action needs to process a request,
 * including passing this object as is to a producer that puts it on the queue to be
 * picked up by the consumer and passed to an async action.
 *
 */
public class UserActionRequest implements Serializable
{

    /**
	 * Generated serial version Id.
	 */
	private static final long serialVersionUID = -5787537910924233440L;

	/**
     * Uniquely represents one ServerAction.
     */
    private String actionKey;

    /**
     * User details.
     */
    private UserDetails user;

    /**
     * Parameters to be passed to the ServerAction.
     */
    private Serializable params = null;

    /**
     * Constructor.
     *
     * @param inActionKey
     *            the action to be called to respond to the request; this action is to implement {@link RequestAction}
     * @param inUser
     *            identify the request
     * @param inParams
     *            parameters to send to the request.
     */
    public UserActionRequest(final String inActionKey, final UserDetails inUser, final Serializable inParams)
    {
        actionKey = inActionKey;
        user = inUser;
        params = inParams;
    }

    /**
     * Getter.
     *
     * @return the action key
     */
    public String getActionKey()
    {
        return actionKey;
    }

    /**
     * Getter.
     *
     * @return parameters for the ServerAction
     */
    public Serializable getParams()
    {
        return params;
    }

    /**
     * Getter.
     *
     * @return the user
     */
    public UserDetails getUser()
    {
        return user;
    }

    /**
     * Setter.
     *
     * @param inUser
     *            the user
     */
    public void setUser(final UserDetails inUser)
    {
        this.user = inUser;
    }
    
    /**
     * Retrieve information about the user request overriding the toString method.
     * @return String description of the contents of this User Request.
     */
    @Override
    public String toString()
    {
        String stringOutput = "UserActionRequest actionKey: " + actionKey + " requesting user: " + user.getUsername();
        return stringOutput;
    }
}
