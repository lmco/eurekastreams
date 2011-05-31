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

import org.eurekastreams.server.search.modelview.UsageMetricDTO;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.eurekastreams.web.client.events.data.GotUsageMetricSummaryEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model to register usageMetric with server.
 */
public class UsageMetricModel extends BaseModel implements Insertable<UsageMetricDTO>, Fetchable<Integer>
{
    /**
     * Singleton.
     */
    private static UsageMetricModel model = new UsageMetricModel();

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
    public void insert(final UsageMetricDTO inRequest)
    {
        // send it to server.
        // specify empty onFailure, don't want user to see anything if error in metrics collection.
        super.callWriteAction("registerUsageMetric", inRequest, null, new OnFailureCommand()
        {
            public void onFailure(final Throwable inEx)
            {
                // no-op
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Integer inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getUsageMetricSummaryAction", new UsageMetricStreamSummaryRequest(inRequest, null),
                new OnSuccessCommand<UsageMetricSummaryDTO>()
                {
                    public void onSuccess(final UsageMetricSummaryDTO response)
                    {
                        Session.getInstance().getEventBus().notifyObservers(new GotUsageMetricSummaryEvent(response));
                    }
                }, inUseClientCacheIfAvailable);

    }

}
