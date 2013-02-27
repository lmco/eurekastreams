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
package org.eurekastreams.server.persistence.mappers.ldap.templateretrievers;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Return LdapTemplate based on presented LdapGroup relative dn. This implementation only uses the template key from
 * {@link LdapLookupRequest} and ignores query string.
 * 
 */
public class LdapGroupDnLdapTemplateRetriever extends BaseLdapTemplateRetriever
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();
    
    /**
     * Constructor.
     * 
     * @param inLdapTemplates
     *            Map of ldap templates.
     * @param inDefaultLdapTemplate
     *            The default LdapTemplate to use.
     */
    public LdapGroupDnLdapTemplateRetriever(final HashMap<String, LdapTemplate> inLdapTemplates,
            final LdapTemplate inDefaultLdapTemplate)
    {
        super(inLdapTemplates, inDefaultLdapTemplate);
    }

    /**
     * Uses specified template key to return an LdapTemplate used to make query.
     * 
     * @param inLdapLookupRequest
     *            {@link LdapLookupRequest}.
     * @return LdapTemplate based on provided template key, or default template if none found.
     */
    @Override
    protected LdapTemplate retrieveLdapTemplate(final LdapLookupRequest inLdapLookupRequest)
    {
        String testString = StringUtils.lowerCase(inLdapLookupRequest.getTemplateKey());
        //If the templateKey is null, try the query string to determine the correct template
        //to use.
        if(testString == null)
        {
        	testString = StringUtils.lowerCase(inLdapLookupRequest.getQueryString());
        }

        if(log.isDebugEnabled())
		{
			log.debug("Retrieving the template based on the domain in the request template key: "
					+ testString);
		}
        
        if (testString != null && !testString.isEmpty())
        {
        	//Determine the very first DC in the CN to be used to find an appropriate ldap template.
        	int firstDcIndex = StringUtils.indexOf(testString, ",dc=");
        	if(firstDcIndex > -1 && firstDcIndex < testString.length())
        	{
	        	int endOfFirstDcIndex = StringUtils.indexOf(testString, ",dc=", firstDcIndex + 1);
	        	if(endOfFirstDcIndex < 0)
	        	{
	        		//If there is only one dc attribute, bound it with the end of the string.
	        		endOfFirstDcIndex = testString.length();
	        	}
	        	String dcSubstring = StringUtils.substring(testString, firstDcIndex, endOfFirstDcIndex);
	        	String[] dcParts = StringUtils.split(dcSubstring, "=");
	        	if(dcParts.length > 1)
	        	{
	                for (String key : getLdapTemplates().keySet())
	                {
	                    if (StringUtils.equalsIgnoreCase(key, dcParts[1]))
	                    {
	                    	if(log.isDebugEnabled())
	                		{
	                			log.debug("Matched ldapTemplateKey: " + dcParts[1]);
	                		}
	                        return getLdapTemplates().get(key);
	                    }
	                }
	        	}
        	}
        	else
        	{
        		if(log.isWarnEnabled())
        		{
        			log.warn("No dc was supplied in the Group DN, this may not be a valid search.");
        		}
        	}
        }

        if(log.isDebugEnabled())
		{
			log.debug("No matched ldapTemplates, returning default");
		}
     
        return getDefaultLdapTemplate();
    }
}
