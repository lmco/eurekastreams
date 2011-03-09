/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.model;

import net.sf.gilead.pojo.gwt.LightEntity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Wrapper around Gilead's LightEntity annotated properly to allow it to be serialized to JSON using Jackson.
 *
 * Jackson 1.6 throws an exception if it tries to serialize an object to JSON which contains a direct reference to
 * itself. (That feature is intended as inexpensive protection against cyclical graphs; although it would be nice if we
 * could tell Jackson to simply throw the field away instead of throw an exception.) Gilead 1.3.2 has a property on
 * LightEntity called underlyingValue which is a direct reference back to the object itself. Thus any class derived from
 * LightEntity cannot be serialized to JSON with Jackson. However, it is possible to annotate a class to tell Jackson to
 * ignore certain properties; that is done here to tell Jackson to ignore the troublesome underlyingValue.
 */
@JsonIgnoreProperties({ "underlyingValue" })
public abstract class WrappedLightEntity extends LightEntity
{
    /** Fingerprint. */
    private static final long serialVersionUID = -4130719726995975906L;
}
