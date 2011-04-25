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
package org.eurekastreams.web.client.ui.pages.start.gallery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eurekastreams.server.action.request.gallery.GetGalleryItemsRequest;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.GalleryPageLoadedEvent;
import org.eurekastreams.web.client.events.GotGadgetMetaDataEvent;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.ThemeChangedEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.UserLoggedInEvent;
import org.eurekastreams.web.client.events.data.GotGadgetDefinitionCategoriesResponseEvent;
import org.eurekastreams.web.client.events.data.GotGadgetDefinitionsResponseEvent;
import org.eurekastreams.web.client.events.data.GotStartPageTabsResponseEvent;
import org.eurekastreams.web.client.events.data.GotThemeDefinitionCategoriesResponseEvent;
import org.eurekastreams.web.client.events.data.GotThemeDefinitionsResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGadgetDefinitionResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedThemeResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGadgetDefinitionResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedThemeResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GadgetDefinitionCategoriesModel;
import org.eurekastreams.web.client.model.GadgetDefinitionModel;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.model.ThemeDefinitionCategoriesModel;
import org.eurekastreams.web.client.model.ThemeModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.notifier.UINotifier;
import org.eurekastreams.web.client.ui.common.pagedlist.GadgetMetaDataRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.SingleColumnPagedListRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.ThemeRenderer;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.common.tabs.TabContainerPanel;
import org.eurekastreams.web.client.ui.pages.master.MasterComposite;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The content page for the gallery.
 * 
 */
public class GalleryContent extends SettingsPanel
{
    /**
     * The tab container.
     */
    private TabContainerPanel portalPage;

    /**
     * JSNI Facade.
     */
    WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /**
     * The panel.
     */
    static FlowPanel panel = new FlowPanel();

    /**
     * JSNI Facade.
     */
    private WidgetJSNIFacadeImpl jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * Gadget tab.
     */
    private PagedListPanel gadgetTab = null;
    /**
     * Theme tab.
     */
    private PagedListPanel themeTab = null;

    /**
     * Gadget from index.
     */
    private int gadgetsFrom = 0;
    /**
     * Gadget to index.
     */
    private int gadgetsTo = 0;
    /**
     * Gadget total number.
     */
    private int gadgetsTotal = 0;

    /**
     * Add Gadget button.
     */
    private Hyperlink addGadget = new Hyperlink("Add App", Session.getInstance().generateUrl(
            new CreateUrlRequest("action", "newApp", false)));

    /**
     * Add theme button.
     */
    private Hyperlink addTheme = new Hyperlink("Add Theme", Session.getInstance().generateUrl(
            new CreateUrlRequest("action", "newTheme", false)));

    /**
     * Container for the gallery tabs.
     */
    private FlowPanel galleryPortalContainer = new FlowPanel();
    /**
     * Container for the add/edit panels.
     */
    private FlowPanel galleryAddOrEditContainer = new FlowPanel();

    /**
     * Default constructor.
     * 
     */
    public GalleryContent()
    {
        super(panel, "Configure");

        this.clearContentPanel();
        RootPanel.get().addStyleName(StaticResourceBundle.INSTANCE.coreCss().gallery());
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().galleryMaster());

        addGadget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addGadget());
        addGadget.setVisible(false);
        addTheme.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addTheme());
        addTheme.setVisible(false);

        panel.add(galleryPortalContainer);
        panel.add(galleryAddOrEditContainer);

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.ORG_COORDINATOR))
        {
            galleryPortalContainer.add(addGadget);
            galleryPortalContainer.add(addTheme);
        }

        gadgetTab = new PagedListPanel("gadgets", new SingleColumnPagedListRenderer());
        themeTab = new PagedListPanel("themes", new SingleColumnPagedListRenderer());

        portalPage = new TabContainerPanel("galleryTab");
        portalPage.addTab(new SimpleTab("Apps", gadgetTab));
        portalPage.addTab(new SimpleTab("Themes", themeTab));

        galleryPortalContainer.add(portalPage);
        portalPage.init();

        setUpEvents();

        StartTabsModel.getInstance().fetch(null, true);
        GadgetDefinitionCategoriesModel.getInstance().fetch(null, true);
        ThemeDefinitionCategoriesModel.getInstance().fetch(null, true);
    }

    /**
     * Set up all events.
     */
    private void setUpEvents()
    {
        Session.getInstance().getEventBus().addObserver(GotGadgetMetaDataEvent.class,
                new Observer<GotGadgetMetaDataEvent>()
                {
                    public void update(final GotGadgetMetaDataEvent event)
                    {
                        gadgetTab.render(new PagedSet<GadgetMetaDataDTO>(gadgetsFrom, gadgetsTo, gadgetsTotal, event
                                .getMetadata()), "There are no apps in this category.");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotGadgetDefinitionsResponseEvent.class,
                new Observer<GotGadgetDefinitionsResponseEvent>()
                {
                    public void update(final GotGadgetDefinitionsResponseEvent event)
                    {
                        gadgetsFrom = event.getResponse().getFromIndex();
                        gadgetsTo = event.getResponse().getToIndex();
                        gadgetsTotal = event.getResponse().getTotal();

                        if (gadgetsTotal == 0)
                        {
                            Session.getInstance().getEventBus().notifyObservers(
                                    new GotGadgetMetaDataEvent(new LinkedList<GadgetMetaDataDTO>()));
                        }
                        else
                        {
                            GadgetMetaDataFetcher fetcher = // \n
                            new GadgetMetaDataFetcher(event.getResponse().getPagedSet());
                            fetcher.fetchMetaData();
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotGadgetDefinitionCategoriesResponseEvent.class,
                new Observer<GotGadgetDefinitionCategoriesResponseEvent>()
                {
                    public void update(final GotGadgetDefinitionCategoriesResponseEvent event)
                    {
                        gadgetTab.addSet("All", GadgetDefinitionModel.getInstance(), new GadgetMetaDataRenderer(),
                                new GetGalleryItemsRequest("recent", "", 0, 0), "Recent");
                        gadgetTab.addSet("All", GadgetDefinitionModel.getInstance(), new GadgetMetaDataRenderer(),
                                new GetGalleryItemsRequest("popularity", "", 0, 0), "Popular");

                        for (String category : event.getResponse())
                        {
                            gadgetTab.addSet(category, GadgetDefinitionModel.getInstance(),
                                    new GadgetMetaDataRenderer(), new GetGalleryItemsRequest("recent", category, 0, 0),
                                    "Recent");
                            gadgetTab.addSet(category, GadgetDefinitionModel.getInstance(),
                                    new GadgetMetaDataRenderer(), new GetGalleryItemsRequest("popularity", category, 0,
                                            0), "Popular");
                        }

                        Session.getInstance().getEventBus().removeObserver(
                                GotGadgetDefinitionCategoriesResponseEvent.class, this);
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotGadgetDefinitionCategoriesResponseEvent.class,
                new Observer<GotGadgetDefinitionCategoriesResponseEvent>()
                {
                    public void update(final GotGadgetDefinitionCategoriesResponseEvent event)
                    {
                        if (Session.getInstance().getParameterValue("action").equals("newApp")
                                || Session.getInstance().getParameterValue("action").equals("editApp"))
                        {
                            renderCreateOrEditGadget(event.getResponse());
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotThemeDefinitionsResponseEvent.class,
                new Observer<GotThemeDefinitionsResponseEvent>()
                {
                    public void update(final GotThemeDefinitionsResponseEvent event)
                    {
                        themeTab.render(event.getResponse(), "There are no themes in this category.");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotThemeDefinitionCategoriesResponseEvent.class,
                new Observer<GotThemeDefinitionCategoriesResponseEvent>()
                {
                    public void update(final GotThemeDefinitionCategoriesResponseEvent event)
                    {
                        themeTab.addSet("All", ThemeModel.getInstance(), new ThemeRenderer(),
                                new GetGalleryItemsRequest("recent", "", 0, 0), "Recent");
                        themeTab.addSet("All", ThemeModel.getInstance(), new ThemeRenderer(),
                                new GetGalleryItemsRequest("popularity", "", 0, 0), "Popular");

                        for (String category : event.getResponse())
                        {
                            themeTab.addSet(category, ThemeModel.getInstance(), new ThemeRenderer(),
                                    new GetGalleryItemsRequest("recent", category, 0, 0), "Recent");
                            themeTab.addSet(category, ThemeModel.getInstance(), new ThemeRenderer(),
                                    new GetGalleryItemsRequest("popularity", category, 0, 0), "Popular");
                        }

                        Session.getInstance().getEventBus().removeObserver(
                                GotThemeDefinitionCategoriesResponseEvent.class, this);

                    }
                });

        Session.getInstance().getEventBus().addObserver(GotThemeDefinitionCategoriesResponseEvent.class,
                new Observer<GotThemeDefinitionCategoriesResponseEvent>()
                {
                    public void update(final GotThemeDefinitionCategoriesResponseEvent event)
                    {
                        if (Session.getInstance().getParameterValue("action").equals("newTheme")
                                || Session.getInstance().getParameterValue("action").equals("editTheme"))
                        {
                            renderCreateOrEditTheme(event.getResponse());
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(ThemeChangedEvent.getEvent(), new Observer<ThemeChangedEvent>()
        {
            public void update(final ThemeChangedEvent arg1)
            {
                String text = "Theme has been applied";

                // since a refresh happens in IE7 when navigating to the start page, show the notification
                // by passing in a notification url parameter
                if (MasterComposite.getUserAgent().contains("msie 7"))
                {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put(UINotifier.NOTIFICATION_PARAM, text);

                    Session.getInstance().getEventBus().notifyObservers(
                            new UpdateHistoryEvent(new CreateUrlRequest(Page.START, "", parameters)));
                }
                // otherwise, throw the notification event as normal
                else
                {
                    Session.getInstance().getEventBus().notifyObservers(
                            new UpdateHistoryEvent(new CreateUrlRequest(Page.START)));

                    Session.getInstance().getEventBus().notifyObservers(
                            new ShowNotificationEvent(new Notification(text)));
                }
            }
        });

        Session.getInstance().getEventBus().addObserver(UserLoggedInEvent.class, new Observer<UserLoggedInEvent>()
        {
            public void update(final UserLoggedInEvent event)
            {
                Session.getInstance().getEventBus().notifyObservers(GalleryPageLoadedEvent.getEvent());
            }
        });

        final HashMap<String, String> gadgetParams = new HashMap<String, String>();
        gadgetParams.put("tab", Session.getInstance().getParameterValue("tab"));
        gadgetParams.put("galleryTab", "Apps");

        final HashMap<String, String> themeParams = new HashMap<String, String>();
        themeParams.put("tab", Session.getInstance().getParameterValue("tab"));
        themeParams.put("galleryTab", "Themes");

        Session.getInstance().getEventBus().addObserver(InsertedGadgetDefinitionResponseEvent.class,
                new Observer<InsertedGadgetDefinitionResponseEvent>()
                {
                    public void update(final InsertedGadgetDefinitionResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.GALLERY, gadgetParams)));
                        gadgetTab.reload();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your app has been successfully added")));
                    }
                });

        Session.getInstance().getEventBus().addObserver(UpdatedGadgetDefinitionResponseEvent.class,
                new Observer<UpdatedGadgetDefinitionResponseEvent>()
                {
                    public void update(final UpdatedGadgetDefinitionResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.GALLERY, gadgetParams)));
                        gadgetTab.reload();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your app has been successfully saved")));
                    }
                });

        Session.getInstance().getEventBus().addObserver(InsertedThemeResponseEvent.class,
                new Observer<InsertedThemeResponseEvent>()
                {
                    public void update(final InsertedThemeResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.GALLERY, themeParams)));
                        themeTab.reload();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your theme has been successfully added")));
                    }

                });
        Session.getInstance().getEventBus().addObserver(UpdatedThemeResponseEvent.class,
                new Observer<UpdatedThemeResponseEvent>()
                {
                    public void update(final UpdatedThemeResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.GALLERY, themeParams)));
                        themeTab.reload();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Your theme has been successfully saved")));
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotStartPageTabsResponseEvent.class,
                new Observer<GotStartPageTabsResponseEvent>()
                {
                    public void update(final GotStartPageTabsResponseEvent event)
                    {
                        onGotStartTabs(event);
                    }
                });
    }

    /**
     * What happens after we get the start tabs (for the theme).
     * 
     * @param event
     *            the event.
     */
    private void onGotStartTabs(final GotStartPageTabsResponseEvent event)
    {
        // Apply the theme.
        if (event.getResponse().getTheme() != null)
        {
            jSNIFacade.setCSS(event.getResponse().getTheme().getCssFile());
        }
        else
        {
            // default theme
            jSNIFacade.setCSS("/themes/green_hills.css");
        }
        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {

                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().get("action").equals("newTheme")
                                || event.getParameters().get("action").equals("editTheme"))
                        {
                            if (Session.getInstance().getCurrentPersonRoles().contains(Role.ORG_COORDINATOR))
                            {
                                galleryPortalContainer.setVisible(false);
                                galleryAddOrEditContainer.setVisible(true);
                                galleryAddOrEditContainer.clear();

                                ThemeDefinitionCategoriesModel.getInstance().fetch(null, true);
                            }
                        }
                        else if (event.getParameters().get("action").equals("editApp")
                                || event.getParameters().get("action").equals("newApp"))
                        {
                            if (Session.getInstance().getCurrentPersonRoles().contains(Role.ORG_COORDINATOR))
                            {
                                galleryPortalContainer.setVisible(false);
                                galleryAddOrEditContainer.setVisible(true);
                                galleryAddOrEditContainer.clear();

                                GadgetDefinitionCategoriesModel.getInstance().fetch(null, true);
                            }
                        }
                        else
                        {
                            galleryAddOrEditContainer.setVisible(false);
                            galleryPortalContainer.setVisible(true);

                            setPreviousPage(new CreateUrlRequest(Page.START, "tab", Session.getInstance()
                                    .getParameterValue("tab")), "< Return to Start Page");
                            Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                            setPageTitle("Configure");

                            if (Session.getInstance().getCurrentPersonRoles().contains(Role.ORG_COORDINATOR))
                            {
                                addGadget.setVisible(Session.getInstance().getParameterValue("galleryTab").equals(
                                        "Apps"));
                                addTheme.setVisible(Session.getInstance().getParameterValue("galleryTab").equals(
                                        "Themes"));
                            }
                        }
                    }

                }, true);
    }

    /**
     * Render the create or edit screen for a theme.
     * 
     * @param categories
     *            the params from the history token.
     */
    private void renderCreateOrEditTheme(final LinkedList<String> categories)
    {
        String defaultCategory = null;
        String defaultUrl = "";
        String id = "";

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("tab", Session.getInstance().getParameterValue("tab"));
        urlParams.put("galleryTab", "Themes");

        this.setPreviousPage(new CreateUrlRequest(Page.GALLERY, urlParams), "< Return to Configure Page");

        String title = "Add Theme";
        FormBuilder.Method method = Method.INSERT;

        if (Session.getInstance().getParameterValue("action").equals("editTheme"))
        {
            title = "Edit Theme";
            method = Method.UPDATE;
            defaultUrl = Session.getInstance().getParameterValue("url");

            defaultCategory = Session.getInstance().getParameterValue("category");
            id = Session.getInstance().getParameterValue("id");
        }

        this.setPageTitle(title);
        FormBuilder form = new FormBuilder("", ThemeModel.getInstance(), method);

        if (method.equals(Method.UPDATE))
        {
            form.setSubmitButtonClass("form-update-button");
        }

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.GALLERY, urlParams)));
        form.addFormElement(new ValueOnlyFormElement("id", id));
        form.addWidget(new HTML("<em class='gallery-upload-note'><strong>Please Note:</strong><br />"
                + "Please be sure your XML file includes the required fields. You will not be able to upload the XML "
                + "without the required fields."));
        form.addFormDivider();

        form
                .addFormElement(new BasicDropDownFormElement("Category", "category", categories, defaultCategory, "",
                        true));

        form.addFormDivider();

        form.addFormElement(new BasicTextBoxFormElement("Theme XML:", "url", defaultUrl,
                "Enter the link to the xml file", true));

        form.addFormDivider();

        galleryAddOrEditContainer.add(form);
    }

    /**
     * Render the create or edit screen for a gadget.
     * 
     * @param categories
     *            the params from the history token.
     */
    private void renderCreateOrEditGadget(final LinkedList<String> categories)
    {

        String defaultCategory = null;
        String defaultUrl = "";
        String id = "";

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("tab", Session.getInstance().getParameterValue("tab"));
        urlParams.put("galleryTab", "Apps");

        this.setPreviousPage(new CreateUrlRequest(Page.GALLERY, urlParams), "< Return to Configure Page");

        String title = "Add App";
        FormBuilder.Method method = Method.INSERT;

        if (Session.getInstance().getParameterValue("action").equals("editApp"))
        {
            title = "Edit App";
            method = Method.UPDATE;
            defaultUrl = Session.getInstance().getParameterValue("url");

            defaultCategory = Session.getInstance().getParameterValue("category");
            id = Session.getInstance().getParameterValue("id");
        }

        this.setPageTitle(title);
        FormBuilder form = new FormBuilder("", GadgetDefinitionModel.getInstance(), method);

        if (method.equals(Method.UPDATE))
        {
            form.setSubmitButtonClass(StaticResourceBundle.INSTANCE.coreCss().formUpdateButton());
        }

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.GALLERY, urlParams)));
        form.addFormElement(new ValueOnlyFormElement("id", id));
        form.addWidget(new HTML("<em class='gallery-upload-note'><strong>Please Note:</strong><br />"
                + "Please be sure your XML file includes the required fields. You will not be able to upload the XML "
                + "without the required fields."));
        form.addFormDivider();

        form
                .addFormElement(new BasicDropDownFormElement("Category", "category", categories, defaultCategory, "",
                        true));

        form.addFormDivider();

        form.addFormElement(new BasicTextBoxFormElement("App XML:", "url",
                defaultUrl, "Enter the link to the xml file", true));

        form.addFormDivider();

        galleryAddOrEditContainer.add(form);
    }

}
