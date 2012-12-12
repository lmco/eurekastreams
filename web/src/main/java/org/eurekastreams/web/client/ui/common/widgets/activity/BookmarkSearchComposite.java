/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.widgets.activity;

import java.util.HashMap;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.GotSearchResultsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.SearchResultsModel;
import org.eurekastreams.web.client.model.StreamBookmarksModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Global search composite. TODO break this out for testability.
 */
public class BookmarkSearchComposite extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Global styles. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Title shown when collapsed. */
    @UiField
    Label collapsedTitle;

    /** Title shown when expanded. */
    @UiField
    Label expandedTitle;

    /** Panel shown when expanded. */
    @UiField
    DivElement expandedPanel;

    /** The search term box. */
    @UiField
    LabeledTextBox searchTerm;

    /** Results panel. */
    @UiField
    FlowPanel resultsPanel;

    /** Results panel container. */
    @UiField
    FocusPanel resultsPanelContainer;

    /** Last length of search term. */
    private int termLength = -1;

    /** Currently active item for keyboard selection. */
    private Panel activeItem = null;

    /**
     * Constructor.
     */
    public BookmarkSearchComposite()
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        Widget main = binder.createAndBindUi(this);
        // resultsPanelContainer.setVisible(false);
        initWidget(main);

        // addStyleName(StaticResourceBundle.INSTANCE.coreCss().bookmarkSearch());

        setupEvents();
    }

    /**
     * Sets up event handling.
     */
    private void setupEvents()
    {
        final EventBus eventBus = Session.getInstance().getEventBus();

        searchTerm.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                // ENTER key
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown())
                {
                    // navigating the list of search results - pick it
                    if (activeItem != null)
                    {
                        activeItem.getElement().dispatchEvent(
                                Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
                    }
                    // search term in box - go to search page (should this be here?)
                    else if (!searchTerm.getText().isEmpty())
                    {
                        eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.SEARCH,
                                generateParams(searchTerm.getText()), false)));
                    }
                }
                else if (ev.isDownArrow() && activeItem != null)
                {
                    int activeIndex = resultsPanel.getWidgetIndex(activeItem);
                    if (activeIndex + 1 < resultsPanel.getWidgetCount())
                    {
                        selectItem((Panel) resultsPanel.getWidget(activeIndex + 1));
                    }
                }
                else if (ev.isUpArrow() && activeItem != null)
                {
                    int activeIndex = resultsPanel.getWidgetIndex(activeItem);
                    if (activeIndex >= 1)
                    {
                        selectItem((Panel) resultsPanel.getWidget(activeIndex - 1));
                    }
                }
                else if (termLength != searchTerm.getText().length())
                {
                    termLength = searchTerm.getText().length();
                    if (termLength == 0)
                    {
                        resultsPanelContainer.setVisible(false);
                        resultsPanel.clear();
                    }
                    else
                    {
                        GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest(searchTerm
                                .getText(), "", 0, 4, "bookmark");
                        SearchResultsModel.getInstance().fetch(request, true);
                    }
                }
            }
        });

        eventBus.addObserver(GotSearchResultsResponseEvent.class, new Observer<GotSearchResultsResponseEvent>()
        {
            public void update(final GotSearchResultsResponseEvent event)
            {
                if ("bookmark".equals(event.getCallerKey()))
                {
                    activeItem = null;
                    resultsPanel.clear();
                    resultsPanelContainer.setVisible(event.getResponse().getPagedSet().size() > 0);

                    for (ModelView result : event.getResponse().getPagedSet())
                    {
                        final FocusPanel itemContainer = new FocusPanel();
                        final FlowPanel itemPanel = new FlowPanel();
                        final Anchor name = new Anchor();
                        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().bookmarkSearchName());
                        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsis());

                        if (result instanceof PersonModelView)
                        {
                            final PersonModelView person = (PersonModelView) result;
                            itemPanel.add(AvatarLinkPanel.create(person, Size.VerySmall, false));
                            name.setText(person.getDisplayName());
                            name.setTitle(person.getDisplayName());
                            itemContainer.addClickHandler(new ClickHandler()
                            {
                                public void onClick(final ClickEvent event)
                                {
                                    StreamBookmarksModel.getInstance().insert(person.getStreamId());
                                }
                            });

                        }
                        else if (result instanceof DomainGroupModelView)
                        {
                            final DomainGroupModelView group = (DomainGroupModelView) result;
                            itemPanel.add(new AvatarLinkPanel(EntityType.GROUP, group.getShortName(), group
                                    .getAvatarId(), Size.VerySmall, false));
                            name.setText(group.getName());
                            name.setTitle(group.getName());
                            itemContainer.addClickHandler(new ClickHandler()
                            {
                                public void onClick(final ClickEvent event)
                                {
                                    StreamBookmarksModel.getInstance().insert(group.getStreamId());
                                }
                            });
                        }

                        itemContainer.addMouseOverHandler(new MouseOverHandler()
                        {
                            public void onMouseOver(final MouseOverEvent arg0)
                            {
                                selectItem(itemContainer);
                            }
                        });

                        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().bookmarkNameLink());
                        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsis());
                        itemPanel.add(name);

                        itemContainer.add(itemPanel);
                        resultsPanel.add(itemContainer);

                        if (activeItem == null)
                        {
                            selectItem(itemContainer);
                        }
                    }
                }
            }
        });

        eventBus.addObserver(SwitchedHistoryViewEvent.class, new Observer<SwitchedHistoryViewEvent>()
        {
            public void update(final SwitchedHistoryViewEvent event)
            {
                if (event.getPage() != Page.SEARCH)
                {
                    searchTerm.reset();
                }
            }
        });
    }

    /**
     * Resets search when results clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("resultsPanelContainer")
    void onResultsPanelContainerClick(final ClickEvent ev)
    {
        clearSearchResults();
    }

    /**
     * Clears UI associated with the search results.
     */
    private void clearSearchResults()
    {
        searchTerm.reset();
        resultsPanelContainer.setVisible(false);
        resultsPanel.clear();
        removeStyleName(style.searchActive());
        activeItem = null;
    }

    /**
     * Expands panel when clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("collapsedTitle")
    void onExpandClick(final ClickEvent ev)
    {
        collapsedTitle.setVisible(false);
        UIObject.setVisible(expandedPanel, true);
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            public void execute()
            {
                searchTerm.setFocus(true);
            }
        });
    }

    /**
     * Collapses panel when clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("expandedTitle")
    void onCollapseClick(final ClickEvent ev)
    {
        collapsedTitle.setVisible(true);
        UIObject.setVisible(expandedPanel, false);
        clearSearchResults();
    }

    /**
     * Select an item.
     *
     * @param item
     *            the item.
     */
    private void selectItem(final Panel item)
    {
        if (activeItem != null)
        {
            activeItem.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        }
        item.addStyleName(StaticResourceBundle.INSTANCE.coreCss().active());
        activeItem = item;
    }

    /**
     * Creates a hashmap for the history parameters to pass to the search page.
     *
     * @param query
     *            the search string.
     * @return the hashmap of all necessary initial search parameters.
     */
    private HashMap<String, String> generateParams(final String query)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("query", query);
        params.put("startIndex", "0");
        params.put("endIndex", "9");
        return params;
    }

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Active style for overall widget. */
        @ClassName("search-active")
        String searchActive();
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, BookmarkSearchComposite>
    {
    }
}
