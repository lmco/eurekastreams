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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Thumbnail selector controller.
 */
// TODO: Refactor into new widget-model design
public class ThumbnailSelectorCompositeController
{
    /**
     * The view.
     */
    private ThumbnailSelectorCompositeView view = null;

    /**
     * The model.
     */
    private ThumbnailSelectorCompositeModel model = null;

    /**
     * Constructor.
     * @param inView view.
     * @param inModel model.
     */
    public ThumbnailSelectorCompositeController(final ThumbnailSelectorCompositeView inView,
            final ThumbnailSelectorCompositeModel inModel)
    {
        model = inModel;
        view = inView;
    }

    /**
     * Init controller.
     */
    public void init()
    {
        view.addPrevClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                if (model.hasPrevious())
                {
                    model.selectPrevious();
                    view.updateImage();
                }
            }
        });

        view.addNextClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                if (model.hasNext())
                {
                    model.selectNext();
                    view.updateImage();
                }
            }
        });

        view.addRemoveThumbClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                view.showHideThumbnail();
            }
        });
    }

}
