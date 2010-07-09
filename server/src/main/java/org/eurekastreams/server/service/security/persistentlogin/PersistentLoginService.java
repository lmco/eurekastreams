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
package org.eurekastreams.server.service.security.persistentlogin;

import org.springframework.security.Authentication;
import org.springframework.security.ui.rememberme.InvalidCookieException;
import org.springframework.security.ui.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.eurekastreams.server.domain.PersistentLogin;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Persistent login service based on Spring's TokenBasedRememberMeServcies
 * class. This class overrides the parents processAutoLoginCookie,
 * onLoginSuccess, and logout methods to provide functionality required for
 * persistent login with LDAP authentication. NOTE: This class currently
 * requires a UserDetailsService implementation that returns object of type
 * ExtendedUserDetails, but that can be refactored out to a strategy if there is
 * ever another type of UserDetails service used with this system.
 */
public class PersistentLoginService extends TokenBasedRememberMeServices
{
    /**
     * Time before expiration.
     */
    private static final long EXPIRE_TIME = 1000L;
    
    /**
     * The PersistentTokenRepository used by this service.
     */
    private PersistentLoginRepository loginRepository = null;

    /**
     * Constructor.
     * 
     * @param key
     *            Key used in generating token value. THIS MUST MATCH THE SAME
     *            VALUE PASSED TO THE SPRING FILTER IN THE "remember-me" ELEMENT
     *            IN THE CONTEXT FILE.
     * @param userDetailsService
     *            The UserDetailsService this service should use.
     * @param persistentLoginRepository
     *            PersistentTokenRepository this object uses to store login
     *            information
     */
    public PersistentLoginService(final String key,
            final UserDetailsService userDetailsService,
            final PersistentLoginRepository persistentLoginRepository)
    {
        Assert.notNull(key);
        Assert.notNull(userDetailsService);
        Assert.notNull(persistentLoginRepository);
        this.setKey(key);
        this.setUserDetailsService(userDetailsService);
        this.loginRepository = persistentLoginRepository;
    }

    /**
     * This method overrides parents to allow persistent login using eurekastreams
     * UserDetailsService and LDAP authentication.
     * 
     * @param cookieTokens
     *            Provided by parent class, the decoded cookie information.
     * @param request
     *            The request object.
     * @param response
     *            The response object.
     * @return Populated UserDetails object. In this case it's an instance of
     *         ExtendedUserDetails.
     */
    public UserDetails processAutoLoginCookie(final String[] cookieTokens,
            final HttpServletRequest request, final HttpServletResponse response)
    {

        //This cookie validation code section is taken straight from 
        //Spring's TokenBasedRememberMeServices, no need to reinvent the wheel.
        if (cookieTokens.length != 3)
        {
            throw new InvalidCookieException("Cookie token did not contain "
                    + 2 + " tokens, but contained '"
                    + Arrays.asList(cookieTokens) + "'");
        }

        long tokenExpiryTime;

        try
        {
            tokenExpiryTime = new Long(cookieTokens[1]).longValue();
        } 
        catch (NumberFormatException nfe)
        {
            throw new InvalidCookieException(
                    "Cookie token[1] did not contain a valid number (contained '"
                            + cookieTokens[1] + "')");
        }

        if (isTokenExpired(tokenExpiryTime))
        {
            throw new InvalidCookieException(
                    "Cookie token[1] has expired (expired on '"
                            + new Date(tokenExpiryTime)
                            + "'; current time is '" + new Date() + "')");
        }

        // TODO make the following validation steps a cookie validation strategy
        // passing userDetails and cookieTokens
        // so this class doesn't have to know about ExtendedUserDetails
        // interface. Only needed if some other UserDetails service is created.
        
        // if not expired load user details
        ExtendedUserDetails userDetails = (ExtendedUserDetails) 
                (getUserDetailsService().loadUserByUsername(cookieTokens[0]));

        //if no persistentLogin info returned from UserDetailsService, abort
        //as cookie was misleading or manually invalidated.
        
        PersistentLogin login = userDetails.getPersistentLogin();
        
        if (login == null)
        {
            throw new InvalidCookieException(
                    "No PersistentLogin record in repository");
        }

        // Check signature of token matches remaining details.
        // Must do this after user lookup,
        String expectedTokenSignature = login.getTokenValue();
        long expectedExpiryDate = login.getTokenExpirationDate();

        if (tokenExpiryTime != expectedExpiryDate)
        {
            throw new InvalidCookieException(
                    "Cookie token[1] contained expirationDate '"
                            + cookieTokens[2] + "' but expected '"
                            + expectedExpiryDate + "'");

        }

        if (!expectedTokenSignature.equals(cookieTokens[2]))
        {
            throw new InvalidCookieException(
                    "Cookie token[2] contained signature '" + cookieTokens[2]
                            + "' but expected '" + expectedTokenSignature + "'");
        }

        return userDetails;
    }

    /**
     * Override parents version so we can generate our own custom token value
     * and store persistent login information in our repository.
     * @param request The request object.
     * @param response The response object.
     * @param successfulAuthentication The Authentication object for authenticated user.
     */
    public void onLoginSuccess(final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication successfulAuthentication)
    {

        String username = retrieveUserName(successfulAuthentication);

        // If unable to find a username, just abort as
        // TokenBasedRememberMeServices is
        // unable to construct a valid token in this case.
        if (!StringUtils.hasLength(username))
        {
            logger.error(
                "Authentication contains empty username. Unable to create persistent login info.");
            return;
        }

        int tokenLifetime = calculateLoginLifetime(request,
                successfulAuthentication);
        long expiryTime = System.currentTimeMillis() + EXPIRE_TIME * tokenLifetime;

        String signatureValue = makeTokenSignature(expiryTime, username,
                UUID.randomUUID().toString());

        // store it to repository, this is "non-essential" functionality so
        // swallow any
        // exceptions and log it. If for any reason this doesn't work, abort
        // without
        // exception and don't set cookie
        try
        {
            PersistentLogin login = new PersistentLogin(username, signatureValue, expiryTime);
            loginRepository.createOrUpdatePersistentLogin(login);
        } 
        catch (Exception e)
        {
            logger.error("Unable to insert PersistentLogin information to DB for user: "
                            + username
                            + "', expiry: '"
                            + new Date(expiryTime)
                            + "'");
            return;
        }

        setCookie(new String[] { username, Long.toString(expiryTime),
                signatureValue }, tokenLifetime, request, response);

        if (logger.isDebugEnabled())
        {
            logger.debug("Added remember-me cookie for user '" + username
                    + "', expiry: '" + new Date(expiryTime) + "'");
        }
    }

    /**
     * Override parent logout method so we can remove the persistent login info
     * from the repository (and cancel cookie).
     * 
     * @param request
     *            The request object.
     * @param response
     *            The response object.
     * @param authentication
     *            The Authentication object.
     */
    public void logout(final HttpServletRequest request,
            final HttpServletResponse response, final Authentication authentication)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Logout of user "
                    + (authentication == null ? "Unknown" : authentication.getName()));
        }
        cancelCookie(request, response);
        loginRepository.removePersistentLogin(((UserDetails) authentication
                .getPrincipal()).getUsername());
    }
}
