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
package org.eurekastreams.web.client.ui.common.form.elements.avatar;

import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * The controller for the avatar upload panel.
 * 
 */
public class AvatarUploadFormElementController
{
    /**
     * The model.
     */
    private AvatarUploadFormElementModel model;

    /**
     * the jsni facade so we can do evil things inside the view.
     */
    private WidgetJSNIFacade jSNIFacade;

    /**
     * The controller constructor.
     * 
     * @param inModel
     *            the model.
     * @param inJSNIFacade
     *            the jsni facade.
     */
    public AvatarUploadFormElementController(final AvatarUploadFormElementModel inModel,
            final WidgetJSNIFacade inJSNIFacade)
    {
        model = inModel;
        jSNIFacade = inJSNIFacade;
    }



    /**
     * Adds the resize click listener.
     * 
     * @param resizeButton
     *            the resize button.
     */
    public void addResizeClickListener(final Anchor resizeButton)
    {
        resizeButton.addClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                model.setResizePanelShown(true);
            }
        });
    }

    /**
     * Adds a form handler to the form itself.
     * 
     * @param uploadForm
     *            the form itself.
     */
    public void addFormHandler(final FormPanel uploadForm)
    {
        uploadForm.addFormHandler(new FormHandler()
        {
            public void onSubmit(final FormSubmitEvent arg0)
            {
            }

            /**
             * On submit complete, the form will wrap the result in
             * 
             * <pre>
             * tags, or anything else, it depends on the browser (crappy, I know).
             * So lets strip out ALL HTML before we give it back to the model.
             */
            public void onSubmitComplete(final FormSubmitCompleteEvent arg0)
            {
                String result = arg0.getResults().replaceAll("\\<.*?\\>", "");
                model.setFormResult(result);
            }
        });
    }

    /**
     * Adds listener to hidden large version of avatar. 
     * @param inHiddenImage Hidden image.
     * @param inView The view.
     */
    public void addHiddenImageLoadListener(final Image inHiddenImage, final AvatarUploadFormElementView inView)
    {
        inHiddenImage.addLoadHandler(new LoadHandler()
        {
            public void onLoad(final LoadEvent inEvent)
            {
                prefetchAvatarAndShowResize(inHiddenImage, inView);
            }
        });
    }

    /**
     * Have to prefetch large version of avatar so resize modal can be created with correct dimentions.
     * @param inHiddenImage The hidden version of large avatar.
     * @param inView The AvatarUploadFormElementView.
     */
    private void prefetchAvatarAndShowResize(final Image inHiddenImage, final AvatarUploadFormElementView inView)
    {
        inView.showResizeModal(inHiddenImage.getWidth() + "px", inHiddenImage.getHeight() + "px");
    }
}
