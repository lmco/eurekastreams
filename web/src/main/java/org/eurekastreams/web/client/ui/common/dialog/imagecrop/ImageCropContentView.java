/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog.imagecrop;

import java.util.List;

import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * The image crop dialog view.
 * 
 */
public class ImageCropContentView implements Bindable
{
    /**
     * The save button.
     */
    Hyperlink saveButton;
    /**
     * The cancel button.
     */
    Hyperlink closeButton;
    /**
     * The person.
     */
    private ImageUploadStrategy strategy;
    /**
     * The widget.
     */
    private ImageCropContent widget;
    /**
     * The command to exe on save.
     */
    private Command saveCommand;
    /**
     * The controller.
     */
    private ImageCropContentController controller;

    /**
     * Default constructor.
     * 
     * @param inController
     *            the controller.
     * @param inWidget
     *            the widget.
     * @param inSaveCommand
     *            the save command.
     * @param inStrategy
     *            the entity.
     */
    public ImageCropContentView(final ImageCropContentController inController,
            final ImageCropContent inWidget, final Command inSaveCommand,
            final ImageUploadStrategy inStrategy)
    {
        strategy = inStrategy;
        widget = inWidget;
        saveCommand = inSaveCommand;
        controller = inController;
    }

    /**
     * Wires up the widgets.
     */
    public void init()
    {
        controller.addSaveClickListener(saveButton, this);
        controller.addCloseClickListener(closeButton);
    }

    /**
     * Gets the coords from the widget. Needs to do this because of JSNI.
     * 
     * @return the list of coords.
     */
    public List<Integer> getCoords()
    {
        return widget.getCoords();
    }

    /**
     * Responds to when the model changes the person.
     * 
     * @param inStrategy
     *            the person.
     */
    public void onStrategyUpdated(final ImageUploadStrategy inStrategy)
    {
        strategy = inStrategy;
        saveCommand.execute();
    }

    /**
     * Responds to when the model changes its shown value.
     * 
     * @param value
     *            the value.
     */
    public void onIsShownChanged(final boolean value)
    {
        if (!value)
        {
            widget.close();
        }
    }

    /**
     * Gets the person.
     * 
     * @return the person.
     */
    public ImageUploadStrategy getStrategy()
    {
        return strategy;
    }
}
