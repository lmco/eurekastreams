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
package org.eurekastreams.server.action.validation;

/**
 * A Helper class for methods that are used to help validate inputs.
 * 
 */
public final class ValidationTestHelper
{

    /**
     * required private constructor.
     */
    private ValidationTestHelper()
    {
        // nothing to do here.
    }

    /**
     * helper method to generate a String of X length.
     * 
     * @param length
     *            length of the string to generate.
     * @return the string of X length.
     */
    public static String generateString(final long length)
    {
        // Uses a string buffer since a String would create param:length times new String objects when concatanating.
        StringBuffer genString = new StringBuffer();

        while (genString.length() < length)
        {
            genString.append("c");
        }

        return genString.toString();
    }
}
