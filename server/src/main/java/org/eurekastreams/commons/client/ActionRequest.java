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
package org.eurekastreams.commons.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The ActionRequest interface.
 * 
 * @param <T>
 *            The generic type implementations will work with.
 */
public interface ActionRequest<T extends Serializable>
{
    /**
     * Getter for Id.
     * 
     * @return the action id.
     */
    Integer getId();

    /**
     * Setter for Id.
     * 
     * @param inId
     *            the Id to set.
     */
    void setId(final Integer inId);

    /**
     * Getter for action key.
     * 
     * @return The action key.
     */
    String getActionKey();

    /**
     * Setter for action key.
     * 
     * @param inActionKey
     *            The Id to set.
     */
    void setActionKey(final String inActionKey);

    /**
     * Getter for param.
     * 
     * @return ServiceAction param.
     */
    Serializable getParam();

    /**
     * Setter for param.
     * 
     * @param inParam
     *            ServiceAction param.
     */
    void setParam(final Serializable inParam);

    /**
     * Getter.
     * 
     * @return the ServerAction response
     */
    T getResponse();

    /**
     * Setter.
     * 
     * @param inResponse
     *            the ServerAction's response
     */
    void setResponse(final T inResponse);

    /**
     * Adds callback.
     * 
     * @param callback
     *            The callback to add.
     */
    void addCallback(AsyncCallback<T> callback);

    /**
     * Executes the callbacks.
     * 
     * @param inResponse
     *            The response object.
     */
    void executeCallbacks(T inResponse);

    /**
     * Gets the session id.
     * 
     * @return the session id.
     */
    String getSessionId();

    /**
     * Sets session id.
     * 
     * @param inSessionId
     *            the session id.
     */
    void setSessionId(final String inSessionId);
}
