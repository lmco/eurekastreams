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
package org.eurekastreams.server.service.opensocial.gadgets.http;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.common.util.Utf8UrlCoder;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.http.HttpCache;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.HttpResponseMetadataHelper;
import org.apache.shindig.gadgets.http.InvalidationService;
import org.apache.shindig.gadgets.http.RequestPipeline;
import org.apache.shindig.gadgets.oauth.OAuthRequest;
import org.apache.shindig.gadgets.rewrite.ResponseRewriterRegistry;
import org.apache.shindig.gadgets.rewrite.RewritingException;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;
import com.google.inject.name.Named;

/**
 * This class provides a custom Request Pipeline for Shindig that incorporates 
 * adding headers to the oauth requests for passing additional information about
 * the user making the request.
 * 
 * This class is based on the DefaultRequestPipeline within shindig.
 * 
 */
@Singleton
public class GadgetRequestPipeline implements RequestPipeline
{
    /**
     * Local instance of the HttpFetcher.
     */
    private final HttpFetcher httpFetcher;
    
    /**
     * Local instance of the HttpCache.
     */
    private final HttpCache httpCache;
    
    /**
     * Local instance of the {@link OAuthRequest} provider.
     */
    private final Provider<OAuthRequest> oauthRequestProvider;
    
    /**
     * Local instance of the {@link ResponseRewriterRegistry}.
     */
    private final ResponseRewriterRegistry responseRewriterRegistry;
    
    /**
     * Local instance of the {@link InvalidationService}.
     */
    private final InvalidationService invalidationService;
    
    /**
     * Local instance of the {@link HttpResponseMetadataHelper}.
     */
    private final HttpResponseMetadataHelper metadataHelper;
    
    /**
     * Local instance of the {@link PrincipalPopulatorTransWrapper}.
     */
    private final PrincipalPopulatorTransWrapper principalPopulator;

    /**
     * Constructor.
     * 
     * @param inHttpFetcher
     *            - instance of the HttpFetcher class that will fetch a response based on the request passed in.
     * @param inHttpCache
     *            - Cache implementation for responses.
     * @param inOauthRequestProvider
     *            - provider that handles OAuth requests.
     * @param inResponseRewriterRegistry
     *            - rewriter registry.
     * @param inInvalidationService
     *            - strategy for invalidating cache.
     * @param inMetadataHelper
     *            - class that helps attach metadata to the response.
     * @param inPrincipalPopulator
     *            - strategy for transforming the supplied opensocial id into a valie EurekaStreams {@link Principal}
     *            object.
     */
    @Inject
    public GadgetRequestPipeline(final HttpFetcher inHttpFetcher, final HttpCache inHttpCache,
            final Provider<OAuthRequest> inOauthRequestProvider,
            @Named("shindig.rewriters.response.pre-cache") final ResponseRewriterRegistry inResponseRewriterRegistry,
            final InvalidationService inInvalidationService, 
            @Nullable final HttpResponseMetadataHelper inMetadataHelper,
            final PrincipalPopulatorTransWrapper inPrincipalPopulator)
    {
        this.httpFetcher = inHttpFetcher;
        this.httpCache = inHttpCache;
        this.oauthRequestProvider = inOauthRequestProvider;
        this.responseRewriterRegistry = inResponseRewriterRegistry;
        this.invalidationService = inInvalidationService;
        this.metadataHelper = inMetadataHelper;
        this.principalPopulator = inPrincipalPopulator;
    }

    /**
     * {@inheritDoc}. 
     */
    public HttpResponse execute(final HttpRequest request) throws GadgetException
    {
        normalizeProtocol(request);
        HttpResponse invalidatedResponse = null;
        HttpResponse staleResponse = null;

        if (!request.getIgnoreCache())
        {
            HttpResponse cachedResponse = httpCache.getResponse(request);
            // Note that we don't remove invalidated entries from the cache as we want them to be
            // available in the event of a backend fetch failure
            if (cachedResponse != null)
            {
                if (!cachedResponse.isStale())
                {
                    if (invalidationService.isValid(request, cachedResponse))
                    {
                        return cachedResponse;
                    }
                    else
                    {
                        invalidatedResponse = cachedResponse;
                    }
                }
                else
                {
                    if (!cachedResponse.isError())
                    {
                        // Remember good but stale cached response, to be served if server unavailable
                        staleResponse = cachedResponse;
                    }
                }
            }
        }

        HttpResponse fetchedResponse = null;
        switch (request.getAuthType())
        {
        case NONE:
            fetchedResponse = httpFetcher.fetch(request);
            break;
        case SIGNED:
        case OAUTH:
            try
            {
                Principal currentUser = principalPopulator.getPrincipal(request.getSecurityToken().getOwnerId());
                //Remove headers that might have been set in gadget for these reserved words.
                if(request.getHeaders() != null)
                {
                    if(request.getHeaders().containsKey("accountid"))
                    {
                        request.getHeaders().remove("accountid");
                    }
                    if(request.getHeaders().containsKey("moduleid"))
                    {
                        request.getHeaders().remove("moduleid");
                    }
                }
                request.addHeader("accountid", currentUser.getAccountId());
                request.addHeader("moduleid", Long.toString(request.getSecurityToken().getModuleId()));
                fetchedResponse = oauthRequestProvider.get().fetch(request);
                break;
            }
            catch (Exception ex)
            {
                throw new GadgetException(GadgetException.Code.FAILED_TO_RETRIEVE_CONTENT, ex);
            }
        default:
            return HttpResponse.error();
        }

        if (fetchedResponse.isError() && invalidatedResponse != null)
        {
            // Use the invalidated cached response if it is not stale. We don't update its
            // mark so it remains invalidated
            return invalidatedResponse;
        }

        if (fetchedResponse.getHttpStatusCode() >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                && staleResponse != null)
        {
            // If we have trouble accessing the remote server,
            // Lets try the latest good but staled result
            return staleResponse;
        }

        if (!fetchedResponse.isError() && !request.getIgnoreCache() && request.getCacheTtl() != 0)
        {
            try
            {
                fetchedResponse = responseRewriterRegistry.rewriteHttpResponse(request, fetchedResponse);
            }
            catch (RewritingException e)
            {
                throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e, e.getHttpStatusCode());
            }
        }

        // Set response hash value in metadata (used for url versioning)
        fetchedResponse = HttpResponseMetadataHelper.updateHash(fetchedResponse, metadataHelper);
        if (!request.getIgnoreCache())
        {
            // Mark the response with invalidation information prior to caching
            if (fetchedResponse.getCacheTtl() > 0)
            {
                fetchedResponse = invalidationService.markResponse(request, fetchedResponse);
            }
            httpCache.addResponse(request, fetchedResponse);
        }
        return fetchedResponse;
    }

    /**
     * Determine if the protocol provided for the request is within the bounds of this pipeline (http or https).
     * 
     * @param request
     *            - request to be checked.
     * @throws GadgetException
     *             - on error.
     */
    protected void normalizeProtocol(final HttpRequest request) throws GadgetException
    {
        // Normalize the protocol part of the URI
        if (request.getUri().getScheme() == null)
        {
            throw new GadgetException(GadgetException.Code.INVALID_PARAMETER, "Url " + request.getUri().toString()
                    + " does not include scheme", HttpResponse.SC_BAD_REQUEST);
        }
        else if (!"http".equals(request.getUri().getScheme()) && !"https".equals(request.getUri().getScheme()))
        {
            throw new GadgetException(GadgetException.Code.INVALID_PARAMETER, "Invalid request url scheme in url: "
                    + Utf8UrlCoder.encode(request.getUri().toString()) + "; only \"http\" and \"https\" supported.",
                    HttpResponse.SC_BAD_REQUEST);
        }
    }
}
