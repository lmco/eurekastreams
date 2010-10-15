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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;

/**
 * The model for the avatar upload form panel.
 * 
 */
public class AvatarUploadFormElementModel
{

    /**
     * the view.
     */
    private AvatarUploadFormElementView view;
    /**
     * Whether or not the resize panel is shown.
     */
    private boolean resizePanelShown = false;
    /**
     * the avatar id.
     */
    private String avatarId;
    /**
     * the result of the form submission.
     */
    private String formResult;

    /**
     * The image upload strategy.
     */
    private ImageUploadStrategy strategy;

    /**
     * Default constructor.
     * 
     * @param inStrategy
     *            the upload strategy.
     */
    public AvatarUploadFormElementModel(final ImageUploadStrategy inStrategy)
    {
        strategy = inStrategy;
    }

    /**
     * Registers a view with the model.
     * 
     * @param inView
     *            the view.
     */
    public void registerView(final AvatarUploadFormElementView inView)
    {
        view = inView;
    }

    /**
     * Sets the state of the resize panel.
     * 
     * @param value
     *            the value.
     */
    public void setResizePanelShown(final boolean value)
    {
        resizePanelShown = value;
        view.onResizePanelShownChanged(resizePanelShown);
    }

    /**
     * Sets the avatar id of the user.
     * 
     * @param inAvatarId
     *            the avatar id.
     */
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
        view.onAvatarIdChanged(avatarId, strategy.getEntityType() == EntityType.PERSON);
    }

    /**
     * sets the form result of the submission.
     * 
     * @param inFormResult
     *            the result.
     */
    public void setFormResult(final String inFormResult)
    {
        formResult = inFormResult;
        view.onFormResultChanged(formResult);
    }
}
