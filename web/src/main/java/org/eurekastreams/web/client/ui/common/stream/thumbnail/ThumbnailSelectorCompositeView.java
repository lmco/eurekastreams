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
package org.eurekastreams.web.client.ui.common.stream.thumbnail;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.ui.Bindable;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Thumbnail selector view.
 */
// TODO: Refactor into new widget-model design
public class ThumbnailSelectorCompositeView implements Bindable
{
    /**
     * The selected thumbnail.
     */
    Image selectedThumbnail;

    /**
     * The next button.
     */
    Label prevThumb;

    /**
     * The next button.
     */
    Label nextThumb;

    /**
     * Remove thumbnail check box.
     */
    CheckBox removeThumbnail;

    /**
     * Caption for the control.
     */
    Label caption;

    /**
     * Selected thumbnail container.
     */
    FlowPanel pagingContainer;

    /**
     * The model.
     */
    private ThumbnailSelectorCompositeModel model = null;

    /**
     * Constructor.
     *
     * @param inModel
     *            the model.
     */
    public ThumbnailSelectorCompositeView(final ThumbnailSelectorCompositeModel inModel)
    {
        model = inModel;
    }

    /**
     * Sets the thumbnail show/hide.
     */
    public void showHideThumbnail()
    {
        // caption.setVisible(!removeThumbnail.isChecked());
        // prevThumb.setVisible(!removeThumbnail.isChecked());
        // nextThumb.setVisible(!removeThumbnail.isChecked());
        selectedThumbnail.setVisible(!removeThumbnail.isChecked());

        if (removeThumbnail.isChecked())
        {
            pagingContainer.addStyleName("no-thumbnail");
            model.getLink().setSelectedThumbnail("");
        }
        else
        {
            pagingContainer.removeStyleName("no-thumbnail");
            updateImage();
        }
    }

    /**
     * Update the image.
     */
    public void updateImage()
    {
        if (model.hasPrevious())
        {
            prevThumb.removeStyleName("previous-arrow-disabled");
        }
        else
        {
            prevThumb.addStyleName("previous-arrow-disabled");
        }

        if (model.hasNext())
        {
            nextThumb.removeStyleName("next-arrow-disabled");
        }
        else
        {
            nextThumb.addStyleName("next-arrow-disabled");
        }

        selectedThumbnail.setVisible(model.getSelectedThumbnailUrl() != "");

        if (model.getSelectedThumbnailUrl() != "")
        {
            selectedThumbnail.setUrl(model.getSelectedThumbnailUrl());
            model.getLink().setSelectedThumbnail(model.getSelectedThumbnailUrl());
        }
    }

    /**
     * Set the thumbnail.
     *
     * @param inLink
     *            the link.
     */
    public void setLink(final LinkInformation inLink)
    {
        model.setLink(inLink);

        removeThumbnail.setChecked(false);
        showHideThumbnail();
        updateImage();
    }

    /**
     * Add a click listener to the previous button.
     *
     * @param listener
     *            the listener.
     */
    public void addPrevClickListener(final ClickListener listener)
    {
        prevThumb.addClickListener(listener);

    }

    /**
     * Add a click listener to the next button.
     *
     * @param listener
     *            the listener.
     */
    public void addNextClickListener(final ClickListener listener)
    {
        nextThumb.addClickListener(listener);

    }

    /**
     * Add a click listener to the remove check box.
     *
     * @param listener
     *            the listener.
     */
    public void addRemoveThumbClickListener(final ClickListener listener)
    {
        removeThumbnail.addClickListener(listener);

    }
}
