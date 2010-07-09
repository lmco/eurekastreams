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
package org.eurekastreams.commons.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Explanation;
import org.eurekastreams.commons.reflection.ReflectiveInstantiator;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.hibernate.transform.ResultTransformer;

/**
 * Transformer to convert object/alias arrays to ModelViews.
 */
public class ProjectionToModelViewTransformer implements ResultTransformer
{
    /**
     * The property name that Hibernate uses to store the Hibernate Class of the entity.
     */
    private static final String HIBERNATE_CLASS_PROPERTY_NAME = "_hibernate_class";

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ProjectionToModelViewTransformer.class);

    /**
     * UID.
     */
    private static final long serialVersionUID = 4477869354898040873L;

    /**
     * ReflectiveInstantiator to instantiate ModelViews.
     */
    private ReflectiveInstantiator reflectiveInstantiator;

    /**
     * The mapping of DomainEntity to ModelView.
     */
    private Map<Class< ? >, Class< ? >> modelToViewClassMap;

    /**
     * Constructor taking the DomainEntity -> ModelView mapping map. When results are returned from Lucene of a
     * DomainEntity type, the map contains the type of object to transform to.
     *
     * @param theModelToViewMap
     *            the mapping of DomainEntity to ModelView
     * @param inReflectiveInstantiator
     *            the reflection instantiator to use to instantiate the ModelViews
     *
     */
    public ProjectionToModelViewTransformer(final Map<Class< ? >, Class< ? >> theModelToViewMap,
            final ReflectiveInstantiator inReflectiveInstantiator)
    {
        modelToViewClassMap = theModelToViewMap;
        reflectiveInstantiator = inReflectiveInstantiator;
    }

    /**
     * Required override - used in case of special handling on lists such as removing duplicates, etc.
     *
     * @param list
     *            the list to transform
     * @return the passed-in list
     */
    @SuppressWarnings("unchecked")
    @Override
    public List transformList(final List list)
    {
        return list;
    }

    /**
     * Transform the array of objects and column aliases to a ModelView using reflection and the ModelView's ability to
     * import a property Map.
     *
     * @param tuple
     *            array of properties
     * @param aliases
     *            array of Strings representing the property aliases in tuple
     *
     * @return a ModelView representation of the input property/alias arrays
     */
    @Override
    public Object transformTuple(final Object[] tuple, final String[] aliases)
    {
        Map<String, Object> properties = getMapFromTuplesAndAliases(tuple, aliases);

        // find out what the entity class is
        Class< ? > entityClass = (Class< ? >) properties.get(HIBERNATE_CLASS_PROPERTY_NAME);

        // get the appropriate model view class from the lookup map
        if (!modelToViewClassMap.containsKey(entityClass))
        {
            String message = "Unhandled entity class: " + entityClass.getName()
                    + ", cannot determine which ModelView subclass to instantiate.";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        Class< ? > modelClass = modelToViewClassMap.get(entityClass);

        // instantiate, load the properties, and return the ModelView
        ModelView modelView = (ModelView) reflectiveInstantiator.instantiateObject(modelClass);
        modelView.loadProperties(properties);

        // If the ComplexExplanation is included, populate the ModelView with
        // it. These properties can't be imported by the ModelView because that
        // class needs to only deal with simple objects that GWT can support -
        // ComplexExplanation not being one of them.
        if (properties.containsKey("__HSearch_Explanation"))
        {
            Explanation explanation = (Explanation) properties.get("__HSearch_Explanation");
            modelView.setSearchIndexExplanationString(explanation.toString());
        }

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
    protected Map<String, Object> getMapFromTuplesAndAliases(final Object[] tuple, final String[] aliases)
    {
        Map<String, Object> result = new HashMap<String, Object>(tuple.length);
        for (int i = 0; i < tuple.length; i++)
        {
            result.put(aliases[i], tuple[i]);
        }
        return result;
    }
}
