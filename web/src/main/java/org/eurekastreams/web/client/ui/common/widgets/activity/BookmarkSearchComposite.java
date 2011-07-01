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
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Global search composite. TODO break this out for testability.
 */
public class BookmarkSearchComposite extends FlowPanel
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
     * Constructor.
     */
    public BookmarkSearchComposite()
    {
        searchTerm = new LabeledTextBox("add a bookmark");
        resultsPanelContainer.setVisible(false);

        resultsPanelContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchResultsAutocompleteResults());

        Label bookmarkTitle = new Label("Bookmark a Stream");

        add(bookmarkTitle);

        addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchList());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().bookmarkSearch());
        add(searchTerm);

        add(resultsPanelContainer);
        resultsPanelContainer.add(resultsPanel);

        bookmarkTitle.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                if (getStyleName().contains(StaticResourceBundle.INSTANCE.coreCss().bookmarkSearchActive()))
                {
                    removeStyleName(StaticResourceBundle.INSTANCE.coreCss().bookmarkSearchActive());
                }
                else
                {
                    addStyleName(StaticResourceBundle.INSTANCE.coreCss().bookmarkSearchActive());
                }
            }
        });

        final EventBus eventBus = Session.getInstance().getEventBus();

        searchTerm.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown()
                        && searchTerm.getText().length() > 0)
                {
                    eventBus.notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.SEARCH,
                            generateParams(searchTerm.getText()), false)));
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

        resultsPanelContainer.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                searchTerm.reset();
                resultsPanelContainer.setVisible(false);
                resultsPanel.clear();
            }
        });

        eventBus.addObserver(GotSearchResultsResponseEvent.class, new Observer<GotSearchResultsResponseEvent>()
        {
            public void update(final GotSearchResultsResponseEvent event)
            {
                if ("bookmark".equals(event.getCallerKey()))
                {
                    resultsPanel.clear();
                    resultsPanelContainer.setVisible(event.getResponse().getPagedSet().size() > 0);

                    for (ModelView result : event.getResponse().getPagedSet())
                    {
                        final FocusPanel itemContainer = new FocusPanel();
                        final FlowPanel itemPanel = new FlowPanel();
                        final Anchor name = new Anchor();

                        if (result instanceof PersonModelView)
                        {
                            final PersonModelView person = (PersonModelView) result;
                            itemPanel.add(new AvatarLinkPanel(EntityType.PERSON, person.getAccountId(), person
                                    .getEntityId(), person.getAvatarId(), Size.VerySmall));
                            name.setText(person.getDisplayName());
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
                                    .getEntityId(), group.getAvatarId(), Size.VerySmall));
                            name.setText(group.getName());
                            itemContainer.addClickHandler(new ClickHandler()
                            {
                                public void onClick(final ClickEvent event)
                                {
                                    StreamBookmarksModel.getInstance().insert(group.getStreamId());
                                }
                            });
                        }

                        itemPanel.add(name);

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
