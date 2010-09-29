/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

/**
 * Transformer that does nothing.
 * 
 * @param <T>
 *            Type to transform.
 */
public class PassThroughTransformer<T> implements Transformer<T, T>
{

    /**
     * Does nothing, returns what was passed in.
     * 
     * @param inTransformType
     *            Object to transform.
     * @return same object that was passed in. No Transformation.
     */
    @Override
    public T transform(final T inTransformType)
    {
        return inTransformType;
    }

}
