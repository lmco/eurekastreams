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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.DisabledException;

/**
 * Test for PreAuthenticationFilterEntryPoint class.
 *
 */
public class PreAuthenticationFilterEntryPointTest
{    
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
     * HttpServletRequest mock.
     */
    private final HttpServletRequest inRequest = context.mock(HttpServletRequest.class);
    
    /**
     * HttpServletResponse mock.
     */
    private final HttpServletResponse inResponse = context.mock(HttpServletResponse.class);
    
    /**
     * AuthenticationException mock.
     */
    private final AuthenticationException inAuthenticationException = new BadCredentialsException("badCredEx");
    
    /**
     * DisabledException mock.
     */
    private final DisabledException disabledException = new DisabledException("Disabled");
    
    /**
     * Test commence with non-mapped exception.
     * @throws IOException on io error.
     * @throws ServletException on servlet error
     */
    @Test
    public void testCommenceAuthException() throws IOException, ServletException
    {
        Properties exceptionMappings = new Properties();
        exceptionMappings.setProperty("org.springframework.security.DisabledException", "destinationUrl");
        
        PreAuthenticationFilterEntryPoint sut = new PreAuthenticationFilterEntryPoint();
        sut.setExceptionMappings(exceptionMappings);
        sut.setOrder(1);
        sut.setUseRelativeContext(false);
        
        
        context.checking(new Expectations()
        {
                {                   
                   oneOf(inResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied:badCredEx");
                }
        });
        
        sut.commence(inRequest, inResponse, inAuthenticationException);
        assertEquals(1, sut.getOrder());
        context.assertIsSatisfied();
        
        
    }
    
    /**
     * Test commence with mapped exception and redirect.
     * @throws IOException on io error.
     * @throws ServletException on servlet error
     */
    @Test
    public void testCommenceDisabledExceptionWithRedirect() throws IOException, ServletException
    {
        Properties exceptionMappings = new Properties();
        exceptionMappings.setProperty("org.springframework.security.DisabledException", "/destinationUrl");
        
        PreAuthenticationFilterEntryPoint sut = new PreAuthenticationFilterEntryPoint();
        sut.setExceptionMappings(exceptionMappings);
        
        context.checking(new Expectations()
        {
                {                  
                   oneOf(inRequest).getContextPath();
                   will(returnValue("/contextPath"));
                   
                   oneOf(inResponse).encodeRedirectURL("/contextPath/destinationUrl");
                   will(returnValue("/contextPath/destinationUrl"));
                   
                   oneOf(inResponse).sendRedirect("/contextPath/destinationUrl");                   
                }
        });
        
        sut.commence(inRequest, inResponse, disabledException);
        context.assertIsSatisfied();                
    }
    
    /**
     * Test commence with mapped exception and server redirect.
     * @throws IOException on io error.
     * @throws ServletException on servlet error
     */
    @Test
    public void testCommenceDisabledExceptionWithServerRedirect() throws IOException, ServletException
    {
        final RequestDispatcher rd = context.mock(RequestDispatcher.class);
        Properties exceptionMappings = new Properties();
        exceptionMappings.setProperty("org.springframework.security.DisabledException", "/destinationUrl");
        
        PreAuthenticationFilterEntryPoint sut = new PreAuthenticationFilterEntryPoint();
        sut.setExceptionMappings(exceptionMappings);
        sut.setServerSideRedirect(true);
        
        context.checking(new Expectations()
        {
                {                  
                    oneOf(inRequest).getRequestDispatcher("/destinationUrl");
                    will(returnValue(rd));
                    
                    oneOf(rd).forward(inRequest, inResponse);
                }
        });
        
        sut.commence(inRequest, inResponse, disabledException);
        context.assertIsSatisfied();                
    }

}

