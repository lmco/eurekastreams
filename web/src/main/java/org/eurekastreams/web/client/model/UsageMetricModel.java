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
import java.util.Map.Entry;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;
import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.web.client.events.data.GotUsageMetricSummaryEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model to register page views with server.
 */
public class UsageMetricModel extends BaseModel implements Insertable<HashMap<String, Serializable>>,
        Fetchable<Integer>
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
     * Values key.
     */
    public static final String VIEWS_KEY = "views";

    /**
     * Last viewed page.
     */
    private String lastPageView = null;

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
    public void insert(final HashMap<String, Serializable> inRequest)
    {
        // grab values from param map.
        Page incomingPage = (Page) inRequest.get(UsageMetricModel.PAGE_KEY);
        String incomingView = (String) inRequest.get(VIEWS_KEY) == null ? "" : (String) inRequest.get(VIEWS_KEY);
        String incomingPageView = (incomingPage + incomingView);

        // get metric flags.
        boolean isStreamView = isStreamView(inRequest);
        boolean isPageView = !incomingPageView.equalsIgnoreCase(lastPageView);

        // register as last viewed page.
        lastPageView = incomingPageView;

        // short circuit here if not page or stream view (user bouncing around non-stream tabs).
        if (isPageView || isStreamView)
        {
            // create the metric dto object.
            UsageMetricDTO metric = new UsageMetricDTO(isPageView, isStreamView);
            metric.setMetricDetails(generateMetricDetails(inRequest));

            // send it to server.
            // specify empty onFailure, don't want user to see anything if error in metrics collection.
            super.callWriteAction("registerUsageMetric", metric, null, new OnFailureCommand()
            {
                public void onFailure(final Throwable inEx)
                {
                    // no-op
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Integer inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getUsageMetricSummaryAction", inRequest, new OnSuccessCommand<UsageMetricSummaryDTO>()
        {
            public void onSuccess(final UsageMetricSummaryDTO response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotUsageMetricSummaryEvent(response));
            }
        }, inUseClientCacheIfAvailable);

    }

    /**
     * Returns true if inRequest info indicates page displays a stream.
     * 
     * @param inRequest
     *            current page Page and parameter values.
     * @return true if inRequest info indicates page displays a stream, false otherwise.
     */
    @SuppressWarnings("unchecked")
    private boolean isStreamView(final HashMap<String, Serializable> inRequest)
    {
        Page page = (Page) inRequest.get(UsageMetricModel.PAGE_KEY);
        HashMap<String, String> values = (HashMap<String, String>) inRequest.get(UsageMetricModel.VALUES_KEY);

        switch (page)
        {
        case ACTIVITY:
            return true;
        case PEOPLE:
            return isDefaultOrStreamTab(values);
        case GROUPS:
            return isDefaultOrStreamTab(values);
        case ORGANIZATIONS:
            return isDefaultOrStreamTab(values);
        default:
            return false;
        }
    }

    /**
     * Returns true if parameter map is null, empty, or contains tab/Stream key/value.
     * 
     * @param inValues
     *            History parameters.
     * @return true if values is null, empty, or contains tab/Stream key/value.
     */
    private boolean isDefaultOrStreamTab(final HashMap<String, String> inValues)
    {
        if (inValues == null || inValues.isEmpty()
                || (inValues.containsKey("tab") && inValues.get("tab").equalsIgnoreCase("Stream")))
        {
            return true;
        }
        return false;
    }

    /**
     * Generate metric details from incoming request map.
     * 
     * @param inRequest
     *            incoming request map.
     * @return metric details from incoming request map.
     */
    @SuppressWarnings("unchecked")
    private String generateMetricDetails(final HashMap<String, Serializable> inRequest)
    {
        Page page = (Page) inRequest.get(PAGE_KEY);
        HashMap<String, String> values = (HashMap<String, String>) inRequest.get(VALUES_KEY);
        String views = (String) inRequest.get(VIEWS_KEY);

        StringBuffer metricDetails = new StringBuffer();
        metricDetails.append("Page: ");
        metricDetails.append(page == null ? "NA" : (page.toString() == "" ? "start" : page.toString()) + " ");
        metricDetails.append("Values: ");

        if (values != null && !values.isEmpty())
        {
            for (Entry<String, String> entry : values.entrySet())
            {
                metricDetails.append(entry.getKey() + "=" + entry.getValue() + " ");
            }
        }
        else
        {
            metricDetails.append("NA");
        }

        metricDetails.append(" Views: ");
        metricDetails.append(views == null ? "NA" : views + " ");

        return metricDetails.toString();
    }

}
