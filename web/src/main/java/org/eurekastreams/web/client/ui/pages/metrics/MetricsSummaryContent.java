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
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotUsageMetricSummaryEvent;
import org.eurekastreams.web.client.model.UsageMetricModel;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page for displaying metrics.
 */
public class MetricsSummaryContent extends Composite
{
    /** Default record count. */
    private static final int DEFAULT_RECORD_COUNT = 30;

    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement uniqueVisitorsUi;

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement visitsPerVisitorUi;

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement streamViewersUi;

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement streamViewsPerSpectatorUi;

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement postersUi;

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement messagesPerContributorUi;

    /** UI element displaying applicable metric. */
    @UiField
    SpanElement averageTimeToResponseUi;

    /**
     * Constructor.
     * 
     * @param inViewStreamScopeIdString
     *            if not empty, represents a stream scope id to get the stats for
     */
    public MetricsSummaryContent(final String inViewStreamScopeIdString)
    {
        initWidget(binder.createAndBindUi(this));

        // get the system settings, asynchronously
        Session.getInstance().getEventBus().addObserver(GotUsageMetricSummaryEvent.class,
                new Observer<GotUsageMetricSummaryEvent>()
                {
                    public void update(final GotUsageMetricSummaryEvent event)
                    {
                        // got the metrics - remove the observer
                        Session.getInstance().getEventBus().removeObserver(event, this);
                        buildPage(event.getResponse());
                    }
                });

        Long streamScopeId = null;
        if (inViewStreamScopeIdString != null && inViewStreamScopeIdString.length() > 0)
        {
            streamScopeId = Long.parseLong(inViewStreamScopeIdString);
        }
        UsageMetricModel.getInstance().fetch(new UsageMetricStreamSummaryRequest(DEFAULT_RECORD_COUNT, streamScopeId),
                true);
    }

    /**
     * Build the page.
     * 
     * @param inMetrics
     *            the UsageMetricSummaryDTO.
     */
    private void buildPage(final UsageMetricSummaryDTO inMetrics)
    {
        // displayed directly
        long uniqueVisitorCount = inMetrics.getUniqueVisitorCount();
        long streamViewerCount = inMetrics.getStreamViewerCount();
        long streamViewCount = inMetrics.getStreamViewCount();
        long avgActivityResponseTime = inMetrics.getAvgActivityResponseTime();

        // used in calculations
        long streamContributorCount = inMetrics.getStreamContributorCount();
        long pageViewCount = inMetrics.getPageViewCount();
        long messageCount = inMetrics.getMessageCount();

        // calculated and displayed.
        double pageViewsPerUniqueVisitor = uniqueVisitorCount == 0 ? 0 : (double) pageViewCount
                / (double) uniqueVisitorCount;

        double streamViewsPerStreamViewer = streamViewerCount == 0 ? 0 : (double) streamViewCount
                / (double) streamViewerCount;

        double messagesPostedPerStreamContributor = streamContributorCount == 0 ? 0 : (double) messageCount
                / (double) streamContributorCount;

        NumberFormat formatter = NumberFormat.getFormat("0.0");

        uniqueVisitorsUi.setInnerText(Long.toString(uniqueVisitorCount));
        visitsPerVisitorUi.setInnerText(formatter.format(pageViewsPerUniqueVisitor));
        streamViewersUi.setInnerText(Long.toString(streamViewerCount));
        streamViewsPerSpectatorUi.setInnerText(formatter.format(streamViewsPerStreamViewer));
        postersUi.setInnerText(Long.toString(streamContributorCount));
        messagesPerContributorUi.setInnerText(formatter.format(messagesPostedPerStreamContributor));
        averageTimeToResponseUi.setInnerText(Long.toString(avgActivityResponseTime));
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, MetricsSummaryContent>
    {
    }
}
