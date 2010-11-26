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
package org.eurekastreams.web.client.ui.common;

import java.util.HashMap;

import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerUpdatedEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Pager control. To use this control, subscribe to the PagerUpdatedEvent and access the start and end indexes and
 * refresh your data.
 *
 */
public class Pager extends FlowPanel
{
    /** Name of URL parameter for start index. */
    public static final String URL_PARAM_START_INDEX = "startIndex";

    /** Name of URL parameter for end index. */
    public static final String URL_PARAM_END_INDEX = "endIndex";

    /**
     * The pager id allows us to have multiple of these pagers on the screen that can all update each other but not
     * other pagers that remain unaffected.
     */
    private String pagerId = "pager";
    /**
     * Default page size.
     */
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    /**
     * Default total.
     */
    private static final Integer DEFAULT_TOTAL = DEFAULT_PAGE_SIZE + 1;
    /**
     * Start index.
     */
    private Integer startIndex = 0;
    /**
     * End index.
     */
    private Integer endIndex = 9;
    /**
     * Page size.
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    /**
     * The total. Set to 11 temporarily until we find out how many we have.
     */
    private Integer total = DEFAULT_TOTAL;

    /**
     * Total label.
     */
    private final Label totalLabel = new Label("");

    /**
     * The back page button.
     */
    private final Anchor prev = new Anchor("previous");
    /**
     * The forward page button.
     */
    private final Anchor next = new Anchor("next");

    /**
     * Default constructor.
     *
     * @param inPagerId
     *            the pager id.
     * @param showPageButtons
     *            show page buttons.
     */
    public Pager(final String inPagerId, final boolean showPageButtons)
    {
        pagerId = inPagerId;
        this.addStyleName("pager-container");
        final Pager thisBuffered = this;

        if (showPageButtons)
        {
            this.add(next);
            next.addStyleName("pager-forward");
            next.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (endIndex + 1 < total)
                    {
                        startIndex += pageSize;
                        endIndex += pageSize;
                        Session.getInstance().getEventBus().notifyObservers(new PagerUpdatedEvent(thisBuffered));

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(URL_PARAM_START_INDEX, startIndex.toString());
                        params.put(URL_PARAM_END_INDEX, endIndex.toString());

                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(params, false)));
                    }
                }
            });

            this.add(prev);
            prev.addStyleName("pager-backward");
            prev.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (startIndex - pageSize >= 0)
                    {
                        startIndex -= pageSize;
                        endIndex -= pageSize;
                        Session.getInstance().getEventBus().notifyObservers(new PagerUpdatedEvent(thisBuffered));

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(URL_PARAM_START_INDEX, startIndex.toString());
                        params.put(URL_PARAM_END_INDEX, endIndex.toString());

                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(params, false)));
                    }
                }
            });
        }

        this.add(totalLabel);
        totalLabel.addStyleName("pager-range");

        refreshState();

        Session.getInstance().getEventBus().addObserver(PagerUpdatedEvent.class, new Observer<PagerUpdatedEvent>()
        {
            public void update(final PagerUpdatedEvent event)
            {
                if (event.getPager().getPagerId().equals(pagerId))
                {
                    endIndex = event.getPager().getEndIndex();
                    total = event.getPager().total;
                    startIndex = event.getPager().getStartIndex();

                    refreshState();
                }
            }
        });
    }

    /**
     * Reset the pager.
     */
    public void reset()
    {
        startIndex = 0;
        endIndex = 9;
        //total = DEFAULT_TOTAL;
        Session.getInstance().getEventBus().notifyObservers(new PagerUpdatedEvent(this));
    }

    /**
     * Refresh the state of the pager.
     */
    private void refreshState()
    {
        if (total <= pageSize)
        {
            next.setVisible(false);
            prev.setVisible(false);
        }
        else
        {
            next.setVisible(true);
            prev.setVisible(true);

            if (endIndex + 1 < total)
            {
                next.removeStyleName("pager-forward-disabled");
            }
            else
            {
                next.addStyleName("pager-forward-disabled");
            }

            if (startIndex != 0)
            {
                prev.removeStyleName("pager-backward-disabled");
            }
            else
            {
                prev.addStyleName("pager-backward-disabled");
            }
        }

        Integer toIndex = endIndex + 1;

        if (total < toIndex)
        {
            toIndex = total;
        }

        String rangeText = total == 0 ? "0 - 0 of 0" : (startIndex + 1) + " - " + toIndex + " of " + total;
        totalLabel.setText(rangeText);
    }

    /**
     * Set the page size. Default is 10.
     *
     * @param inPageSize
     *            page size.
     */
    public void setPageSize(final Integer inPageSize)
    {
        pageSize = inPageSize;
        endIndex = pageSize - 1;
        Session.getInstance().getEventBus().notifyObservers(new PagerUpdatedEvent(this));
    }

    /**
     * Set the total.
     *
     * @param inTotal
     *            the total.
     */
    public void setTotal(final Integer inTotal)
    {
        total = inTotal;
        Session.getInstance().getEventBus().notifyObservers(new PagerUpdatedEvent(this));

    }

    /**
     * Get the start index.
     *
     * @return the start index.
     */
    public Integer getStartIndex()
    {
        return startIndex;
    }

    /**
     * Set the startIndex.
     *
     * @param inStartIndex
     *            the startIndex.
     */
    public void setStartIndex(final Integer inStartIndex)
    {
        startIndex = inStartIndex;
    }

    /**
     * Get the end index.
     *
     * @return the end index.
     */
    public Integer getEndIndex()
    {
        return endIndex;
    }

    /**
     * Set the endIndex.
     *
     * @param inEndIndex
     *          the endIndex.
     */
    public void setEndIndex(final Integer inEndIndex)
    {
        endIndex = inEndIndex;
    }

    /**
     * Get the pager id.
     *
     * @return the pager id.
     */
    public String getPagerId()
    {
        return pagerId;
    }

    /**
     * @return the page size.
     */
    public Integer getPageSize()
    {
        return pageSize;
    }
}
