/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Edit panel.
 */
public class EditPanel extends FlowPanel
{
    /**
     * Edit mode.
     */
    public enum Mode
    {
        /**
         * Edit only.
         */
        EDIT, 
        /**
         * Delete only.
         */
        DELETE, 
        /**
         * Both.
         */
        EDIT_AND_DELETE
    }

    /**
     * Delete Panel.
     */
    private Label deletePanel = new Label("Delete");
    
    /**
     * Edit Panel.
     */
    private Label editPanel = new Label("Edit");

    /**
     * Constructor.
     * @param inParent parent (used for hover).
     * @param inMode edit mode.
     */
    public EditPanel(final Widget inParent, final Mode inMode)
    {
        Widget parent = inParent;

        parent.addStyleName(StaticResourceBundle.INSTANCE.coreCss().editableItem());

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().editControls());

        boolean isEdit = Mode.EDIT.equals(inMode) || Mode.EDIT_AND_DELETE.equals(inMode);
        boolean isDelete = Mode.DELETE.equals(inMode) || Mode.EDIT_AND_DELETE.equals(inMode);

        editPanel.setVisible(isEdit);
        
        if (isEdit)
        {
            editPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().edit());
            this.add(editPanel);
        }

        deletePanel.setVisible(isDelete);

        if (isDelete)
        {
            deletePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().delete());
            this.add(deletePanel);
        }

    }

    /**
     * Add a delete handler.
     * @param deleteHandler the handler.
     */
    public void addDeleteClickHandler(final ClickHandler deleteHandler)
    {
        deletePanel.addClickHandler(deleteHandler);
    }

    /**
     * Add an edit handler.
     * @param editHandler the handler.
     */
    public void addEditClickHandler(final ClickHandler editHandler)
    {
        editPanel.addClickHandler(editHandler);
    }

}
