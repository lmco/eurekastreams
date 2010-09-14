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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.web.client.events.CustomStreamCreatedEvent;
import org.eurekastreams.web.client.events.CustomStreamUpdatedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.CustomStreamModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content for creating or editing a stream view.
 *
 */
public class CustomStreamDialogContent implements DialogContent
{

    /**
     * The command to close the dialog.
     */
    private WidgetCommand closeCommand = null;

    /**
     * Maximum name length.
     */
    private static final int MAX_NAME = 50;

    /**
     * Mode.
     */
    private FormBuilder.Method mode;
    /**
     * Main flow panel.
     */
    private FlowPanel body = new FlowPanel();
    /**
     * Name.
     */
    private String name = "";

    /**
     * The ID of the StreamView.
     */
    private Long viewId;

    /**
     * The view.
     */
    private Stream view;

    /**
     * Form Builder.
     */
    private FormBuilder form;

    /**
     * Maximum keyword length.
     */
    private static final int MAX_KEYWORD = 2000;

    /**
     * Keywords.
     */
    private String keywords = "";

    /**
     * View name.
     */
    private String viewName = "Everybody";

    /**
     * The list form element.
     */
    private StreamListFormElement streamLists;

    /**
     * Default constructor.
     *
     * @param inStream
     *            the view id.
     */
    public CustomStreamDialogContent(final Stream inStream)
    {
        viewId = inStream.getId();
        mode = Method.UPDATE;
        body.addStyleName("list-edit-modal");
        view = inStream;
        name = inStream.getName();
        setUpForm();

    }

    /**
     * Constructor for create, i.e. doesnt take in a view.
     *
     */
    public CustomStreamDialogContent()
    {
        mode = Method.INSERT;
        setUpForm();
    }

    /**
     * Sets up the form.
     */
    private void setUpForm()
    {
        body.clear();

        streamLists = new StreamListFormElement(null);

        form = new FormBuilder("Organize streams into custom lists", CustomStreamModel.getInstance(), mode);
        form.turnOffChangeCheck();
        form.addStyleName("stream-view-dialog-body");

        Session.getInstance().getEventBus().addObserver(CustomStreamCreatedEvent.class,
                new Observer<CustomStreamCreatedEvent>()
                {
                    public void update(final CustomStreamCreatedEvent arg1)
                    {
                        close();
                    }
                });

        Session.getInstance().getEventBus().addObserver(CustomStreamUpdatedEvent.class,
                new Observer<CustomStreamUpdatedEvent>()
                {
                    public void update(final CustomStreamUpdatedEvent arg1)
                    {
                        close();
                    }
                });

        form.addOnCancelCommand(new Command()
        {
            public void execute()
            {
                close();
            }
        });

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest()));



        if (view != null)
        {
            JSONObject json = JSONParser.parse(view.getRequest()).isObject();
            JSONObject query = json.get("query").isObject();

            streamLists = new StreamListFormElement(query);
            keywords = query.get("keywords").isString().stringValue();
        }

        form.addFormElement(new BasicTextBoxFormElement(MAX_NAME, false, "Name", "name", name, "", true));

        form.addFormElement(streamLists);

        form.addFormElement(new BasicTextBoxFormElement(MAX_KEYWORD, false, "Keywords", "keywords", keywords,
                "Optional: Separate multiple keywords with spaces", false));



        body.add(form);

        if (mode.equals(Method.UPDATE))
        {
            Anchor deleteButton = new Anchor("delete");
            deleteButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();
                    if (jSNIFacade.confirm("Apps based on this list will need to be manually deleted from the "
                            + "Start Page. Saved Searches based on this list will be automatically deleted. Are "
                            + "you sure you want to delete this list?"))
                    {
                        CustomStreamModel.getInstance().delete(view);
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
        closeCommand.execute();
    }

    /**
     * Gets the body panel.
     *
     * @return the body.
     */
    public Widget getBody()
    {
        return body;
    }

    /**
     * Gets the CSS name.
     *
     * @return the class.
     */
    public String getCssName()
    {
        return "stream-view-dialog";
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
        case INSERT:
            return "Create a Stream";
        case UPDATE:
            return "Edit Stream";
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
