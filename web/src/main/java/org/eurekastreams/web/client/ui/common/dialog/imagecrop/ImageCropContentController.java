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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * The image crop controller.
 * 
 */
public class ImageCropContentController
{
    /**
     * The model.
     */
    private ImageCropContentModel model;

    /**
     * Default constructor.
     * 
     * @param inModel
     *            the model.
     */
    public ImageCropContentController(final ImageCropContentModel inModel)
    {
        model = inModel;
    }

    /**
     * Adds the save click listener.
     * 
     * @param saveButton
     *            the save button to add it to.
     * @param view
     *            the view so we can fetch the coords.
     */
    public void addSaveClickListener(final Hyperlink saveButton,
            final ImageCropContentView view)
    {
        saveButton.addClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                model.setCoords(view.getCoords());
            }
        });
    }

    /**
     * Adds the close button listen to the close button.
     * @param closeButton the close button.
     */
    public void addCloseClickListener(final Hyperlink closeButton)
    {
        closeButton.addClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                model.setIsShown(false);
            }
        });
    }
}
