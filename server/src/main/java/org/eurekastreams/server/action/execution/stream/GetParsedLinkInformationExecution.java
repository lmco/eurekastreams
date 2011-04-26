/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.server.persistence.mappers.FindLinkInformationByUrl;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.requests.UniqueStringRequest;
import org.eurekastreams.server.service.actions.strategies.links.ConnectionFacade;
import org.eurekastreams.server.service.actions.strategies.links.HtmlLinkParser;

/**
 * Retrieve the {@link LinkInformation} for a given url.
 * 
 */
public class GetParsedLinkInformationExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * List of parsing strategies.
     */
    List<HtmlLinkParser> parsingStrategies = null;

    /**
     * The HTTP connection facade.
     */
    private final ConnectionFacade connection;

    /**
     * Link mapper.
     */
    private FindLinkInformationByUrl mapper = null;

    /**
     * Insert mapper.
     */
    private InsertMapper<LinkInformation> insertMapper = null;

    /**
     * Constructor.
     * 
     * @param inConnection
     *            the file downloader.
     * @param inMapper
     *            the link URL mapper.
     * @param inInsertMapper
     *            the insert mapper.
     * @param inParsingStrategies
     *            the parsing strategies.
     */
    public GetParsedLinkInformationExecution(final ConnectionFacade inConnection,
            final FindLinkInformationByUrl inMapper, final InsertMapper<LinkInformation> inInsertMapper,
            final List<HtmlLinkParser> inParsingStrategies)
    {
        connection = inConnection;
        mapper = inMapper;
        insertMapper = inInsertMapper;
        parsingStrategies = inParsingStrategies;
    }

    /**
     * {@inheritDoc}
     * 
     * Retrieve the {@link LinkInformation} associated with the provided url.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String url = (String) inActionContext.getParams();

        LinkInformation theLink = null;

        try
        {
            // First see if the user supplied a url with a protocol. If they didn't
            // prepend http:// onto it.
            if (!Pattern.matches("^([a-z]+://.+)", url))
            {
                url = "http://" + url;
            }
            url = connection.getFinalUrl(url, inActionContext.getPrincipal().getAccountId());

            UniqueStringRequest req = new UniqueStringRequest(url);
            theLink = mapper.execute(req);

            if (null == theLink)
            {
                theLink = new LinkInformation();
                theLink.setUrl(url);
                // set the source to the protocol + authority
                // (Take everything up to the first slash beyond the protocol-authority separator ://)
                int postAuthorityIndex = url.indexOf("/", url.indexOf("://") + "://".length());
                theLink.setSource(postAuthorityIndex == -1 ? url : url.substring(0, postAuthorityIndex));

                // Attempt to retrieve the contents of the resource.
                log.debug("Downloading resource: " + url);
                try
                {
                    String htmlString = connection.downloadFile(url, inActionContext.getPrincipal().getAccountId());
                    htmlString = htmlString.replace("\\s+", " ");

                    String host = connection.getHost(url);

                    for (HtmlLinkParser strategy : parsingStrategies)
                    {
                        Matcher match = Pattern.compile(strategy.getRegex()).matcher(host);

                        if (match.find())
                        {
                            log.debug("Found: " + strategy.getRegex());
                            strategy.parseLinkInformation(htmlString, theLink, inActionContext.getPrincipal()
                                    .getAccountId());
                            break;
                        }
                        else
                        {
                            log.debug("Didn't find: " + strategy.getRegex());
                        }
                    }
                }
                catch (Exception e)
                {
                    log.info("Failed to download resource and extract link information from it.", e);
                }

                theLink.setCreated(new Date());
                insertMapper.execute(new PersistenceRequest<LinkInformation>(theLink));
                insertMapper.flush();
            }
        }
        catch (Exception ex)
        {
            // no reason to tell the user, s/he wont' care.
            int dontCare = 0;
        }
        return theLink;
    }
}
