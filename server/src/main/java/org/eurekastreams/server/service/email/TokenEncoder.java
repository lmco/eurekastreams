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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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

    /** Mapper to get a person's encryption key. */
    private final DomainMapper<Long, byte[]> getPersonCryptoKeyDao;

    /**
     * Constructor.
     *
     * @param inAlgorithm
     *            Encryption algorithm to use.
     * @param inGetPersonCryptoKeyDao
     *            Mapper to get a person's encryption key.
     */
    public TokenEncoder(final String inAlgorithm, final DomainMapper<Long, byte[]> inGetPersonCryptoKeyDao)
    {
        algorithm = inAlgorithm;
        getPersonCryptoKeyDao = inGetPersonCryptoKeyDao;
    }

    /**
     * Encode a data string into a token.
     *
     * @param data
     *            Pre-formatted data.
     * @param personId
     *            ID of person whose key to encrypt token with.
     * @return Token.
     */
    public String encode(final String data, final long personId)
    {
        // get the person's key
        byte[] keyBytes = getPersonCryptoKeyDao.execute(personId);
        Key key = new SecretKeySpec(keyBytes, algorithm);

        // encrypt the data
        byte[] encrypted;
        try
        {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(data.getBytes());
        }
        catch (GeneralSecurityException ex)
        {
            log.error("Error encrypting data into token (data: '{}', person ID {})",
                    new Object[] { data, personId, ex });
            return null;
        }

        // encode the data (base64)
        String token = javax.xml.bind.DatatypeConverter.printBase64Binary(encrypted);
        return token;
    }

    /**
     * Encodes key-value data into a token.
     *
     * @param data
     *            Data.
     * @param personId
     *            ID of person whose key to encrypt token with.
     * @return Token.
     */
    public String encode(final Map<String, Long> data, final long personId)
    {
        // serialize the token data
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Long> entry : data.entrySet())
        {
            sb.append(entry.getKey());
            sb.append(entry.getValue());
        }

        return encode(sb.toString(), personId);
    }

    /**
     * Decodes a token into key-value data.
     *
     * @param token
     *            Token.
     * @param personId
     *            ID of person whose key to decrypt token with.
     * @return Data.
     */
    public Map<String, Long> decode(final String token, final long personId)
    {
        // decode the data (base64)
        byte[] encrypted = javax.xml.bind.DatatypeConverter.parseBase64Binary(token);
        if (encrypted.length == 0)
        {
            return null;
        }

        // get the person's key
        byte[] keyBytes = getPersonCryptoKeyDao.execute(personId);
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
            log.error("Error decrypting data from token (person ID {})", personId, ex);
            return null;
        }

        // parse the data from the string
        Map<String, Long> data = new HashMap<String, Long>();
        String toParse = new String(decrypted);
        int len = toParse.length();
        int pos = 0;
        while (pos < len)
        {
            int startTag = pos;
            while (pos < len && Character.isLetter(toParse.charAt(pos)))
            {
                pos++;
            }
            int startValue = pos;
            while (pos < len && Character.isDigit(toParse.charAt(pos)))
            {
                pos++;
            }
            if (startTag == startValue || startValue == pos)
            {
                log.error("Error parsing data from decrypted token - key or value empty.");
                return null;
            }
            String tag = toParse.substring(startTag, startValue);
            long value = Long.parseLong(toParse.substring(startValue, pos));
            data.put(tag, value);
        }

        return data;
    }
}
