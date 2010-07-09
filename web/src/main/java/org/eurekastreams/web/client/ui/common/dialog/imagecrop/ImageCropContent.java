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
package org.eurekastreams.web.client.ui.common.dialog.imagecrop;

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The insides of the image crop dialog.
 * 
 */
public class ImageCropContent implements DialogContent, Bindable
{
    /**
     * The command to close the dialog.
     */
    private WidgetCommand closeCommand = null;
    /**
     * The person object.
     */
    private ImageUploadStrategy strategy;
    /**
     * The content panel.
     */
    private FlowPanel content = new FlowPanel();
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
    private Image baseImage;
    /**
     * The save button.
     */
    Hyperlink saveButton;
    /**
     * The close button.
     */
    Hyperlink closeButton;

    /**
     * The view.
     */
    private ImageCropContentView view;

    /**
     * Default constructor.
     * 
     * @param inStrategy
     *            the entity.
     * @param processor
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
    public ImageCropContent(final ImageUploadStrategy inStrategy,
            final ActionProcessor processor, final String inAvatarId,
            final Command inSaveCommand, final String inImageWidth,
            final String inImageHeight)
    {
        saveCommand = inSaveCommand;

        content.addStyleName("yui-skin-sam");
        content.addStyleName("avatar-crop-modal");
        strategy = inStrategy;

        AvatarUrlGenerator urlGenerator = new AvatarUrlGenerator(EntityType.PERSON);
        content
                .add(new Label(
                        "Drag or resize the box to change your avatar. When you are finished click the save button."));
        baseImage = new Image();
        baseImage.setSize(inImageWidth, inImageHeight);
        baseImage.setUrl(urlGenerator.getOriginalAvatarUrl(strategy.getId(), inAvatarId));
        baseImage.getElement().setAttribute("id", "avatarBase");

        FlowPanel imageContainer = new FlowPanel();
        imageContainer.addStyleName("image-container");
        imageContainer.add(baseImage);

        content.add(imageContainer);

        saveButton = new Hyperlink("save", Session.getInstance().generateUrl(new CreateUrlRequest()));
        saveButton.addStyleName("form-save-button");
        saveButton.addStyleName("form-button");
        content.add(saveButton);

        closeButton = new Hyperlink("cancel", "settings");
        closeButton.addStyleName("form-cancel-button");
        closeButton.addStyleName("form-button");
        content.add(closeButton);

        ImageCropContentModel model = new ImageCropContentModel(processor);
        ImageCropContentController controller = new ImageCropContentController(
                model);
        view = new ImageCropContentView(controller, this, saveCommand, strategy);
        PropertyMapper mapper = new PropertyMapper(GWT
                .create(ImageCropContent.class), GWT
                .create(ImageCropContentView.class));

        mapper.bind(this, view);
        model.registerView(view);
        view.init();

    }

    /**
     * Gets the person so the new avatar can be read.
     * 
     * @return the person.
     */
    public ImageUploadStrategy getStrategy()
    {
        return view.getStrategy();
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
     * The command to call to close the dialog.
     * 
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Sets the show command.
     * 
     * @param inShowCommand
     *            the command to use.
     */
    public void setShowCommand(final WidgetCommand inShowCommand)
    {

    }

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    public void show()
    {
        imgCrop = createImageCropper("avatarBase", strategy
                .getX(), strategy.getY(), strategy
                .getCropSize());
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
    public static native JavaScriptObject createImageCropper(
            final String imgId, final int x, final int y, final int size) /*-{
            
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
        return "image-crop-dialog";
    }
}
