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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.web.client.model.GroupActivitySubscriptionModel;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content for selecting options when subscribing for email notifications on new activity in a group stream.
 */
public class GroupEmailSubscribeOptionsDialogContent extends BaseDialogContent
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** Global CSS. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Radio button for all option. */
    @UiField
    RadioButton allSelectionButton;

    /** Radio button for coordinators-only option. */
    @UiField
    RadioButton coordOnlySelectionButton;

    /** UI element acting as the save button. */
    @UiField
    Label saveButton;

    /** UI element acting as the cancel button. */
    @UiField
    Label cancelButton;

    /** Main content widget. */
    private final Widget main;

    /** Unique ID of entity owning the stream. */
    private final String streamUniqueId;

    /**
     * Default constructor.
     *
     * @param inStreamUniqueId
     *            Unique ID of entity owning the stream.
     */
    public GroupEmailSubscribeOptionsDialogContent(final String inStreamUniqueId)
    {
        streamUniqueId = inStreamUniqueId;

        // -- build UI --
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        main = binder.createAndBindUi(this);
    }

    /**
     * Requests the widget be closed.
     *
     * @param ev
     *            Event.
     */
    @UiHandler({ "saveButton" })
    void save(final ClickEvent ev)
    {
        GroupActivitySubscriptionModel.getInstance().update(streamUniqueId, true, coordOnlySelectionButton.getValue());
        close();
    }

    /**
     * Requests the widget be closed.
     *
     * @param ev
     *            Event.
     */
    @UiHandler({ "cancelButton" })
    void cancel(final ClickEvent ev)
    {
        close();
    }

    /**
     * Gets the body panel.
     *
     * @return the body.
     */
    public Widget getBody()
    {
        return main;
    }

    /**
     * Gets the CSS name.
     *
     * @return the class.
     */
    @Override
    public String getCssName()
    {
        return style.modal();
    }

    /**
     * Gets the title.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return "Subscribe via Email";
    }

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Extra style for entire modal. */
        @ClassName("modal")
        String modal();
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, GroupEmailSubscribeOptionsDialogContent>
    {
    }
}
