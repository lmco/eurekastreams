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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.hibernate.transform.ResultTransformer;

/**
 * ResultTransformer to create and populate ModelViews from property/field-name
 * arrays.
 *
 * @param <ModelViewType>
 *            the type of ModelViews to build and setup
 */
public class ModelViewResultTransformer<ModelViewType extends ModelView>
        implements ResultTransformer
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -5822701092119140447L;

    /**
     * Factory to build the appropriate ModelView.
     */
    private ModelViewFactory<ModelViewType> modelViewFactory;

    /**
     * Constructor, taking the reflective instantiator and model view class to
     * return.
     *
     * @param inModelViewFactory
     *            the factory to use to build our ModelViews
     */
    public ModelViewResultTransformer(
            final ModelViewFactory<ModelViewType> inModelViewFactory)
    {
        modelViewFactory = inModelViewFactory;
    }

    /**
     * Return the input list.
     *
     * @param collection
     *            the collection to return
     * @return the input list, no transformation
     */
    @Override
    @SuppressWarnings("unchecked")
    public List transformList(final List collection)
    {
        return collection;
    }

    /**
     * Transform the array of objects and column aliases to a ModelView using
     * reflection and the ModelView's ability to import a property Map.
     *
     * @param tuple
     *            array of properties
     * @param aliases
     *            array of Strings representing the property aliases in tuple
     *
     * @return a ModelView representation of the input property/alias arrays
     */
    @Override
    public ModelViewType transformTuple(final Object[] tuple,
            final String[] aliases)
    {
        // Transform the tuples into a property map
        Map<String, Object> properties = getMapFromTuplesAndAliases(tuple,
                aliases);

        // instantiate, load the properties, and return the ModelView
        ModelViewType modelView = modelViewFactory.buildModelView();
        modelView.loadProperties(properties);
        return modelView;
    }

    /**
     * Convert the input tuple and aliases to a key-value mapping.
     *
     * @param tuple
     *            values returned from Hibernate Search.
     *
     * @param aliases
     *            result keys returned from Hibernate Search.
     *
     * @return a key-value mapping of the Hibernate search tupple/aliases.
     */
    protected Map<String, Object> getMapFromTuplesAndAliases(
            final Object[] tuple, final String[] aliases)
    {
        Map<String, Object> result = new HashMap<String, Object>(tuple.length);
        for (int i = 0; i < tuple.length; i++)
        {
            result.put(aliases[i], tuple[i]);
        }
        return result;
    }
}
