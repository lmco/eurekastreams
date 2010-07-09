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
import org.eurekastreams.web.client.ui.PropertyMapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Thumbnail selector.
 */
public class ThumbnailSelectorComposite extends FlowPanel implements Bindable
{
    /**
     * The selected thumbnail.
     */
    Image selectedThumbnail;

    /**
     * The next button.
     */
    Label prevThumb = new Label("<");

    /**
     * The next button.
     */
    Label nextThumb = new Label(">");

    /**
     * Remove thumbnail check box.
     */
    CheckBox removeThumbnail = new CheckBox("Don't display image");

    /**
     * Caption for the control.
     */
    Label caption = new Label("Select Image: ");

    /**
     * Selected thumbnail container.
     */
    private FlowPanel selectedThumbContainer = new FlowPanel();

    /**
     * Selected thumbnail container.
     */
    FlowPanel pagingContainer = new FlowPanel();

    /**
     * The view.
     */
    private ThumbnailSelectorCompositeView view = null;

    /**
     * Constructor.
     */
    public ThumbnailSelectorComposite()
    {
        selectedThumbnail = new Image();
        selectedThumbnail.addStyleName("thumbnail");
        this.add(selectedThumbContainer);
        selectedThumbContainer.add(selectedThumbnail);

        pagingContainer.addStyleName("thumbnail-selector-controls");
        pagingContainer.add(caption);
        pagingContainer.add(prevThumb);
        prevThumb.addStyleName("previous-arrow");
        pagingContainer.add(nextThumb);
        nextThumb.addStyleName("next-arrow");
        pagingContainer.add(new Label(" | "));
        pagingContainer.add(removeThumbnail);

        ThumbnailSelectorCompositeModel model = new ThumbnailSelectorCompositeModel();
        view = new ThumbnailSelectorCompositeView(model);
        ThumbnailSelectorCompositeController controller = new ThumbnailSelectorCompositeController(view, model);

        PropertyMapper mapper = new PropertyMapper(GWT.create(ThumbnailSelectorComposite.class), GWT
                .create(ThumbnailSelectorCompositeView.class));

        mapper.bind(this, view);

        controller.init();
    }

    /**
     * Set the thumbnail.
     *
     * @param inLink
     *            the link.
     */
    public void setLink(final LinkInformation inLink)
    {
        view.setLink(inLink);
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
}
