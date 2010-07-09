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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;

/**
 * The form element interface. This represents *ONE* property on the model you
 * are updating.
 * 
 */
public interface FormElement
{
    /**
     * The *private* key on the model object you are updating. In other words,
     * if you are updating a Person objects last name, this will probably be
     * something like "lastName".
     * 
     * @return the key.
     */
    String getKey();

    /**
     * The value to set the models property (determined by its key) to.
     * 
     * @return the value.
     */
    Serializable getValue();

    /**
     * This gets called when the form element has failed validation. Respond
     * appropriately.
     * 
     * @param errMessage
     *            the error message sent back from the server.
     */
    void onError(String errMessage);

    /**
     * This gets called when the form element has not received a validation
     * error, but this does NOT mean that it has persisted back. If ANY form
     * elements on the form fail, the entire form fails, but this method will be
     * called on the valid entries so they may undo what they did in onError.
     */
    void onSuccess();
}
