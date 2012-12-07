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
package org.eurekastreams.web.client.ui.pages.search;

import java.util.ArrayList;
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
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Global search composite. TODO break this out for testability.
 */
public class GlobalSearchComposite extends FlowPanel
{
    /**
     * The search term.
     */
    private final LabeledTextBox searchTerm;

    /**
     * Results panel.
     */
    private final FlowPanel resultsPanel = new FlowPanel();

    /**
     * Results panel container.
     */
    private final FocusPanel resultsPanelContainer = new FocusPanel();

    /**
     * Last length of search term.
     */
    private int termLength = -1;

    /**
     * Currently active item.
     */
    private Panel activeItem = null;

    /**
     * Timer factory.
     */
    private final TimerFactory timerFactory = new TimerFactory();

    /**
     * Hide delay after blur on post box.
     */
    private static final Integer BLUR_DELAY = 250;

    /**
     * Constructor.
     *
     * @param label
     *            the label for the uninitialized textbox.
     */
    public GlobalSearchComposite(final String label)
    {
        searchTerm = new LabeledTextBox(label);
        resultsPanelContainer.setVisible(false);
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchList());

        add(searchTerm);

        resultsPanelContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchResultsAutocompleteResults());
        resultsPanelContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss()
                .globalSearchResultsAutocompleteResults());

        add(resultsPanelContainer);
        resultsPanelContainer.add(resultsPanel);

        final EventBus eventBus = Session.getInstance().getEventBus();

        final GlobalSearchComposite thisClass = this;

        searchTerm.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown()
                        && searchTerm.getText().length() > 0 && activeItem != null)
                {
                    activeItem.getElement().dispatchEvent(
                            Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
                    clearSearch();

                }
                else if (ev.getNativeKeyCode() == KeyCodes.KEY_DOWN && activeItem != null)
                {
                    int activeIndex = resultsPanel.getWidgetIndex(activeItem);

                    if (activeIndex + 1 < resultsPanel.getWidgetCount())
                    {
                        selectItem((Panel) resultsPanel.getWidget(activeIndex + 1));
                    }
                }
                else if (ev.getNativeKeyCode() == KeyCodes.KEY_UP && activeItem != null)
                {
                    int activeIndex = resultsPanel.getWidgetIndex(activeItem);

                    if (activeIndex - 1 >= 0)
                    {
                        selectItem((Panel) resultsPanel.getWidget(activeIndex - 1));
                    }
                }
                else if (termLength != searchTerm.getText().length())
                {
                    String searchTermText = searchTerm.getText().trim();
                    termLength = searchTermText.length();
                    if (termLength == 0)
                    {
                        resultsPanelContainer.setVisible(false);
                        resultsPanel.clear();
                        thisClass.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().globalSearchBoxActive());
                    }
                    else
                    {
                        GetDirectorySearchResultsRequest request = new GetDirectorySearchResultsRequest(
                                searchTermText, "", 0, 4, "global");
                        SearchResultsModel.getInstance().fetch(request, true);
                        thisClass.addStyleName(StaticResourceBundle.INSTANCE.coreCss().globalSearchBoxActive());
                    }
                }
            }
        });

        searchTerm.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent arg0)
            {
                timerFactory.runTimer(BLUR_DELAY, new TimerHandler()
                {
                    public void run()
                    {
                        clearSearch();
                    }
                });
            }
        });

        eventBus.addObserver(GotSearchResultsResponseEvent.class, new Observer<GotSearchResultsResponseEvent>()
        {
            public void update(final GotSearchResultsResponseEvent event)
            {
                if ("global".equals(event.getCallerKey()))
                {
                    resultsPanel.clear();
                    activeItem = null;
                    resultsPanelContainer.setVisible(event.getResponse().getPagedSet().size() > 0);
                    String historyToken = "";

                    for (ModelView result : event.getResponse().getPagedSet())
                    {
                        final FocusPanel itemContainer = new FocusPanel();
                        final FlowPanel itemPanel = new FlowPanel();
                        final Hyperlink name = new Hyperlink();
                        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().globalSearchItemName());
                        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsisChild());

                        if (result instanceof PersonModelView)
                        {
                            final PersonModelView person = (PersonModelView) result;
                            itemPanel.add(AvatarLinkPanel.create(person, Size.Small, false));
                            name.setText(person.getDisplayName());
                            name.setTitle(person.getDisplayName());
                            historyToken = Session.getInstance().generateUrl(
                                    new CreateUrlRequest(Page.PEOPLE, person.getAccountId()));

                            itemContainer.addClickHandler(new ClickHandler()
                            {
                                public void onClick(final ClickEvent event)
                                {
                                    ArrayList<String> views = new ArrayList<String>();
                                    views.add(person.getAccountId());
                                    eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.PEOPLE,
                                            views)));
                                }
                            });

                        }
                        else if (result instanceof DomainGroupModelView)
                        {
                            final DomainGroupModelView group = (DomainGroupModelView) result;
                            itemPanel.add(new AvatarLinkPanel(EntityType.GROUP, group.getShortName(), group
                                    .getAvatarId(), Size.Small, false));
                            name.setText(group.getName());
                            name.setTitle(group.getName());
                            historyToken = Session.getInstance().generateUrl(
                                    new CreateUrlRequest(Page.GROUPS, group.getShortName()));

                            itemContainer.addClickHandler(new ClickHandler()
                            {
                                public void onClick(final ClickEvent event)
                                {
                                    ArrayList<String> views = new ArrayList<String>();
                                    views.add(group.getShortName());
                                    eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.GROUPS,
                                            views)));
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

                        name.setTargetHistoryToken(historyToken);
                        itemPanel.add(name);

                        itemContainer.add(itemPanel);
                        resultsPanel.add(itemContainer);

                        if (activeItem == null)
                        {
                            selectItem(itemContainer);
                        }
                    }

                    if (event.getResponse().getTotal() > event.getResponse().getPagedSet().size())
                    {
                        final FocusPanel itemContainer = new FocusPanel();
                        final FlowPanel itemPanel = new FlowPanel();

                        itemContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchResultsMoreButton());

                        itemPanel.add(new Label("See more results"));

                        itemContainer.addClickHandler(new ClickHandler()
                        {
                            public void onClick(final ClickEvent event)
                            {
                                eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.SEARCH,
                                        generateParams(searchTerm.getText()), false)));
                            }
                        });

                        itemContainer.addMouseOverHandler(new MouseOverHandler()
                        {
                            public void onMouseOver(final MouseOverEvent arg0)
                            {
                                selectItem(itemContainer);
                            }
                        });

                        itemContainer.add(itemPanel);
                        resultsPanel.add(itemContainer);
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
     * Clear the search.
     */
    protected void clearSearch()
    {
        termLength = -1;
        searchTerm.reset();
        resultsPanelContainer.setVisible(false);
        resultsPanel.clear();
        removeStyleName(StaticResourceBundle.INSTANCE.coreCss().globalSearchBoxActive());
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
}
