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
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to encode and decode person-specific tokens (for use in the email interface).
 */
public class TokenEncoder
{
    /** Metadata key: person performing the action. */
    public static final String META_KEY_ACTOR = "p";

    /** Metadata key: destination personal stream. */
    public static final String META_KEY_PERSON_STREAM = "ps";

    /** Metadata key: destination group stream. */
    public static final String META_KEY_GROUP_STREAM = "gs";

    /** Metadata key: activity ID. */
    public static final String META_KEY_ACTIVITY = "a";

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
     * Encodes a token for a user and stream.
     *
     * @param streamEntityType
     *            Entity type of the stream.
     * @param streamEntityId
     *            ID of stream's entity (person/group).
     * @param personId
     *            Person ID of actor.
     * @param keyBytes
     *            Key to encrypt token with.
     * @return Token.
     */
    public String encodeForStream(final EntityType streamEntityType, final long streamEntityId, final long personId,
            final byte[] keyBytes)
    {
        String metaKey;
        switch (streamEntityType)
        {
        case PERSON:
            metaKey = META_KEY_PERSON_STREAM;
            break;
        case GROUP:
            metaKey = META_KEY_GROUP_STREAM;
            break;
        default:
            throw new IllegalArgumentException("Only person and group streams are allowed.");
        }

        return encode(META_KEY_ACTOR + personId + metaKey + streamEntityId, keyBytes);
    }

    /**
     * Encodes a token for a user and activity.
     *
     * @param activityId
     *            Activity ID.
     * @param personId
     *            Person ID of actor.
     * @param keyBytes
     *            Key to encrypt token with.
     * @return Token.
     */
    public String encodeForActivity(final long activityId, final long personId, final byte[] keyBytes)
    {
        return encode(META_KEY_ACTOR + personId + META_KEY_ACTIVITY + activityId, keyBytes);
    }

    /**
     * Encode a data string into a token.
     *
     * @param data
     *            Pre-formatted data.
     * @param keyBytes
     *            Key to encrypt token with.
     * @return Token.
     */
    public String encode(final String data, final byte[] keyBytes)
    {
        // get the person's key
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
            log.error("Error encrypting data into token (data: '{}')", data, ex);
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
     * @param keyBytes
     *            Key to encrypt token with.
     * @return Token.
     */
    public String encode(final Map<String, Long> data, final byte[] keyBytes)
    {
        // serialize the token data
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Long> entry : data.entrySet())
        {
            sb.append(entry.getKey());
            sb.append(entry.getValue());
        }

        return encode(sb.toString(), keyBytes);
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
     * Decodes a token into key-value data.
     *
     * @param token
     *            Token.
     * @param keyBytes
     *            Key to decrypt token with.
     * @return Data.
     */
    public Map<String, Long> decode(final String token, final byte[] keyBytes)
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
