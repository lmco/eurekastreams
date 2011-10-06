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
package org.eurekastreams.server.service.email;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.eurekastreams.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to encode and decode person-specific tokens (for use in the email interface).
 */
public class TokenEncoder
{
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** Encryption algorithm to use. */
    private final String algorithm;

    /** Regex for checking if a string might be a token. */
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^[A-Z0-9+/]+={0,2}$", Pattern.CASE_INSENSITIVE);

    /**
     * Constructor.
     *
     * @param inAlgorithm
     *            Encryption algorithm to use.
     */
    public TokenEncoder(final String inAlgorithm)
    {
        algorithm = inAlgorithm;
    }

    /**
     * Encode a data string into a token.
     * 
     * @param content
     *            Pre-formatted data.
     * @param keyBytes
     *            Key to encrypt token with.
     * @return Token.
     */
    public String encode(final String content, final byte[] keyBytes)
    {
        // get the person's key
        Key key = new SecretKeySpec(keyBytes, algorithm);

        // encrypt the data
        byte[] encrypted;
        try
        {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content.getBytes());
        }
        catch (GeneralSecurityException ex)
        {
            log.error("Error encrypting data into token (data: '{}')", content, ex);
            return null;
        }

        // encode the data (base64)
        String token = javax.xml.bind.DatatypeConverter.printBase64Binary(encrypted);
        return token;
    }

    /**
     * Determines if a string could represent a token.
     *
     * @param potentialToken
     *            Token string to check.
     * @return If the string may be a token.
     */
    public boolean couldBeToken(final String potentialToken)
    {
        return TOKEN_PATTERN.matcher(potentialToken).matches();
    }

    /**
     * Decodes a token.
     * 
     * @param token
     *            Token.
     * @param keyBytes
     *            Key to decrypt token with.
     * @return Content string.
     */
    public String decode(final String token, final byte[] keyBytes)
    {
        // decode the data (base64)
        byte[] encrypted = javax.xml.bind.DatatypeConverter.parseBase64Binary(token);
        if (encrypted.length == 0)
        {
            return null;
        }

        // get the person's key
        Key key = new SecretKeySpec(keyBytes, algorithm);

        // decrypt the data
        byte[] decrypted;
        try
        {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(encrypted);
        }
        catch (GeneralSecurityException ex)
        {
            log.error("Error decrypting data from token", ex);
            return null;
        }

        return new String(decrypted);
    }
}
