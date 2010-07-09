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
package org.eurekastreams.server.service.actions.strategies;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generates a unique hash for every input using the magic of MD5 and a random
 * number seed.
 * 
 */
public class HashGeneratorStrategy
{
    /**
     * Logggger.
     */
    private Log log = LogFactory.getLog(HashGeneratorStrategy.class);

    /**
     * Has to be here for magic number reasons. Bleh.
     */
    private static final int TWO_FIFTY_FIVE =  0xFF;
    
    /**
     * Hashs an input.
     * 
     * @param inInput
     *            the input string.
     * @return the output string.
     */
    public String hash(final String inInput)
    {
        Random generator = new Random();
        String input = inInput + Integer.toString(generator.nextInt());

        try
        {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] messageDigest = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                hexString.append(Integer.toHexString(TWO_FIFTY_FIVE & messageDigest[i]));
            }

            return hexString.toString();

        } 
        catch (NoSuchAlgorithmException e)
        {
            log.error("hash failed");
            return null;
        }
    }
}
