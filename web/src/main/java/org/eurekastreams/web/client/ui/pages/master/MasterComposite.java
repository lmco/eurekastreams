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
package org.eurekastreams.web.client.ui.pages.master;

import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.web.client.events.GetTutorialVideoResponseEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.model.TutorialVideoModel;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PeriodicEventManager;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.FooterComposite;
import org.eurekastreams.web.client.ui.common.HeaderComposite;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.optoutvideo.OptOutableVideoDialogContent;
import org.eurekastreams.web.client.ui.common.notifier.UINotifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A "Master" page, has a header and a footer and content.
 */
public class MasterComposite extends Composite implements Bindable
{
    /**
     * Panel to use.
     */
    FlowPanel panel;

    /**
     * The header panel.
     */
    FlowPanel headerPanel = new FlowPanel();

    /**
     * The site Labing Text.
     */
    String siteLabelingText = "";
    /**
     * The footer panel.
     */
    FooterComposite footerPanel = new FooterComposite();

    /**
     * The content panel.
     */
    FlowPanel contentPanel = new FlowPanel();

    /**
     * Wraps the content panel.
     */
    FlowPanel mainContents = new FlowPanel();

    /**
     * The actino processor to use.
     */
    private final ActionProcessor actionProcessor;

    /**
     * The composite representing the content.
     */
    Composite contentComposite;

    /**
     * The controller.
     */
    private final MasterCompositeController myController;

    /**
     * The header panel.
     */
    private HeaderComposite header = new HeaderComposite();

    /**
     * Banner.
     */
    private BannerPanel banner = new BannerPanel();

    /**
     * Notifier.
     */
    UINotifier notifier = new UINotifier();

    /**
     * Page factory.
     */
    private PageFactory factory = new PageFactory();

    /**
     * Tracks page we are currently on.
     */
    private Page currentPage;

    /**
     * List of the currentViews.
     */
    private List<String> currentViews;

    /** To tell event manager when system is active. */
    private PeriodicEventManager evtMgr;

    /**
     * Have any pages been loaded (prevents an infinite loop for IE only start page refreshing.).
     */
    private boolean pageHasBeenLoaded = false;

    /**
     * Default constructor.
     *
     */
    public MasterComposite()
    {
        actionProcessor = Session.getInstance().getActionProcessor();

        evtMgr = Session.getInstance().getPeriodicEventManager();

        panel = new FlowPanel()
        {
            @Override
            public void onBrowserEvent(final Event ev)
            {
                super.onBrowserEvent(ev);
                evtMgr.userActivityDetected();
            }
        };
        panel.sinkEvents(Event.KEYEVENTS | Event.FOCUSEVENTS | Event.MOUSEEVENTS & ~Event.ONMOUSEMOVE);
        panel.addStyleName("main");

        headerPanel.addStyleName("header-container");

        notifier.addStyleName("master-notifier");
        panel.add(notifier);

        myController =
                new MasterCompositeController(this, actionProcessor, new WidgetJSNIFacadeImpl(), Session
                        .getInstance(), Session.getInstance().getEventBus());

        PropertyMapper mapper =
                new PropertyMapper(GWT.create(MasterComposite.class), GWT.create(MasterCompositeController.class));

        mapper.bind(this, myController);

        mainContents.addStyleName("main-contents");
        mainContents.add(headerPanel);

        mainContents.add(contentPanel);
        contentPanel.addStyleName("content");
        panel.add(mainContents);

        myController.setTimer(new Timer()
        {
            @Override
            public void run()
            {
                myController.keepAlive();
            }
        });

        initWidget(panel);
        Session.getInstance().getEventBus().addObserver(GetTutorialVideoResponseEvent.class,
                new Observer<GetTutorialVideoResponseEvent>()
                {
                    public void update(final GetTutorialVideoResponseEvent event)
                    {
                        Dialog dialog = null;

                        HashSet<TutorialVideoDTO> tutVids = event.getResponse();

                        for (TutorialVideoDTO vid : tutVids)
                        {
                            if (vid.getPage() == currentPage)
                            {
                                if (currentPage == Page.PEOPLE
                                        && !(currentViews.contains(Session.getInstance().getCurrentPerson()
                                                .getAccountId())))
                                {
                                    // if you are on the person profile tab but it's not you then don't show this
                                    // dialog.
                                    break;
                                }
                                if (!(Session.getInstance().getCurrentPerson().getOptOutVideos().contains(vid
                                        .getEntityId())))
                                {
                                    OptOutableVideoDialogContent dialogContent =
                                            new OptOutableVideoDialogContent(vid);
                                    dialog = new Dialog(dialogContent);
                                    dialog.addCloseButtonListener(dialogContent.closeDialog());
                                    dialog.setBgVisible(true);
                                    dialog.setModal(true);
                                    dialog.show();

                                    Integer videoWidth = OptOutableVideoDialogContent.DEFAULT_VIDEO_WIDTH;
                                    if (vid.getVideoWidth() != null)
                                    {
                                        videoWidth = vid.getVideoWidth();
                                    }
                                    dialog.setWidth(videoWidth + OptOutableVideoDialogContent.CONTENT_WIDTH
                                            + OptOutableVideoDialogContent.MARGIN_OFFSET + "px");
                                    dialog.center();
                                    dialog.setPopupPosition(dialog.getAbsoluteLeft(),
                                            OptOutableVideoDialogContent.DIALOG_HEIGHT_OFFSET);
                                }
                                break;
                            }
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(SwitchedHistoryViewEvent.class,
                new Observer<SwitchedHistoryViewEvent>()
                {
                    public void update(final SwitchedHistoryViewEvent event)
                    {
                        if (pageHasBeenLoaded && getUserAgent().contains("msie 7")
                                && event.getPage().equals(Page.START))
                        {
                            Location.reload();
                            return;
                        }
                        mainContents.remove(banner);
                        notifier.setVisible(false);
                        contentPanel.clear();
                        contentPanel.add(factory.createPage(event.getPage(), event.getViews()));
                        currentPage = event.getPage();
                        currentViews = event.getViews();
                        pageHasBeenLoaded = true;
                        TutorialVideoModel.getInstance().fetch(null, true);

                    }
                });

        Session.getInstance().getEventBus().addObserver(SetBannerEvent.class, new Observer<SetBannerEvent>()
        {
            public void update(final SetBannerEvent event)
            {
                mainContents.insert(banner, 1);

                //Banner exists and should override the banner the theme is supplying. (i.e. profile page.)
                if (event.getBannerableEntity() != null)
                {
                    AvatarUrlGenerator urlGen = new AvatarUrlGenerator(null);
                    new WidgetJSNIFacadeImpl().setBanner(urlGen
                            .getBannerUrl(event.getBannerableEntity().getBannerId()));
                }
                //Start page, the bannerable entity is null, just clear out the banner value
                //to let the theme take over again.
                else
                {
                    new WidgetJSNIFacadeImpl().clearBanner(false);
                }
            }

        });
    }

    /**
     * Get the user agent (for detecting IE7).
     *
     * @return the user agent.
     */
    public static native String getUserAgent()
    /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;

    /**
     * Render header and footer.
     *
     */
    public void renderHeaderAndFooter()
    {
        Person person = Session.getInstance().getCurrentPerson();

        headerPanel.clear();
        headerPanel.add(getHeaderComposite(person));
    }

    /**
     * Get the header composite.
     *
     * @param viewer
     *            the user.
     * @return the header composite.
     */
    HeaderComposite getHeaderComposite(final Person viewer)
    {
        panel.add(footerPanel);
        header.render(viewer);
        actionProcessor.setQueueRequests(false);

        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        header.setSiteLabel(event.getResponse().getSiteLabel());
                        footerPanel.setSiteLabel(event.getResponse().getSiteLabel());
                    }

                });

        SystemSettingsModel.getInstance().fetch(null, true);

        return header;
    }
}
