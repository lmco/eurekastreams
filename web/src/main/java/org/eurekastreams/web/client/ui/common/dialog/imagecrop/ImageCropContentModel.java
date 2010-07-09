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

import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.profile.ResizeAvatarRequest;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.AvatarUploadStrategy;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The model for the image crop dialog.
 *
 */
public class ImageCropContentModel
{
    /**
     * The coords of the crop.
     */
    private List<Integer> coords;
    /**
     * Whether or not the dialog is shown.
     */
    private boolean isShown = true;
    /**
     * The view.
     */
    private ImageCropContentView view;
    /**
     * The processor.
     */
    private ActionProcessor processor;

    /**
     * Default constructor.
     *
     * @param inProcessor
     *            the processor.
     */
    public ImageCropContentModel(final ActionProcessor inProcessor)
    {
        processor = inProcessor;
    }

    /**
     * Registers a view with the model.
     *
     * @param inView
     *            the view.
     */
    public void registerView(final ImageCropContentView inView)
    {
        view = inView;
    }

    /**
     * Sets the coords of the model.
     *
     * @param inCoords
     *            the coords to set.
     */
    public void setCoords(final List<Integer> inCoords)
    {
        coords = inCoords;

        ResizeAvatarRequest request = new ResizeAvatarRequest(coords.get(0), coords.get(1), coords.get(2),
                Boolean.TRUE, view.getStrategy().getId());

        // TODO: refactor to new simplified model design (and thus eliminiate use of the action processor here)
        processor.makeRequest(new ActionRequestImpl<AvatarEntity>(view.getStrategy().getResizeAction(), request),
                new AsyncCallback<AvatarEntity>()
                {
                    /* implement the async call back methods */
                    public void onFailure(final Throwable caught)
                    {

                    }

                    public void onSuccess(final AvatarEntity result)
                    {
                        ImageUploadStrategy strategy = null;
                        if (result instanceof Person)
                        {
                            strategy = new AvatarUploadStrategy<Person>((Person) result, "resizePersonAvatar",
                                    EntityType.PERSON);
                        }
                        else if (result instanceof DomainGroup)
                        {
                            strategy = new AvatarUploadStrategy<DomainGroup>((DomainGroup) result, "resizeGroupAvatar",
                                    EntityType.GROUP);
                        }
                        else if (result instanceof Organization)
                        {
                            strategy = new AvatarUploadStrategy<Organization>((Organization) result, "resizeOrgAvatar",
                                    EntityType.ORGANIZATION);
                        }
                        // TODO in the future take in a factory.
                        setStrategy(strategy);
                        setIsShown(false);
                    }
                });
    }

    /**
     * Sets the person in the model.
     *
     * @param inStrategy
     *            the strategy.
     */
    public void setStrategy(final ImageUploadStrategy inStrategy)
    {
        view.onStrategyUpdated(inStrategy);
    }

    /**
     * Sets whether or not the dialog is shown.
     *
     * @param value
     *            the value.
     */
    public void setIsShown(final boolean value)
    {
        isShown = value;
        view.onIsShownChanged(isShown);
    }
}
