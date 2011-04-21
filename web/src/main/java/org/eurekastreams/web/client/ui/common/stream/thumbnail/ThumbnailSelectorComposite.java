/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import java.util.LinkedList;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Thumbnail selector.
 */
public class ThumbnailSelectorComposite extends FlowPanel
{
    /**
     * The selected thumbnail.
     */
    private final Image selectedThumbnail = new Image();

    /**
     * The next button.
     */
    private final Label prevThumb = new Label("<");

    /**
     * The next button.
     */
    private final Label nextThumb = new Label(">");

    /**
     * Remove thumbnail check box.
     */
    private final CheckBox removeThumbnail = new CheckBox("Don't display image");

    /**
     * Caption for the control.
     */
    private final Label caption = new Label("Select Image: ");

    /**
     * Selected thumbnail container.
     */
    private final FlowPanel selectedThumbContainer = new FlowPanel();

    /**
     * Selected thumbnail container.
     */
    private final FlowPanel pagingContainer = new FlowPanel();

    /** The link. */
    private LinkInformation link;

    /** Index of selected thumbnail in thumbnail URL list. */
    private int selectedIndex;

    /** List of thumbnails. */
    private final LinkedList<String> thumbnailUrls = new LinkedList<String>();

    /**
     * Constructor.
     */
    public ThumbnailSelectorComposite()
    {
        selectedThumbnail.addStyleName(StaticResourceBundle.INSTANCE.coreCss().thumbnail());
        this.add(selectedThumbContainer);
        selectedThumbContainer.add(selectedThumbnail);

        pagingContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().thumbnailSelectorControls());
        pagingContainer.add(caption);
        pagingContainer.add(prevThumb);
        prevThumb.addStyleName(StaticResourceBundle.INSTANCE.coreCss().previousArrow());
        pagingContainer.add(nextThumb);
        nextThumb.addStyleName(StaticResourceBundle.INSTANCE.coreCss().nextArrow());
        pagingContainer.add(new Label(" | "));
        pagingContainer.add(removeThumbnail);

        setupEvents();
    }

    /**
     * Wire up events.
     */
    public void setupEvents()
    {
        prevThumb.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                if (selectedIndex > 0)
                {
                    selectedIndex--;
                    updateImage();
                }
            }
        });

        nextThumb.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                if (selectedIndex < (thumbnailUrls.size() - 1))
                {
                    selectedIndex++;
                    updateImage();
                }
            }
        });

        removeThumbnail.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent ev)
            {
                showHideThumbnail();
            }
        });
    }

    /**
     * Sets the link information for which the control will display thumbnails.
     * 
     * @param inLink
     *            Link information.
     */
    public void setLink(final LinkInformation inLink)
    {
        link = inLink;
        selectedIndex = 0;
        thumbnailUrls.clear();

        if (!link.getImageUrls().isEmpty())
        {
            for (String imgUrl : link.getImageUrls())
            {
                if (link.getLargestImageUrl().equals(imgUrl))
                {
                    thumbnailUrls.addFirst(imgUrl);
                }
                else
                {
                    thumbnailUrls.add(imgUrl);
                }
            }

            link.setSelectedThumbnail(thumbnailUrls.get(0));
        }

        removeThumbnail.setValue(false);
        showHideThumbnail();
        updateImage();
    }

    /**
     * Gets the paging controls.
     * 
     * @return the paging controls.
     */
    public FlowPanel getPagingControlls()
    {
        return pagingContainer;
    }

    /**
     * Update the image.
     */
    private void updateImage()
    {
        if (selectedIndex > 0)
        {
            prevThumb.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().previousArrowDisabled());
        }
        else
        {
            prevThumb.addStyleName(StaticResourceBundle.INSTANCE.coreCss().previousArrowDisabled());
        }

        if (selectedIndex < (thumbnailUrls.size() - 1))
        {
            nextThumb.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().nextArrowDisabled());
        }
        else
        {
            nextThumb.addStyleName(StaticResourceBundle.INSTANCE.coreCss().nextArrowDisabled());
        }

        if (selectedIndex < thumbnailUrls.size())
        {
            String url = thumbnailUrls.get(selectedIndex);
            selectedThumbnail.setUrl(url);
            link.setSelectedThumbnail(url);
            selectedThumbnail.setVisible(true);
        }
        else
        {
            selectedThumbnail.setVisible(false);
        }
    }

    /**
     * Sets the thumbnail show/hide.
     */
    private void showHideThumbnail()
    {
        boolean checked = removeThumbnail.getValue();
        selectedThumbnail.setVisible(!checked);

        if (checked)
        {
            pagingContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().noThumbnail());
            link.setSelectedThumbnail("");
        }
        else
        {
            pagingContainer.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().noThumbnail());
            updateImage();
        }
    }
}
