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

import java.util.LinkedList;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamViewCreatedEvent;
import org.eurekastreams.web.client.events.StreamViewUpdatedEvent;
import org.eurekastreams.web.client.events.data.GotCompleteStreamViewResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.StreamViewModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.StreamScopeFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content for creating or editing a stream view.
 * 
 */
public class StreamViewDialogContent implements DialogContent
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
     * Scopes.
     */
    private LinkedList<StreamScope> scopes = new LinkedList<StreamScope>();

    /**
     * The ID of the StreamView.
     */
    private Long viewId;

    /**
     * The view.
     */
    private StreamView view;
    
    /**
     * The form.
    */
    private FormBuilder form;

    /**
     * Default constructor.
     * 
     * @param inViewId
     *            the view id.
     */
    public StreamViewDialogContent(final Long inViewId)
    {
        viewId = inViewId;
        mode = Method.UPDATE;
        Label loading = new Label("");
        loading.setStyleName("loading");
        body.add(loading);
        body.addStyleName("list-edit-modal");

        Session.getInstance().getEventBus().addObserver(GotCompleteStreamViewResponseEvent.class,
                new Observer<GotCompleteStreamViewResponseEvent>()
                {
                    public void update(final GotCompleteStreamViewResponseEvent event)
                    {
                        view = event.getResponse();
                        name = event.getResponse().getName();
                        scopes = new LinkedList<StreamScope>(event.getResponse().getIncludedScopes());
                        setUpForm();
                    }

                });
        StreamViewModel.getInstance().fetch(inViewId, true);
    }

    /**
     * Constructor for create, i.e. doesnt take in a view.
     * 
     */
    public StreamViewDialogContent()
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

        form = new FormBuilder("Organize streams into custom lists", StreamViewModel.getInstance(),
                mode);
        form.turnOffChangeCheck();
        form.addStyleName("stream-view-dialog-body");

        Session.getInstance().getEventBus().addObserver(StreamViewCreatedEvent.class,
                new Observer<StreamViewCreatedEvent>()
                {
                    public void update(final StreamViewCreatedEvent arg1)
                    {
                        close();
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamViewUpdatedEvent.class,
                new Observer<StreamViewUpdatedEvent>()
                {
                    public void update(final StreamViewUpdatedEvent arg1)
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
        form.addFormElement(new ValueOnlyFormElement("id", viewId));

        form.addFormElement(new BasicTextBoxFormElement(MAX_NAME, false, "Name", "name", name, "", true));

        form.addFormElement(new StreamScopeFormElement("scopes", scopes, "Streams",
                "Enter the name of an employee or group stream.", true, true, "/resources/autocomplete/entities/",
                MAX_NAME));
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
                        StreamViewModel.getInstance().delete(view);
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
            return "Create a List";
        case UPDATE:
            return "Edit List";
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
