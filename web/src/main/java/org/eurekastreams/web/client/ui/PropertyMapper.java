/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui;

/**
 * Maps properties between two Bindables.
 * 
 */
public class PropertyMapper
{
    /**
     * The bindable view.
     */
    private HashedBindable viewHashedBindable;

    /**
     * The bindable controller.
     */
    private HashedBindable controllerHashedBindable;

    /**
     * Constructor.
     * 
     * @param inViewHashedBindable
     *            the view as a bindable.
     * @param inControllerHashedBindable
     *            the controller as a bindable.
     */
    public PropertyMapper(final Object inViewHashedBindable,
            final Object inControllerHashedBindable)
    {
        viewHashedBindable = (HashedBindable) inViewHashedBindable;
        controllerHashedBindable = (HashedBindable) inControllerHashedBindable;
    }

    /**
     * Binds the controller to the view.
     * 
     * @param view
     *            the view.
     * @param controller
     *            the controller.
     */
    public void bind(final Bindable view, final Bindable controller)
    {
        viewHashedBindable.populateHash(view);
        controllerHashedBindable.populateHash(controller);

        for (String controllerField : controllerHashedBindable.getFields())
        {
            Object viewProp = viewHashedBindable.get(controllerField);

            if (viewProp != null)
            {
                controllerHashedBindable.set(controllerField, viewProp);
            }
        }
    }
}
