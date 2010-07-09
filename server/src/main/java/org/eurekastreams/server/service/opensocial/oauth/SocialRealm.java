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
package org.eurekastreams.server.service.opensocial.oauth;

import org.jsecurity.authc.AuthenticationInfo;
import org.jsecurity.authc.AuthenticationToken;
import org.jsecurity.authz.AuthorizationInfo;
import org.jsecurity.realm.AuthorizingRealm;
import org.jsecurity.subject.PrincipalCollection;

/**
 * This class is a stub of the SocialRealm that needs to be implemented
 * for OAuth support.
 *
 */
public class SocialRealm extends AuthorizingRealm
{

    /**
     * This method allows the system to retrieve Authorization Info.
     * @param arg0 - PrincipalCollection.
     * @return AuthorizationInfo retrieved.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection arg0)
    {        
        return null;
    }

    /**
     * This metho is a stub to retrieve AuthorizationInfo.
     * @param arg0 - Authentication Token.
     * @return AuthenticationInfo retrieved.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            final AuthenticationToken arg0)
    {
        return null;
    }    
}
