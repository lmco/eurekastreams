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

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;

/**
 * Model to register page views with server.
 */
public class UsageMetricModel extends BaseModel implements Insertable<HashMap<String, Serializable>>
{
    /**
     * Singleton.
     */
    private static UsageMetricModel model = new UsageMetricModel();

    /**
     * Page key.
     */
    public static final String PAGE_KEY = "page";

    /**
     * Values key.
     */
    public static final String VALUES_KEY = "values";

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static UsageMetricModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final HashMap<String, Serializable> request)
    {
        Page page = (Page) request.get(UsageMetricModel.PAGE_KEY);
        // HashMap<String, String> values = (HashMap<String, String>) request.get(UsageMetricModel.VALUES_KEY);

        UsageMetricDTO metric = new UsageMetricDTO();

        switch (page)
        {
        case ACTIVITY:
            metric.setStreamView(true);
            break;
        default:
            break;
        }

        // specify empty onFailure, don't want user to see anything if error in metrics collection.
        super.callWriteAction("registerMetric", metric, null, new OnFailureCommand()
        {
            public void onFailure(final Throwable inEx)
            {
                // no-op
            }
        });
    }
}
