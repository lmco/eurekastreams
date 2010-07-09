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
package org.eurekastreams.commons.hibernate;

import org.eurekastreams.commons.search.modelview.ModelView;

/**
 * Factory to create a ModelView.
 *
 * @param <T>
 *            the type of ModelView to build and return
 */
public abstract class ModelViewFactory<T extends ModelView>
{
    /**
     * Build the ModelView object.
     *
     * @return an instance of a ModelView.
     */
    public abstract T buildModelView();
}
