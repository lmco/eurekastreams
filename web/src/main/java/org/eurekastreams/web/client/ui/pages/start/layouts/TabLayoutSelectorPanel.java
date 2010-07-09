/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.start.layouts;

import java.util.Date;

import org.eurekastreams.server.action.request.start.SetTabLayoutRequest;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.UpdatedStartPageLayoutResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.OLPanel;
import org.eurekastreams.web.client.ui.pages.start.preferences.PortalPreferenceFacade;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tab layout selector.
 */
public class TabLayoutSelectorPanel extends FlowPanel implements PortalPreferenceFacade
{
    /**
     * Default animation speed.
     */
    public static final int ANIMATION_SPEED = 600;

    /**
     * Default margin.
     */
    public static final int NAV_MARGIN = 40;

    /**
     * Currrently active layout.
     */
    private FlowPanel activeLayout = new FlowPanel();

    /**
     * Current layout.
     */
    private Layout currentLayout;

    /**
     * The carousel div.
     */
    private FlowPanel carousel = new FlowPanel();

    /**
     * If the carousel has loaded.
     */
    private boolean loaded = false;

    /**
     * Tab id.
     */
    private Long tabId;

    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacadeImpl jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * Constructor.
     *
     * @param inTabId
     *            the tab for the layout selector.
     * @param inLayout
     *            the layout.
     */
    public TabLayoutSelectorPanel(final Long inTabId, final Layout inLayout)
    {
        tabId = inTabId;

        this.addStyleName("yui-skin-sam");
        activeLayout.setStyleName("active-layout");
        this.add(activeLayout);
        this.add(carousel);
        setActiveLayout(inLayout);

        Session.getInstance().getEventBus().addObserver(UpdatedStartPageLayoutResponseEvent.class,
                new Observer<UpdatedStartPageLayoutResponseEvent>()
                {
                    public void update(final UpdatedStartPageLayoutResponseEvent event)
                    {
                        if (event.getResponse().getId() == inTabId)
                        {
                            setActiveLayout(event.getResponse().getTabLayout());
                        }
                    }
                });
    }

    /**
     * Called when the carousel loads.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!loaded)
        {
            Date date = new Date();
            String timeStamp = new Long(date.getTime()).toString();

            carousel.clear();
            carousel.getElement().setAttribute("id", "carousel" + timeStamp);
            OLPanel ulpanel = new OLPanel();
            carousel.add(ulpanel);

            for (final Layout layout : Layout.values())
            {
                Hyperlink icon = new Hyperlink();
                icon.addStyleName("layout-icon");
                icon.setText("layout");
                icon.setTargetHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest()));
                icon.addStyleName(layout.toString().toLowerCase());

                icon.addClickHandler(new ClickHandler()
                {
                    public void onClick(final ClickEvent event)
                    {
                        boolean shouldChange = true;
                        if (currentLayout.getNumberOfZones() > layout.getNumberOfZones())
                        {
                            shouldChange = jSNIFacade.confirm("You have a column that would be removed. "
                                    + "Should we move your gadgets for you?");
                        }

                        if (shouldChange)
                        {
                            Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                            StartTabsModel.getInstance().setLayout(new SetTabLayoutRequest(layout, tabId));
                        }

                    }
                });
                ulpanel.add(icon);
            }
            setupCarousel(timeStamp);
            loaded = true;
        }
    }

    /**
     * Setup the carousel with YUI.
     *
     * @param timeStamp
     *            used to allow multiple carousels.
     */
    private static native void setupCarousel(final String timeStamp) /*-{
                                        var carousel = new $wnd.YAHOO.widget.Carousel("carousel" + timeStamp);
                                        carousel.set("animation", { speed: 0.5 });
                                        carousel.set("numVisible", 5);
                                        carousel.set("scrollIncrement", 5);
                                        carousel.render();
                                        carousel.show();

                                     }-*/;

    /**
     * Set the active layout.
     *
     * @param layout
     *            the layout to use.
     */
    private void setActiveLayout(final Layout layout)
    {
        currentLayout = layout;
        Hyperlink layoutIcon = new Hyperlink();
        layoutIcon.addStyleName("layout-icon");
        layoutIcon.setText("layout");
        layoutIcon.addStyleName(layout.toString().toLowerCase());

        activeLayout.clear();
        activeLayout.add(layoutIcon);
        activeLayout.add(new Label("Currently Selected"));
    }

    /**
     * Get the layout title.
     *
     * @return the layout title.
     */
    @Override
    public String getTitle()
    {
        return "Change Layout";
    }

    /**
     * There is no title bar widget.
     *
     * @return nothing.
     */
    public Widget getTitleBarWidget()
    {
        return null;
    }

}
