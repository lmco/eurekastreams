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
package org.eurekastreams.web.client.ui.common.pager;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.ui.common.animation.SlideAnimation;
import org.eurekastreams.web.client.ui.common.animation.SlideAnimation.Direction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pager composite.
 */
public class PagerComposite extends Composite
{
    /**
     * Binder for building UI.
     */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * 
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, PagerComposite>
    {
    }

    /**
     * CSS resource.
     */
    interface PagerStyle extends CssResource
    {
        /**
         * Paging disabled style.
         * 
         * @return paging disabled.
         */
        String pagingDisabled();
    }

    /**
     * CSS style.
     */
    @UiField
    PagerStyle style;

    /**
     * Page results.
     */
    @UiField
    FlowPanel pageResults;

    /**
     * Previous button.
     */
    @UiField
    Label prevButton;

    /**
     * Next button.
     */
    @UiField
    Label nextButton;

    /**
     * Button container.
     */
    @UiField
    FlowPanel buttonContainer;

    /**
     * Slide left animation.
     */
    private SlideAnimation slideAnimation = new SlideAnimation();

    /**
     * Pager strategy.
     */
    private PagerStrategy pagerStrategy = null;

    /**
     * Current direction.
     */
    private Direction direction;

    /**
     * Pager animation time.
     */
    private static final int PAGER_ANIMATION_TIME = 500;

    /**
     * Default constructor.
     */
    public PagerComposite()
    {
        initWidget(binder.createAndBindUi(this));
        buttonContainer.setVisible(false);

        buildPage();

    }

    /**
     * Initialize with strategy.
     * 
     * @param inPagerStrategy
     *            the strategy.
     */
    public void init(final PagerStrategy inPagerStrategy)
    {
        pagerStrategy = inPagerStrategy;

        EventBus.getInstance().addObserver(PagerResponseEvent.class, new Observer<PagerResponseEvent>()
        {
            public void update(final PagerResponseEvent event)
            {
                if (event.getKey().equals(pagerStrategy.getKey()))
                {
                    buttonContainer.setVisible(true);

                    if (pageResults.getWidgetCount() != 0)
                    {
                        slideAnimation.slide(direction, event.getWidget(), pageResults, PAGER_ANIMATION_TIME);
                    }
                    else
                    {
                        pageResults.add(event.getWidget());
                    }

                    if (!pagerStrategy.hasNext())
                    {
                        nextButton.addStyleName(style.pagingDisabled());
                    }
                    else
                    {
                        nextButton.removeStyleName(style.pagingDisabled());
                    }

                    if (!pagerStrategy.hasPrev())
                    {
                        prevButton.addStyleName(style.pagingDisabled());
                    }
                    else
                    {
                        prevButton.removeStyleName(style.pagingDisabled());
                    }
                }
            }
        });
    }

    /**
     * Load components.
     */
    public void load()
    {
        buttonContainer.setVisible(false);
        pageResults.clear();
        pagerStrategy.init();
    }

    /**
     * Build page.
     */
    private void buildPage()
    {
        nextButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (pagerStrategy.hasNext())
                {
                    direction = Direction.Left;
                    pagerStrategy.next();
                }
            }
        });

        prevButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (pagerStrategy.hasPrev())
                {
                    direction = Direction.Right;
                    pagerStrategy.prev();
                }
            }
        });
    }
}
