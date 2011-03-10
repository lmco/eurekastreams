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
package org.eurekastreams.web.client.ui.common.dialog.imagecrop;

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.profile.ResizeAvatarRequest;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The insides of the image crop dialog.
 */
public class ImageCropContent extends BaseDialogContent
{
    /**
     * The person object.
     */
    private final ImageUploadStrategy strategy;
    /**
     * The content panel.
     */
    private final FlowPanel content = new FlowPanel();
    /**
     * The command to exe on save.
     */
    private Command saveCommand = null;
    /**
     * The JS object returned from YUI.
     */
    private JavaScriptObject imgCrop;
    /**
     * The image itself.
     */
    private final Image baseImage;
    /**
     * The save button.
     */
    private final Hyperlink saveButton;
    /**
     * The close button.
     */
    private final Hyperlink closeButton;

    /**
     * Action processor. TODO: Replace with use of models.
     */
    private final ActionProcessor processor;

    /**
     * Default constructor.
     *
     * @param inStrategy
     *            the entity.
     * @param inProcessor
     *            the processor.
     * @param inAvatarId
     *            the avatar ID.
     * @param inSaveCommand
     *            the save command.
     * @param inImageWidth
     *            Image width.
     * @param inImageHeight
     *            Image height.
     */
    public ImageCropContent(final ImageUploadStrategy inStrategy, final ActionProcessor inProcessor,
            final String inAvatarId, final Command inSaveCommand, final String inImageWidth, final String inImageHeight)
    {
        saveCommand = inSaveCommand;
        processor = inProcessor;

        content.addStyleName(StaticResourceBundle.INSTANCE.coreCss().yuiSkinSam());
        content.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarCropModal());
        strategy = inStrategy;

        AvatarUrlGenerator urlGenerator = new AvatarUrlGenerator(EntityType.PERSON);
        content.add(new Label(
                "Drag or resize the box to change your avatar. When you are finished click the save button."));
        baseImage = new Image();
        baseImage.setSize(inImageWidth, inImageHeight);
        baseImage.setUrl(urlGenerator.getOriginalAvatarUrl(strategy.getId(), inAvatarId));
        baseImage.getElement().setAttribute("id", "avatarBase");

        FlowPanel imageContainer = new FlowPanel();
        imageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().imageContainer());
        imageContainer.add(baseImage);

        content.add(imageContainer);

        saveButton = new Hyperlink("save", Session.getInstance().generateUrl(new CreateUrlRequest()));
        saveButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formSaveButton());
        saveButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());
        content.add(saveButton);

        closeButton = new Hyperlink("cancel", StaticResourceBundle.INSTANCE.coreCss().settings());
        closeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formCancelButton());
        closeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());
        content.add(closeButton);

        setupEvents();
    }

    /**
     * Wire up events.
     */
    private void setupEvents()
    {
        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                close();
            }
        });

        saveButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                List<Integer> coords = getCoords();

                ResizeAvatarRequest request = new ResizeAvatarRequest(coords.get(0), coords.get(1), coords.get(2),
                        Boolean.TRUE, strategy.getId());

                // TODO: refactor to new simplified model design (and thus eliminiate use of the action processor here)
                processor.makeRequest(new ActionRequestImpl<AvatarEntity>(strategy.getResizeAction(), request),
                        new AsyncCallback<AvatarEntity>()
                        {
                            public void onFailure(final Throwable caught)
                            {
                            }

                            public void onSuccess(final AvatarEntity result)
                            {
                                strategy.setEntity(result);
                                saveCommand.execute();
                                close();
                            }
                        });
            }
        });
    }

    /**
     * The title of the login dialog.
     *
     * @return the title.
     */
    public final String getTitle()
    {
        return "Resize Photo";
    }

    /**
     * The login form.
     *
     * @return the login form.
     */
    public final Widget getBody()
    {
        return content;
    }

    /**
     * Returns the form panel.
     *
     * @return the form panel.
     */
    public FormPanel getFormPanel()
    {
        return null;
    }

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    @Override
    public void show()
    {
        imgCrop = createImageCropper("avatarBase", strategy.getX(), strategy.getY(), strategy.getCropSize());
    }

    /**
     * Gets the coords from YUI.
     *
     * @return the coords of the crop.
     */
    public List<Integer> getCoords()
    {
        List<Integer> coords = new LinkedList<Integer>();
        coords.add(new Integer(getX(imgCrop)));
        coords.add(new Integer(getY(imgCrop)));
        coords.add(new Integer(getSize(imgCrop)));
        return coords;
    }

    /**
     * Wraps YUI and creates the image crop.
     *
     * @param imgId
     *            the ID of the image tag.
     * @param x
     *            the start of the crop window.
     * @param y
     *            the start of the crop window.
     * @param size
     *            the size of the crop window.
     * @return the JS obj to ref.
     */
    public static native JavaScriptObject createImageCropper(final String imgId, final int x, final int y,
            final int size) /*-{

                            var crop =  new $wnd.YAHOO.widget.ImageCropper(imgId, {
                            status: false,
                            ratio: true,
                            minHeight: 75,
                            minWidth: 75,
                            initHeight: size,
                            initWidth: size,
                            initialXY: [x, y]
                            });

                            return crop;
                            }-*/;

    /**
     * Wraps YUI to get the X coord.
     *
     * @param imgCrop
     *            the JS object.
     * @return the X coord.
     */
    public static native int getX(final JavaScriptObject imgCrop) /*-{
                                                                  var cropArea = imgCrop.getCropCoords();
                                                                  return cropArea.left;
                                                                  }-*/;

    /**
     * Wraps the YUI to get the Y coord.
     *
     * @param imgCrop
     *            the JS object.
     * @return the Y coord.
     */
    public static native int getY(final JavaScriptObject imgCrop) /*-{
                                                                  var cropArea = imgCrop.getCropCoords();
                                                                  return cropArea.top;
                                                                  }-*/;

    /**
     * Wraps the YUI to get the size of the crop.
     *
     * @param imgCrop
     *            the JS object.
     * @return the size of the crop.
     */
    public static native int getSize(final JavaScriptObject imgCrop) /*-{
                                                                     var cropArea = imgCrop.getCropCoords();
                                                                     return cropArea.width;
                                                                     }-*/;

    /**
     * Gets the CSS name for the dialog.
     *
     * @return the css name.
     */
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().imageCropDialog();
    }
}
