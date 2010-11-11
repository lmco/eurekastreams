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
package org.eurekastreams.server.service.opensocial.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * This is a utility class to help support the OpenSocial implementation. 
 *
 */
public final class SPIUtils
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(SPIUtils.class);
    /**
     * Hiding the default constructor.
     */
    private SPIUtils()
    {
        //empty default constructor.
    }
    /**
     * This method retrieves a List Long of user id's from the Set of UserId objects
     * to use to query for Application Data.
     * @param userIds - input set of UserIds to decode.
     * @param token - security token for the current request.
     * @return - List of User Id's from inputed Set of UserId objects.
     */
    public static List<String> getUserList(final Set<UserId> userIds, final SecurityToken token) 
    {
      List<String> paramList = new ArrayList<String>();
      for (UserId u : userIds) 
      {
        try 
        {
          String uid = u.getUserId(token);
          if (uid != null) 
          {
            paramList.add(uid);
          }
        } 
        catch (IllegalStateException istate) 
        {
            log.info("Skipping userid");
        }
      }
      return paramList;
    }
    
    /**
    * Determine if a string id is an OpenSocial Id or not.
    * @param inId - string id to check if it is an OpenSocial id.
    * @return true if the passed in id is an OpenSocial Id.
    */
        public static boolean isOpenSocialId(final String inId)
        {
            /** Regex pattern for a UUID */
            Pattern uuidPattern =
                    Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
            return uuidPattern.matcher(inId).matches();
        }
}
