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

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent.ImageType;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.dialog.imagecrop.ImageCropContent;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The form element for the avatar upload. Its not a REAL form element because it doesnt save back with the form itself,
 * its Miss Independent.
 * 
 */
public class AvatarUploadFormElement extends FlowPanel implements Bindable
{
    /**
     * The view.
     */
    private AvatarUploadFormElementView view;

    /**
     * The panel.
     */
    private FlowPanel panel = new FlowPanel();
    /**
     * the processor.
     */
    private ActionProcessor processor = null;
    /**
     * the upload form.
     */
    FormPanel uploadForm = new FormPanel();
    /**
     * the edit button.
     */
    Anchor editButton;
    /**
     * the delete button.
     */
    Anchor deleteButton;
    /**
     * the error box.
     */
    FlowPanel errorBox;
    /**
     * the avatar container.
     */
    FlowPanel avatarContainer = new FlowPanel();

    /**
     * hidden image for full size avatar, only loaded on resize, never displayed.
     */
    Image hiddenImage;

    /**
     * The image upload strategy.
     */
    private ImageUploadStrategy strategy;

    /**
     * Default description if none is provided.
     */
    private static String description = 
        // line break.
        "Select a JPG, PNG or GIF image from your computer. The maximum file size is 4MB.";

    /**
     * Create an avatar upload form element.
     * 
     * @param label
     *            the label of the element.
     * @param servletPath
     *            the path to hit to upload the image
     * @param inProcessor
     *            the processor.
     * @param inStrategy
     *            the strategy.
     */
    public AvatarUploadFormElement(final String label, final String servletPath, final ActionProcessor inProcessor,
            final ImageUploadStrategy inStrategy)
    {
        this(label, description, servletPath, inProcessor, inStrategy);
    }

    /**
     * Create an avatar upload form element.
     * 
     * @param label
     *            the label of the element.
     * @param desc
     *            the description.
     * @param servletPath
     *            the path to hit to upload the image
     * @param inProcessor
     *            the processor.
     * @param inStrategy
     *            the strategy.
     */
    public AvatarUploadFormElement(final String label, final String desc, final String servletPath,
            final ActionProcessor inProcessor, final ImageUploadStrategy inStrategy)
    {
        description = desc;
        strategy = inStrategy;
        Boolean resizeable = strategy.isResizable();

        errorBox = new FlowPanel();
        errorBox.addStyleName("form-error-box");
        errorBox.add(new Label("There was an error uploading your image. Please be sure "
                + "that your photo is under 4MB and is a PNG, JPG, or GIF."));

        errorBox.setVisible(false);

        this.addStyleName("form-avatar-upload");
        this.addStyleName("form-element");

        processor = inProcessor;
        // AvatarEntity Entity = inEntity;

        uploadForm.setAction(servletPath);

        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);

        Label photoLabel = new Label(label);
        photoLabel.addStyleName("form-label");
        panel.add(photoLabel);

        FlowPanel avatarModificationPanel = new FlowPanel();
        avatarModificationPanel.addStyleName("avatar-modification-panel");

        avatarContainer.addStyleName("avatar-container");
        avatarModificationPanel.add(avatarContainer);

        FlowPanel photoButtonPanel = new FlowPanel();
        photoButtonPanel.addStyleName("form-photo-button-panel");

        if (resizeable)
        {
            editButton = new Anchor("Resize");
            editButton.addStyleName("form-resize-button");
            editButton.addStyleName("form-button");
            photoButtonPanel.add(editButton);
        }

        deleteButton = new Anchor("Delete");
        deleteButton.addStyleName("form-delete-button");
        deleteButton.addStyleName("form-button");
        photoButtonPanel.add(deleteButton);

        avatarModificationPanel.add(photoButtonPanel);
        panel.add(avatarModificationPanel);

        FlowPanel uploadPanel = new FlowPanel();
        uploadPanel.addStyleName("form-upload-panel");
        uploadPanel.add(errorBox);

        // Wrapping the FileUpload because otherwise IE7 shifts it way
        // to the right. I couldn't figure out why,
        // but for whatever reason, this works.
        FlowPanel fileUploadWrapper = new FlowPanel();
        FileUpload upload = new FileUpload();
        upload.setName("imageUploadFormElement");
        upload.addStyleName("form-avatar-file-upload");
        fileUploadWrapper.add(upload);
        uploadPanel.add(fileUploadWrapper);

        uploadPanel.add(new Label(description));
        Anchor submitButton = new Anchor("");
        submitButton.addStyleName("form-upload-button");
        submitButton.addStyleName("form-button");

        uploadPanel.add(submitButton);

        hiddenImage = new Image();
        hiddenImage.addStyleName("avatar-hidden-original");
        uploadPanel.add(hiddenImage);

        panel.add(uploadPanel);

        submitButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                uploadForm.submit();
            }
        });

        uploadForm.setWidget(panel);
        this.add(uploadForm);

        AvatarUploadFormElementModel model = new AvatarUploadFormElementModel(strategy);
        AvatarUploadFormElementController controller = new AvatarUploadFormElementController(model,
                new WidgetJSNIFacadeImpl());
        view = new AvatarUploadFormElementView(controller, this, strategy);
        PropertyMapper mapper = new PropertyMapper(GWT.create(AvatarUploadFormElement.class), GWT
                .create(AvatarUploadFormElementView.class));

        mapper.bind(this, view);
        model.registerView(view);
        view.init();
        if (strategy.getImageType().equals(ImageType.BANNER))
        {
            view.onAvatarIdChanged(strategy.getImageId(), strategy.getId().equals(strategy.getImageEntityId()), true,
                    strategy.getEntityType() == EntityType.PERSON);
        }
        else
        {
            view.onAvatarIdChanged(strategy.getImageId(), strategy.getEntityType() == EntityType.PERSON);
        }

        deleteButton.addClickHandler(new ClickHandler()
        {
            @SuppressWarnings("unchecked")
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete your current photo?"))
                {
                    strategy.getDeleteAction().delete(strategy.getDeleteParam());
                }
            }
        });

        Session.getInstance().getEventBus().addObserver(ClearUploadedImageEvent.class,
                new Observer<ClearUploadedImageEvent>()
                {
                    public void update(final ClearUploadedImageEvent event)
                    {
                        if (event.getImageType().equals(strategy.getImageType())
                                && event.getEntityType().equals(strategy.getEntityType()))
                        {
                            if (event.getImageType().equals(ImageType.BANNER))
                            {
                                view.onAvatarIdChanged(event.getEntity().getBannerId(), strategy.getId().equals(
                                        event.getEntity().getBannerEntityId()), true,
                                        strategy.getEntityType() == EntityType.PERSON);
                            }
                            else
                            {
                                view.onAvatarIdChanged(null, strategy.getEntityType() == EntityType.PERSON);
                            }
                        }
                    }
                });

    }

    /**
     * Creates the inside dialog content for the view.
     * 
     * @param inStrategy
     *            the entity.
     * @param avatarId
     *            the avatar id.
     * @param inImageWidth
     *            image width.
     * @param inImageHeight
     *            image height.
     * @return the image crop content widget.
     */
    ImageCropContent createImageCropContent(final ImageUploadStrategy inStrategy, final String avatarId,
            final String inImageWidth, final String inImageHeight)
    {
        strategy = inStrategy;
        return new ImageCropContent(strategy, processor, avatarId, new Command()
        {
            public void execute()
            {
                view.onSave();
            }
        }, inImageWidth, inImageHeight);
    }

    /**
     * Creates an image for the view.
     * 
     * @param inStrategy
     *            the entity object.
     * @param avatarId
     *            the avatar id.
     * @return the image.
     */
    Widget createImage(final ImageUploadStrategy inStrategy, final String avatarId)
    {
        strategy = inStrategy;
        return strategy.getImage(avatarId);
    }

    /**
     * Creates the dialog for the view.
     * 
     * @param imageCropDialog
     *            the dialog
     * @return the dialog.
     */
    Dialog createDialog(final DialogContent imageCropDialog)
    {
        return new Dialog(imageCropDialog);
    }

    /**
     * Set the avatar id.
     * 
     * @param inAvatarId
     *            the avatar id.
     */
    public void setAvatarId(final String inAvatarId)
    {
        view.onAvatarIdChanged(inAvatarId, strategy.getEntityType() == EntityType.PERSON);
    }

}
