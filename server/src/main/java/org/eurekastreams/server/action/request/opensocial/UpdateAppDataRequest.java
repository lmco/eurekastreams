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
package org.eurekastreams.server.action.request.opensocial;

import java.util.HashMap;

/**
 * Request for updating application data.
 * 
 */
public class UpdateAppDataRequest extends GetAppDataRequest
{
    /**
     * Map of app data values.
     */
    private HashMap<String, String> appDataValues;

    /**
     * Constructor.
     */
    private UpdateAppDataRequest()
    {
        // no-op
    }

    /**
     * Constructor for the Request object.
     * 
     * @param inApplicationId
     *            - instance of the application id making the request.
     * @param inOpenSocialId
     *            - opensocial id of the
     * @param inAppDataValues
     *            Map of app data values.
     */
    public UpdateAppDataRequest(final Long inApplicationId, final String inOpenSocialId,
            final HashMap<String, String> inAppDataValues)
    {
        super(inApplicationId, inOpenSocialId);
        appDataValues = inAppDataValues;

    }

    /**
     * @return the appDataValues
     */
    public HashMap<String, String> getAppDataValues()
    {
        return appDataValues;
    }

    /**
     * @param inAppDataValues
     *            the appDataValues to set
     */
    public void setAppDataValues(final HashMap<String, String> inAppDataValues)
    {
        appDataValues = inAppDataValues;
    }
}
