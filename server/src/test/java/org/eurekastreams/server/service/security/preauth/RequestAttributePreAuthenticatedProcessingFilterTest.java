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
package org.eurekastreams.server.service.security.preauth;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.preauth.PreAuthenticatedCredentialsNotFoundException;

/**
 * Tests for RequestAttributePreAutenticatedProcessingFilter class.
 *
 */
public class RequestAttributePreAuthenticatedProcessingFilterTest
{
	/**
	 * The attribute to look for in the Request object.
	 */
	private static final String PRINCIPALREQUESTATTRIBUTEDEFAULT = "REMOTE_USER";  //default request attribute name
	/**
	 * An attribute use to to set the value to the non-default.
	 */
	private static final String PRINCIPALREQUESTATTRIBUTENONDEFAULT = "NO_USER";   //non-default request attribute name
	/**
	 * A principal name to use.
	 */
	private static final String PRINCIPALNAME = "jsmith";  //a simple String principal name
	/**
	 * The default credentials response string.
	 */
	private static final String CREDENTIALSDEFAULT = "N/A";  //a simple String for credentials, default response
	/**
	 * The non default credentials string.
	 */
	private static final String CREDENTIALSNONDEFAULT = "xxx";  // a simple String for credentials, non-default response
	/**
	 * A request header value to use as the non-default.
	 */
    private static final String CREDENTIALSREQUESTHEADERNONDEFAULT = "CREDENTIALS"; //a non default header name
	
    /**
     * Context for building mock objects. 
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    }; 
    
    /**
     * Test to insure the filter chain order is pre-auth.
     */
    @Test
    public void testFilterChainOrder() 
    {
    	// The system under test
    	final RequestAttributePreAuthenticatedProcessingFilter sut = 
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	
    	assertEquals(FilterChainOrder.PRE_AUTH_FILTER, sut.getOrder());
    }    
    
    /**
     * Test to validate an error is thrown if the attribute is not in the Servlet context.
     * Using the default principal request attribute
     */
    @Test(expected = PreAuthenticatedCredentialsNotFoundException.class)
    public void testGetAttributeNotInContext()
    {           
    	final RequestAttributePreAuthenticatedProcessingFilter sut = 
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	final HttpServletRequest request = context.mock(HttpServletRequest.class);
    	context.checking(new Expectations()
    	{
    		{
    			oneOf(request).getAttribute(PRINCIPALREQUESTATTRIBUTEDEFAULT);
                will(returnValue(null));
    		}
    	});
    	
    	sut.getPreAuthenticatedPrincipal(request);
        context.assertIsSatisfied();
    }
    
    /**
     * Test to validate a user principal name is returned using the default principal request attribute.
     */
    public void testGetAttributeInContext() 
    {
    	final RequestAttributePreAuthenticatedProcessingFilter sut =
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	final HttpServletRequest request = context.mock(HttpServletRequest.class);
    	context.checking(new Expectations()
    	{
    		{
    			oneOf(request).getAttribute(PRINCIPALREQUESTATTRIBUTEDEFAULT);
    			will(returnValue(PRINCIPALNAME));
    		}
    	});
    	
    	// main method invocation
    	Object principal = sut.getPreAuthenticatedPrincipal(request);
    	
    	// assert the Object is a String
    	assertTrue(principal instanceof String);
    	// assert the Object is equal to the expected value
    	assertEquals(PRINCIPALNAME, (String) principal);
        context.assertIsSatisfied();
    }
    
    /**
     * Test to validate that upon changing the principal request attribute value.  A correct
     * principal name is returned.
     */
    @Test
    public void testSetPrincipalRequstAttributeReturnsPrincipalName()
    {
    	final RequestAttributePreAuthenticatedProcessingFilter sut = 
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	final HttpServletRequest request = context.mock(HttpServletRequest.class);
    	
    	context.checking(new Expectations()
    	{
    		{
    			oneOf(request).getAttribute(PRINCIPALREQUESTATTRIBUTENONDEFAULT);
    			will(returnValue(PRINCIPALNAME));
    		}
    	});

    	// method invocations
    	sut.setPrincipalRequestAttribute(PRINCIPALREQUESTATTRIBUTENONDEFAULT);
    	Object principal = sut.getPreAuthenticatedPrincipal(request);
    	
    	// assert the Object is a String
    	assertTrue(principal instanceof String);
    	// assert the Object is equal to the expected value
    	assertEquals(PRINCIPALNAME, (String) principal);  
        context.assertIsSatisfied();
    }
    
    /**
     * Test that a default value is returned for the credentials String.
     */
    @Test
    public void testDefaultCredentialStringIsReturned()
    {
    	final RequestAttributePreAuthenticatedProcessingFilter sut = 
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	final HttpServletRequest request = context.mock(HttpServletRequest.class);
    	
    	Object credentials = sut.getPreAuthenticatedCredentials(request);
    	// assert the Object is a String
    	assertTrue(credentials instanceof String);
    	// assert the Object is equal to the expected value
    	assertEquals(CREDENTIALSDEFAULT, (String) credentials);    	 
    }
    
    /**
     * Test to validate an error is thrown when the header value is not in the request context.
     */
    @Test(expected = PreAuthenticatedCredentialsNotFoundException.class)
    public void testGetHeaderNotInContext() 
    {
    	final RequestAttributePreAuthenticatedProcessingFilter sut = 
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	final HttpServletRequest request = context.mock(HttpServletRequest.class);
    	
    	context.checking(new Expectations()
    	{
    		{
    			oneOf(request).getHeader(CREDENTIALSREQUESTHEADERNONDEFAULT);
    			will(returnValue(null));
    		}
    	});
    	// need to set the credentials request header to a non-default value
    	sut.setCredentialsRequestHeader(CREDENTIALSREQUESTHEADERNONDEFAULT);
    	// main method invocation
    	sut.getPreAuthenticatedCredentials(request);
        context.assertIsSatisfied();
    }
    
    /**
     * Test to validate that upon changing the credentials request header value.  A correct
     * credentials string is returned
     */
    @Test
    public void testSetCredentialsRequstHeaderReturnsPrincipalName()
    {
    	final RequestAttributePreAuthenticatedProcessingFilter sut = 
    		new RequestAttributePreAuthenticatedProcessingFilter();
    	final HttpServletRequest request = context.mock(HttpServletRequest.class);
    	
    	context.checking(new Expectations()
    	{
    		{
    			oneOf(request).getHeader(CREDENTIALSREQUESTHEADERNONDEFAULT);
    			will(returnValue(CREDENTIALSNONDEFAULT));
    		}
    	});

    	// method invocations
    	sut.setCredentialsRequestHeader(CREDENTIALSREQUESTHEADERNONDEFAULT);
    	Object credentials = sut.getPreAuthenticatedCredentials(request);
    	
    	// assert the Object is a String
    	assertTrue(credentials instanceof String);
    	// assert the Object is equal to the expected value
    	assertEquals(CREDENTIALSNONDEFAULT, (String) credentials);  
        context.assertIsSatisfied();
    }    
}
