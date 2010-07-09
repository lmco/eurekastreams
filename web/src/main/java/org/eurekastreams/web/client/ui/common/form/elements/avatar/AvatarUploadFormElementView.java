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

import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.imagecrop.ImageCropContent;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * The view for the avatar upload form element.
 *
 */
public class AvatarUploadFormElementView implements Bindable
{
    /**
     * the edit button.
     */
    Anchor editButton;
    /**
     * the delete button.
     */
    Anchor deleteButton;
    /**
     * the error box for form validation error.
     */
    FlowPanel errorBox;
    /**
     * The container for the avatar itself.
     */
    FlowPanel avatarContainer;
    /**
     * The form for the uploading.
     */
    FormPanel uploadForm;

    /**
     * hidden image for full size avatar, only loaded on resize, never displayed.
     */
    Image hiddenImage;

    /**
     * The avatar ID of the person.
     */
    private String avatarId = "";
    /**
     * The person.
     */
    private ImageUploadStrategy strategy;
    /**
     * The image crop content widget.
     */
    private ImageCropContent imageCropDialog;
    /**
     * The widget.
     */
    private AvatarUploadFormElement widget;
    /**
     * The controller.
     */
    private AvatarUploadFormElementController controller;

    /**
     * Default constructor.
     *
     * @param inController
     *            the controller.
     * @param inWidget
     *            the widget.
     * @param inStrategy
     *            the entity.
     */
    public AvatarUploadFormElementView(
            final AvatarUploadFormElementController inController,
            final AvatarUploadFormElement inWidget,
            final ImageUploadStrategy inStrategy)
    {
        strategy = inStrategy;
        widget = inWidget;
        controller = inController;
    }

    /**
     * Wire up the events.
     */
    public void init()
    {
        controller.addFormHandler(uploadForm);
        if (editButton != null)
        {
            controller.addResizeClickListener(editButton);
            controller.addHiddenImageLoadListener(hiddenImage, this);
        }
    }

    /**
     * Gets fired off when the avatar ID is changed. param inAvatarId the avatar
     * ID.
     *
     * @param inAvatarId
     *            the avatar id.
     */
    public void onAvatarIdChanged(final String inAvatarId)
    {
        onAvatarIdChanged(inAvatarId, (inAvatarId != null), (inAvatarId != null));
    }

    /**
     * Gets fired off when the avatar id is changed.
     * @param inAvatarId - the avatar id.
     * @param inDisplayDelete - flag telling to display or hide the delete button.
     * @param inDisplayEdit - flag telling to display or hide the edit button
     */
    public void onAvatarIdChanged(final String inAvatarId, final boolean inDisplayDelete, final boolean inDisplayEdit)
    {
        Session.getInstance().getCurrentPerson().setAvatarId(inAvatarId);

        avatarId = inAvatarId;
        Widget avatar = widget.createImage(strategy, avatarId);
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
    /**
     * Gets fired off when the resize panel being shown is changed.
     *
     * @param value
     *            the value.
     */
    public void onResizePanelShownChanged(final boolean value)
    {
        //Since the size of the image is required before we can correctly show the
        //resize dialog, this method determines the avatar url and sets image url.
        //The load event of that image being loaded will kick off the resize modal.
        AvatarUrlGenerator urlGenerator = new AvatarUrlGenerator(EntityType.PERSON);
        hiddenImage.setUrl(urlGenerator.getOriginalAvatarUrl(strategy.getId(), avatarId));
    }

    /**
     * Shows image resize modal.
     * @param inImageWidth Width of original image to resize.
     * @param inImageHeight Height of original image to resize.
     */
    public void showResizeModal(final String inImageWidth, final String inImageHeight)
    {
        imageCropDialog = widget.createImageCropContent(strategy, avatarId, inImageWidth, inImageHeight);
        Dialog dialog = widget.createDialog(imageCropDialog);
        dialog.setBgVisible(true);
        dialog.center();
    }

    /**
     * Gets fired when the user clicks save on the resize modal.
     */
    public void onSave()
    {
        strategy = imageCropDialog.getStrategy();
        onAvatarIdChanged(strategy.getImageId());
    }

    /**
     * Gets fired when the forms result has changed. If its "fail", the upload
     * has failed.
     *
     * @param result
     *            the result.
     */
    public void onFormResultChanged(final String result)
    {
        errorBox.setVisible(result.equals("fail"));
        if (!result.equals("fail"))
        {
            String[] results = result.split(",");
            if (results.length >= 4)
            {
                strategy.setX(Integer.parseInt(results[1]));
                strategy.setY(Integer.parseInt(results[2]));
                strategy.setCropSize(Integer.parseInt(results[3]));
            }
            onAvatarIdChanged(results[0]);
        }
    }
}
