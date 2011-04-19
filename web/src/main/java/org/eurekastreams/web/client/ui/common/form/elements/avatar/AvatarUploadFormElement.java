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
package org.eurekastreams.web.client.ui.common.form.elements.avatar;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent.ImageType;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.imagecrop.ImageCropContent;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The form element for the avatar upload. Its not a REAL form element because it doesnt save back with the form itself,
 * its Miss Independent.
 */
public class AvatarUploadFormElement extends FlowPanel
{
    /**
     * The panel.
     */
    private final FlowPanel panel = new FlowPanel();
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
     * The avatar ID of the person.
     */
    private String avatarId = "";

    /**
     * The image crop content widget.
     */
    private ImageCropContent imageCropDialog;

    /**
     * The image upload strategy.
     */
    private final ImageUploadStrategy strategy;

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
        errorBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        errorBox.add(new Label("There was an error uploading your image. Please be sure "
                + "that your photo is under 4MB and is a PNG, JPG, or GIF."));

        errorBox.setVisible(false);

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formAvatarUpload());
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formElement());

        processor = inProcessor;
        // AvatarEntity Entity = inEntity;

        uploadForm.setAction(servletPath);

        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);

        Label photoLabel = new Label(label);
        photoLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        panel.add(photoLabel);

        FlowPanel avatarModificationPanel = new FlowPanel();
        avatarModificationPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarModificationPanel());

        avatarContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarContainer());
        avatarModificationPanel.add(avatarContainer);

        FlowPanel photoButtonPanel = new FlowPanel();
        photoButtonPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formPhotoButtonPanel());

        if (resizeable)
        {
            editButton = new Anchor("Resize");
            editButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formResizeButton());
            editButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());
            photoButtonPanel.add(editButton);
        }

        deleteButton = new Anchor("Delete");
        deleteButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formDeleteButton());
        deleteButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());
        photoButtonPanel.add(deleteButton);

        avatarModificationPanel.add(photoButtonPanel);
        panel.add(avatarModificationPanel);

        FlowPanel uploadPanel = new FlowPanel();
        uploadPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formUploadPanel());
        uploadPanel.add(errorBox);

        // Wrapping the FileUpload because otherwise IE7 shifts it way
        // to the right. I couldn't figure out why,
        // but for whatever reason, this works.
        FlowPanel fileUploadWrapper = new FlowPanel();
        FileUpload upload = new FileUpload();
        upload.setName("imageUploadFormElement");
        upload.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formAvatarUpload());
        fileUploadWrapper.add(upload);
        uploadPanel.add(fileUploadWrapper);

        uploadPanel.add(new Label(description));
        Anchor submitButton = new Anchor("");
        submitButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formUploadButton());
        submitButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());

        uploadPanel.add(submitButton);

        hiddenImage = new Image();
        hiddenImage.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarHiddenOriginal());
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

        uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler()
        {
            public void onSubmitComplete(final SubmitCompleteEvent ev)
            {
                String result = ev.getResults().replaceAll("\\<.*?\\>", "");
                final boolean fail = "fail".equals(result);
                errorBox.setVisible(fail);
                if (!fail)
                {
                    String[] results = result.split(",");
                    if (results.length >= 4)
                    {
                        strategy.setX(Integer.parseInt(results[1]));
                        strategy.setY(Integer.parseInt(results[2]));
                        strategy.setCropSize(Integer.parseInt(results[3]));
                    }
                    onAvatarIdChanged(results[0], strategy.getEntityType() == EntityType.PERSON);
                }
            }
        });

        if (editButton != null)
        {
            editButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent inArg0)
                {
                    // Since the size of the image is required before we can correctly show the
                    // resize dialog, this method determines the avatar url and sets image url.
                    // The load event of that image being loaded will kick off the resize modal.
                    AvatarUrlGenerator urlGenerator = new AvatarUrlGenerator(EntityType.PERSON);
                    hiddenImage.setUrl(urlGenerator.getOriginalAvatarUrl(strategy.getId(), avatarId));
                }
            });
            hiddenImage.addLoadHandler(new LoadHandler()
            {
                public void onLoad(final LoadEvent inEvent)
                {
                    imageCropDialog = new ImageCropContent(strategy, processor, avatarId, new Command()
                    {
                        public void execute()
                        {
                            onAvatarIdChanged(strategy.getImageId(), strategy.getEntityType() == EntityType.PERSON);
                        }
                    }, hiddenImage.getWidth() + "px", hiddenImage.getHeight() + "px");

                    Dialog dialog = new Dialog(imageCropDialog);
                    dialog.showCentered();
                }
            });
        }

        if (strategy.getImageType().equals(ImageType.BANNER))
        {
            onAvatarIdChanged(strategy.getImageId(), strategy.getId().equals(strategy.getImageEntityId()), true,
                    strategy.getEntityType() == EntityType.PERSON);
        }
        else
        {
            onAvatarIdChanged(strategy.getImageId(), strategy.getEntityType() == EntityType.PERSON);
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
                                onAvatarIdChanged(event.getEntity().getBannerId(), strategy.getId().equals(
                                        event.getEntity().getBannerEntityId()), true,
                                        strategy.getEntityType() == EntityType.PERSON);
                            }
                            else
                            {
                                onAvatarIdChanged(null, strategy.getEntityType() == EntityType.PERSON);
                            }
                        }
                    }
                });

    }

    /**
     * Gets fired off when the avatar ID is changed. param inAvatarId the avatar ID.
     * 
     * @param inAvatarId
     *            the avatar id.
     * @param setPersonAvatar
     *            if the person's avatar should be changed.
     */
    public void onAvatarIdChanged(final String inAvatarId, final boolean setPersonAvatar)
    {
        onAvatarIdChanged(inAvatarId, (inAvatarId != null), (inAvatarId != null), setPersonAvatar);
    }

    /**
     * Gets fired off when the avatar id is changed.
     * 
     * @param inAvatarId
     *            - the avatar id.
     * @param inDisplayDelete
     *            - flag telling to display or hide the delete button.
     * @param inDisplayEdit
     *            - flag telling to display or hide the edit button
     * @param setPersonAvatar
     *            if the person's avatar should be changed.
     */
    public void onAvatarIdChanged(final String inAvatarId, final boolean inDisplayDelete, final boolean inDisplayEdit,
            final boolean setPersonAvatar)
    {
        if (setPersonAvatar)
        {
            Session.getInstance().getCurrentPerson().setAvatarId(inAvatarId);
        }

        avatarId = inAvatarId;
        Widget avatar = strategy.getImage(avatarId);
        avatarContainer.clear();
        avatarContainer.add(avatar);

        if (editButton != null)
        {
            editButton.setVisible(inDisplayEdit);
        }

        if (deleteButton != null)
        {
            deleteButton.setVisible(inDisplayDelete);
        }
    }
}
