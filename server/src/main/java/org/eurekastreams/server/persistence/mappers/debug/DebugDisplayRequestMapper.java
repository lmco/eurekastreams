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
package org.eurekastreams.server.persistence.mappers.debug;

import java.io.PrintWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Debugging mapper that dumps the request to standard output.
 * 
 * @param <TRequestType>
 *            Request type.
 * @param <TReturnType>
 *            Return type.
 */
public class DebugDisplayRequestMapper<TRequestType, TReturnType> implements DomainMapper<TRequestType, TReturnType>
{
    /** JSON Factory for building JSON Generators. */
    private final JsonFactory jsonFactory;

    /**
     * Constructor.
     *
     * @param inJsonFactory
     *            JSON Factory for building JSON Generators.
     */
    public DebugDisplayRequestMapper(final JsonFactory inJsonFactory)
    {
        jsonFactory = inJsonFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TReturnType execute(final TRequestType inRequest)
    {
        if (inRequest == null)
        {
            System.out.println("DebugDisplayRequestMapper:  null request");
            return null;
        }

        System.out.println("DebugDisplayRequestMapper:  " + inRequest.getClass().getName());

        PrintWriter writer = new PrintWriter(System.out);

        try
        {
            JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(writer);
            jsonGenerator.writeObject(inRequest);
        }
        catch (Exception ex)
        {
            System.out.println("Error converting request to JSON");
            ex.printStackTrace();
        }
        writer.flush();
        System.out.println();

        return null;
    }

}
