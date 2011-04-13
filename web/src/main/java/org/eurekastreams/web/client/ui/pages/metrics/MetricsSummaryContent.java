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
package org.eurekastreams.web.client.ui.pages.metrics;

import org.eurekastreams.server.search.modelview.UsageMetricSummaryDTO;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotUsageMetricSummaryEvent;
import org.eurekastreams.web.client.model.UsageMetricModel;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Page for displaying metrics.
 * 
 */
public class MetricsSummaryContent extends Composite
{
    /**
     * Main panel.
     */
    private final FlowPanel panel;

    /**
     * Default record count.
     */
    private final int defaultRecordCount = 30;

    /**
     * Default constructor.
     */
    public MetricsSummaryContent()
    {
        panel = new FlowPanel();
        // panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().metrics());

        // get the system settings, asynchronously
        Session.getInstance().getEventBus().addObserver(GotUsageMetricSummaryEvent.class,
                new Observer<GotUsageMetricSummaryEvent>()
                {
                    public void update(final GotUsageMetricSummaryEvent event)
                    {
                        // got the metrics - remove the observer
                        Session.getInstance().getEventBus().removeObserver(GotUsageMetricSummaryEvent.class, this);
                        final UsageMetricSummaryDTO metrics = event.getResponse();

                        buildPage(metrics);
                    }
                });

        UsageMetricModel.getInstance().fetch(defaultRecordCount, true);
        initWidget(panel);
    }

    /**
     * Build the page.
     * 
     * @param inMetrics
     *            the UsageMetricSummaryDTO.
     */
    private void buildPage(final UsageMetricSummaryDTO inMetrics)
    {
        panel.add(new Label("Based on " + inMetrics.getRecordCount() + " daily records."));

        // displayed directly
        long uniqueVisitorCount = inMetrics.getUniqueVisitorCount();
        long streamViewerCount = inMetrics.getStreamViewerCount();
        long streamViewCount = inMetrics.getStreamViewCount();

        // used in calculations
        long streamContributorCount = inMetrics.getStreamContributorCount();
        long pageViewCount = inMetrics.getPageViewCount();
        long messageCount = inMetrics.getMessageCount();

        // calculated and displayed.
        double pageViewsPerUniqueVisitor = uniqueVisitorCount == 0 ? 0 : (double) pageViewCount
                / (double) uniqueVisitorCount;

        double streamViewsPerStreamViewer = uniqueVisitorCount == 0 ? 0 : (double) streamViewCount
                / (double) streamViewerCount;

        double messagesPostedPerStreamContributor = uniqueVisitorCount == 0 ? 0 : (double) messageCount
                / (double) streamContributorCount;

        NumberFormat formatter = NumberFormat.getFormat("0.0");

        panel.add(new Label("uniqueVisitorCount " + uniqueVisitorCount));
        panel.add(new Label("streamViewerCount " + streamViewerCount));
        panel.add(new Label("streamViewCount " + streamViewCount));
        panel.add(new Label("pageViewsPerUniqueVisitor " + formatter.format(pageViewsPerUniqueVisitor)));
        panel.add(new Label("streamViewsPerStreamViewer " + formatter.format(streamViewsPerStreamViewer)));
        panel.add(new Label("messagesPostedPerStreamContributor "
                + formatter.format(messagesPostedPerStreamContributor)));

    }
}
