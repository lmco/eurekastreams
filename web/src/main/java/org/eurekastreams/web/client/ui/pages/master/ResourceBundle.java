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
package org.eurekastreams.web.client.ui.pages.master;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;

/**
 * Resource Bundle for Eureka Streams.
 */
public interface ResourceBundle extends ClientBundle
{
    /**
     * The instance of the bundle.
     */
    ResourceBundle INSTANCE = GWT.create(ResourceBundle.class);

    /**
     * Core CSS.
     * @return core.css.
     */
    @NotStrict
    @Source("style/core.css")
    CssResource coreCss();
 
    /**
     * IE CSS.
     * @return ie.css.
     */
    @NotStrict
    @Source("style/ie.css")
    CssResource ieCss();
}
