/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.model;

/**
 * Establish the session.
 * 
 */
public class SessionEstablishModel extends BaseModel implements Fetchable<String>
{
    /**
     * Singleton.
     */
    private static SessionEstablishModel model = new SessionEstablishModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static SessionEstablishModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final String request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("noOperation", request, new OnSuccessCommand<Integer>()
        {
            public void onSuccess(final Integer response)
            {
                
            }
        }, new OnFailureCommand()
        {
            public void onFailure(final Throwable ex)
            {

            }
        }, useClientCacheIfAvailable);
    }
}
