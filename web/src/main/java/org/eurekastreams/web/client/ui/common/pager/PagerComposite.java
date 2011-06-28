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

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
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
     * Pager strategy.
     */
    private PagerStrategy pagerStrategy = null;

    /**
     * Default constructor.
     */
    public PagerComposite()
    {
        initWidget(binder.createAndBindUi(this));
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
        pagerStrategy.init();
    }

    /**
     * Build page.
     */
    private void buildPage()
    {
        prevButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (pagerStrategy.hasNext())
                {
                    pagerStrategy.next();

                    if (!pagerStrategy.hasNext())
                    {
                        nextButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().nextArrowDisabled());
                    }
                }
            }
        });

        nextButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (pagerStrategy.hasPrev())
                {
                    pagerStrategy.prev();

                    if (!pagerStrategy.hasPrev())
                    {
                        prevButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().previousArrowDisabled());
                    }
                }
            }
        });
    }
}
