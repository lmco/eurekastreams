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
package org.eurekastreams.web.client.ui.common.stream.filters.search;

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamSearchCreatedEvent;
import org.eurekastreams.web.client.events.StreamSearchUpdatedEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.StreamSearchModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog for search streams.
 * 
 */
public class StreamSearchDialogContent implements DialogContent
{
    /**
     * Mode of the dialog.
     * 
     */
    public enum Mode
    {
        /**
         * Create mode.
         */
        CREATE,
        /**
         * Edit mode.
         */
        EDIT,
        /**
         * Save mode.
         */
        SAVE
    }

    /**
     * The command to close the dialog.
     */
    private WidgetCommand closeCommand = null;

    /**
     * Maximum name length.
     */
    private static final int MAX_NAME = 50;

    /**
     * Maximum keyword length.
     */
    private static final int MAX_KEYWORD = 2000;

    /**
     * Text for dialog box title.
     */
    private static final String TITLE = "Saved searches allow you to quickly keep tabs on activity that contains all "
            + "keywords that you choose.";

    /**
     * Text for dialog box title when being viewed from a profile page.
     */
    private static final String PROFILE_TITLE = TITLE + " Your saved searches will be listed on your activity page.";

    /**
     * Mode.
     */
    private Mode mode;

    /**
     * Main flow panel.
     */
    private FlowPanel body = new FlowPanel();

    /** The main content of the dialog. */
    private FormBuilder form;

    /**
     * Scopes.
     */
    private LinkedList<StreamScope> scopes = new LinkedList<StreamScope>();

    /**
     * The ID of the StreamView.
     */
    private String name = "";
    /**
     * Keywords.
     */
    private String keywords = "";
    /**
     * View id.
     */
    private Long viewId = null;
    /**
     * ID of the search.
     */
    private Long id;
    /**
     * View name.
     */
    private String viewName = "Everybody";

    /**
     * The views.
     */
    private List<StreamFilter> views;

    /**
     * The list form element.
     */
    private StreamListFormElement streamLists;

    /**
     * If the events are setup.
     */
    private static boolean eventsSetup = false;

    /**
     * Event observer for {@link StreamSearchCreatedEvent}.
     */
    private Observer<StreamSearchCreatedEvent> createSearchObserver;

    /**
     * Event observer for {@link StreamSearchUpdatedEvent}.
     */
    private Observer<StreamSearchUpdatedEvent> updateSearchObserver;

    /**
     * The search.
     */
    private StreamSearch search;

    /**
     * Default constructor.
     * 
     * @param inSearch
     *            the search.
     * @param inViews
     *            the views.
     */
    public StreamSearchDialogContent(final StreamSearch inSearch, final List<StreamFilter> inViews)
    {
        search = inSearch;
        views = inViews;
        name = search.getName();
        viewId = search.getStreamView().getId();
        id = search.getId();
        viewName = search.getStreamView().getName();

        keywords = search.getKeywordsAsString();

        mode = Mode.EDIT;
        setUpForm();
    }

    /**
     * Constructor for create, i.e. doesnt take in a view.
     * 
     * @param inViews
     *            the views.
     */
    public StreamSearchDialogContent(final List<StreamFilter> inViews)
    {
        views = inViews;
        mode = Mode.CREATE;
        setUpForm();
    }

    /**
     * Constructor for saving a search from a search.
     * 
     * @param inName
     *            the name.
     * @param inKeywords
     *            the keywords.
     * @param view
     *            the view.
     */
    public StreamSearchDialogContent(final String inName, final String inKeywords, final StreamView view)
    {
        viewId = view.getId();
        viewName = view.getName();
        name = inName;
        keywords = inKeywords;
        mode = Mode.SAVE;
        setUpForm();
    }

    /**
     * viewId Sets up the form.
     */
    private void setUpForm()
    {
        FormBuilder.Method method = Method.INSERT;
        if (mode.equals(Mode.EDIT))
        {
            method = Method.UPDATE;
        }
        if (!mode.equals(Mode.SAVE))
        {
            streamLists = new StreamListFormElement(views, viewId, viewName);
            viewName = streamLists.getStreamViewName();
            form = new FormBuilder(TITLE, StreamSearchModel.getInstance(viewName), method);
        }
        else
        {
            form = new FormBuilder(PROFILE_TITLE, StreamSearchModel.getInstance(viewName), method);
        }
        form.turnOffChangeCheck();
        form.addStyleName("stream-search-dialog-body");

        if (mode.equals(Mode.EDIT))
        {
            form.addStyleName("stream-search-dialog-body-edit");
        }

        form.addOnCancelCommand(new Command()
        {
            public void execute()
            {
                close();
            }
        });

        createSearchObserver = new Observer<StreamSearchCreatedEvent>()
        {
            public void update(final StreamSearchCreatedEvent event)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new ShowNotificationEvent(new Notification("Your search has been successfully saved")));

                close();
            }

        };

        updateSearchObserver = new Observer<StreamSearchUpdatedEvent>()
        {
            public void update(final StreamSearchUpdatedEvent event)
            {
                close();
            }

        };

        Session.getInstance().getEventBus().addObserver(StreamSearchCreatedEvent.class, createSearchObserver);
        Session.getInstance().getEventBus().addObserver(StreamSearchUpdatedEvent.class, updateSearchObserver);

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest()));

        form.addFormElement(new ValueOnlyFormElement("id", id));

        form.addFormElement(new BasicTextBoxFormElement(MAX_NAME, false, "Name", "name", name, "", true));

        if (mode.equals(Mode.SAVE))
        {
            form.addFormElement(new ValueOnlyFormElement("streamViewId", viewId));

            Panel whichViewPanel = new FlowPanel();
            whichViewPanel.addStyleName("view-panel");
            Label label = new InlineLabel("Stream: ");
            label.addStyleName("form-label");
            whichViewPanel.add(label);
            label = new InlineLabel(viewName);
            label.addStyleName("view-text");
            whichViewPanel.add(label);
            form.addWidget(whichViewPanel);
        }
        else
        {
            form.addFormElement(streamLists);
        }

        form.addFormElement(new BasicTextBoxFormElement(MAX_KEYWORD, false, "Keywords", "keywords", keywords,
                "Separate multiple keywords with spaces", true));

        if (mode.equals(Mode.EDIT))
        {
            Anchor deleteButton = new Anchor("delete");
            deleteButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();
                    if (jSNIFacade.confirm("Apps based on this search will need to be manually deleted from the "
                            + "Start Page. Are you sure you want to delete this saved search?"))
                    {
                        StreamSearchModel.getInstance(null).delete(search);
                        close();
                    }
                }
            });
            deleteButton.addStyleName("delete-button");
            form.add(deleteButton);
        }
    }

    /**
     * The command to call to close the dialog.
     * 
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        Session.getInstance().getEventBus().removeObserver(StreamSearchCreatedEvent.class, createSearchObserver);
        Session.getInstance().getEventBus().removeObserver(StreamSearchUpdatedEvent.class, updateSearchObserver);
        closeCommand.execute();
    }

    /**
     * Gets the body panel.
     * 
     * @return the body.
     */
    public Widget getBody()
    {
        return form;
    }

    /**
     * Gets the CSS name.
     * 
     * @return the class.
     */
    public String getCssName()
    {
        return "stream-search-dialog";
    }

    /**
     * Gets the title.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        switch (mode)
        {
        case CREATE:
            return "Create a Saved Search";
        case EDIT:
            return "Edit a Saved Search";
        case SAVE:
            return "Save a Search";
        default:
            return "";
        }
    }

    /**
     * On show. Nothing to do here. Carry on.
     */
    public void show()
    {
    }
}
