/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.GetTutorialVideoResponseEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.UpdateRawHistoryEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.model.TutorialVideoModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.FooterComposite;
import org.eurekastreams.web.client.ui.common.HeaderComposite;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.optoutvideo.OptOutableVideoDialogContent;
import org.eurekastreams.web.client.ui.common.notifier.UINotifier;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A "Master" page, has a header and a footer and content.
 */
public class MasterComposite extends Composite
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
     * The Site Labing panel.
     */
    FlowPanel siteLabelingContainer = new FlowPanel();

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
     * The header panel.
     */
    private final HeaderComposite header = new HeaderComposite();

    /**
     * Banner.
     */
    private final BannerPanel banner = new BannerPanel();

    /**
     * Notifier.
     */
    UINotifier notifier = new UINotifier();

    /**
     * Page factory.
     */
    private final PageFactory factory = new PageFactory();

    /**
     * Tracks page we are currently on.
     */
    private Page currentPage;

    /**
     * List of the currentViews.
     */
    private List<String> currentViews;

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

        panel = new FlowPanel();
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().main());

        headerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().headerContainer());

        siteLabelingContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().siteLabeling());

        notifier.addStyleName(StaticResourceBundle.INSTANCE.coreCss().masterNotifier());
        panel.add(notifier);

        panel.add(headerPanel);

        mainContents.addStyleName(StaticResourceBundle.INSTANCE.coreCss().mainContents());
        mainContents.add(siteLabelingContainer);
        mainContents.add(contentPanel);
        contentPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().content());
        panel.add(mainContents);

        initWidget(panel);

        Session.getInstance().getEventBus().addObserver(GetTutorialVideoResponseEvent.class,
                new Observer<GetTutorialVideoResponseEvent>()
                {
            public void update(final GetTutorialVideoResponseEvent event)
            {
                HashSet<TutorialVideoDTO> tutVids = event.getResponse();
                PersonModelView currentPerson = Session.getInstance().getCurrentPerson();

                for (TutorialVideoDTO vid : tutVids)
                {
                    if (vid.getPage() == currentPage)
                    {
                        if (currentPage == Page.PEOPLE
                                && !(currentViews.contains(currentPerson.getAccountId())))
                        {
                            // if you are on the person profile tab but it's not you then don't show this
                            // dialog.
                            break;
                        }
                        if (!(currentPerson.getOptOutVideos().contains(vid.getEntityId())))
                        {
                            final Integer videoWidth = vid.getVideoWidth() != null ? vid.getVideoWidth()
                                    : OptOutableVideoDialogContent.DEFAULT_VIDEO_WIDTH;

                            OptOutableVideoDialogContent dialogContent = new OptOutableVideoDialogContent(vid);
                            Dialog dialog = new Dialog(dialogContent)
                            {
                                {
                                    getPopupPanel().setModal(true);
                                }

                                @Override
                                public void center()
                                {
                                    getPopupPanel().setWidth(
                                            videoWidth + OptOutableVideoDialogContent.CONTENT_WIDTH
                                            + OptOutableVideoDialogContent.MARGIN_OFFSET + "px");
                                    super.center();
                                    getPopupPanel().setPopupPosition(getPopupPanel().getAbsoluteLeft(),
                                            OptOutableVideoDialogContent.DIALOG_HEIGHT_OFFSET);
                                }
                            };
                            dialog.showUncentered();
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
                mainContents.remove(banner);
                notifier.setVisible(false);
                contentPanel.clear();
                String redirect = factory.createPage(event.getPage(), event.getViews(), contentPanel);
                currentPage = event.getPage();
                currentViews = event.getViews();
                pageHasBeenLoaded = true;
                if (redirect == null)
                {
                    TutorialVideoModel.getInstance().fetch(null, true);
                }
                else
                {
                    EventBus.getInstance().notifyObservers(new UpdateRawHistoryEvent(redirect));
                }
            }
                });

        Session.getInstance().getEventBus().addObserver(SetBannerEvent.class, new Observer<SetBannerEvent>()
                {
            public void update(final SetBannerEvent event)
            {
                mainContents.insert(banner, 1);

                // Banner exists and should override the banner the theme is supplying. (i.e. profile page.)
                if (event.getBannerableEntity() != null)
                {
                    AvatarUrlGenerator urlGen = new AvatarUrlGenerator(null);
                    new WidgetJSNIFacadeImpl()
                    .setBanner(urlGen.getBannerUrl(event.getBannerableEntity().getBannerId()));
                }
                // Start page, the bannerable entity is null, just clear out the banner value
                // to let the theme take over again.
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
        PersonModelView person = Session.getInstance().getCurrentPerson();

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
    HeaderComposite getHeaderComposite(final PersonModelView viewer)
    {
        panel.add(footerPanel);
        header.render(viewer);

        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
            public void update(final GotSystemSettingsResponseEvent event)
            {
                final SystemSettings settings = event.getResponse();

                setSiteLabelTemplate(settings.getHeaderTemplate(), settings.getSiteLabel());
                footerPanel.setSiteLabelTemplate(settings.getFooterTemplate(), settings.getSiteLabel());
                banner.getElement().setInnerHTML(settings.getBannerTemplate());
            }
                });

        SystemSettingsModel.getInstance().fetch(null, true);

        return header;
    }

    /**
     * Sets Site labeling.
     *
     * @param inTemplate
     *            HTML template content to insert in the footer.
     * @param inSiteLabel
     *            The text for Site Labeling.
     */
    public void setSiteLabelTemplate(final String inTemplate, final String inSiteLabel)
    {
        String siteLabel = inSiteLabel == null ? "" : inSiteLabel;
        String template = inTemplate.replace("%SITELABEL%", siteLabel);
        siteLabelingContainer.getElement().setInnerHTML(template);
    }
}
