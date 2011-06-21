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
package org.eurekastreams.web.client.ui.pages.search;

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

        add(resultsPanelContainer);
        resultsPanelContainer.add(resultsPanel);

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
                                .getText(), "", 0, 4);
                        SearchResultsModel.getInstance().fetch(request, true);
                    }
                }
            }
        });

        resultsPanelContainer.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                resultsPanelContainer.setVisible(false);
                resultsPanel.clear();
            }
        });

        eventBus.addObserver(GotSearchResultsResponseEvent.class, new Observer<GotSearchResultsResponseEvent>()
        {
            public void update(GotSearchResultsResponseEvent event)
            {
                resultsPanel.clear();
                resultsPanelContainer.setVisible(event.getResponse().getPagedSet().size() > 0);

                for (ModelView result : event.getResponse().getPagedSet())
                {
                    FlowPanel itemPanel = new FlowPanel();
                    Anchor name = new Anchor();
                    Label desc = new Label();

                    if (result instanceof PersonModelView)
                    {
                        PersonModelView person = (PersonModelView) result;
                        itemPanel.add(new AvatarLinkPanel(EntityType.PERSON, person.getAccountId(), person
                                .getEntityId(), person.getAvatarId(), Size.Small));
                        name.setText(person.getDisplayName());
                        name.setHref("#"
                                + Session.getInstance().generateUrl(
                                        new CreateUrlRequest(Page.PEOPLE, person.getAccountId())));
                        desc.setText(person.getDescription());
                    }
                    else if (result instanceof DomainGroupModelView)
                    {
                        DomainGroupModelView group = (DomainGroupModelView) result;
                        itemPanel.add(new AvatarLinkPanel(EntityType.GROUP, group.getShortName(), group.getEntityId(),
                                group.getAvatarId(), Size.Small));
                        name.setText(group.getName());
                        name.setHref("#"
                                + Session.getInstance().generateUrl(
                                        new CreateUrlRequest(Page.GROUPS, group.getShortName())));
                        desc.setText(group.getDescription());
                    }

                    itemPanel.add(name);
                    itemPanel.add(desc);

                    resultsPanel.add(itemPanel);
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
