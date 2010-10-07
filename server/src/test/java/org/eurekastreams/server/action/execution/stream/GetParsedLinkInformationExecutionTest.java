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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.server.persistence.mappers.FindLinkInformationByUrl;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.requests.UniqueStringRequest;
import org.eurekastreams.server.service.actions.strategies.links.ConnectionFacade;
import org.eurekastreams.server.service.actions.strategies.links.HtmlLinkInformationParserStrategy;
import org.eurekastreams.server.service.actions.strategies.links.HtmlLinkParser;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetParsedLinkInformationExecution} class.
 * 
 */
public class GetParsedLinkInformationExecutionTest
{
    /**
     * System under test.
     */
    private GetParsedLinkInformationExecution sut;

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
     * Mock file downloader.
     */
    private ConnectionFacade fileDownloader = context.mock(ConnectionFacade.class);

    /**
     * Mock mapper.
     */
    private FindLinkInformationByUrl mapperMock = context.mock(FindLinkInformationByUrl.class);

    /**
     * Insert mapper.
     */
    private InsertMapper<LinkInformation> insertMapper = context.mock(InsertMapper.class);

    /**
     * Strategy list.
     */
    private List<HtmlLinkParser> strategies = new ArrayList<HtmlLinkParser>();

    /**
     * Mock image parser.
     */
    private HtmlLinkInformationParserStrategy imgParser = context.mock(HtmlLinkInformationParserStrategy.class,
            "imgParser");

    /**
     * Mock description parser.
     */
    private HtmlLinkInformationParserStrategy descParser = context.mock(HtmlLinkInformationParserStrategy.class,
            "descParser");

    /**
     * Mock title parser.
     */
    private HtmlLinkInformationParserStrategy titleParser = context.mock(HtmlLinkInformationParserStrategy.class,
            "titleParser");

    /**
     * Mocked instance of principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * TEst account.
     */
    private static final String TEST_ACCOUNT = "testaccount";

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        HtmlLinkParser parser = new HtmlLinkParser();
        parser.setDescriptionParser(descParser);
        parser.setImageParser(imgParser);
        parser.setTitleParser(titleParser);
        parser.setRegex(".*");

        strategies.add(parser);
        sut = new GetParsedLinkInformationExecution(fileDownloader, mapperMock, insertMapper, strategies);
    }

    /**
     * Perform action test.
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performActionDefaultStrategyTest() throws Exception
    {
        final String theHtml = "<html><title>Some Title</title><body>text text text</html>";

        final String theUrl = "http://www.someurl.com/someFile.html";

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));
                
                oneOf(fileDownloader).downloadFile(theUrl, TEST_ACCOUNT);
                will(returnValue(theHtml));

                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));
                
                oneOf(fileDownloader).getFinalUrl(theUrl, TEST_ACCOUNT);
                will(returnValue(theUrl));

                oneOf(principalMock).getAccountId();
                
                oneOf(titleParser).parseInformation(with(equal(theHtml)), with(any(LinkInformation.class)),
                        with(any(String.class)));
                
                oneOf(descParser).parseInformation(with(equal(theHtml)), with(any(LinkInformation.class)),
                        with(any(String.class)));
                
                oneOf(imgParser).parseInformation(with(equal(theHtml)), with(any(LinkInformation.class)),
                        with(any(String.class)));

                oneOf(mapperMock).execute(with(any(UniqueStringRequest.class)));
                will(returnValue(null));

                oneOf(insertMapper).execute(with(any(PersistenceRequest.class)));

                oneOf(insertMapper).flush();

                oneOf(fileDownloader).getHost(theUrl);
                will(returnValue("www.someurl.com/"));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(theUrl, principalMock);

        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Perform action test.
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performActionDefaultStrategyTestWithBadLink() throws Exception
    {
        final String fixedUrl = "http://someurl.com";

        final String theHtml = "<html><title>Some Title</title><body>text text text</html>";

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));
                
                oneOf(fileDownloader).downloadFile(fixedUrl, TEST_ACCOUNT);
                will(returnValue(theHtml));

                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));
                
                oneOf(fileDownloader).getFinalUrl(fixedUrl, TEST_ACCOUNT);
                will(returnValue(fixedUrl));

                oneOf(principalMock).getAccountId();
                
                oneOf(titleParser).parseInformation(with(equal(theHtml)), with(any(LinkInformation.class)),
                        with(any(String.class)));
                
                
                oneOf(descParser).parseInformation(with(equal(theHtml)), with(any(LinkInformation.class)),
                        with(any(String.class)));
                
                
                oneOf(imgParser).parseInformation(with(equal(theHtml)), with(any(LinkInformation.class)),
                        with(any(String.class)));

                
                oneOf(mapperMock).execute(with(any(UniqueStringRequest.class)));
                will(returnValue(null));

                oneOf(insertMapper).execute(with(any(PersistenceRequest.class)));

                oneOf(insertMapper).flush();

                oneOf(fileDownloader).getHost(fixedUrl);
                will(throwException(new MalformedURLException()));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext("someurl.com", principalMock);

        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Perform action test when the link is cached.
     * 
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void performActionLinkCachedTest() throws Exception
    {
        final Serializable[] params = new Serializable[1];
        params[0] = "http://www.youtube.com/someFile.html";

        final LinkInformation link = new LinkInformation();

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue(TEST_ACCOUNT));
                
                oneOf(fileDownloader).getFinalUrl((String) params[0], TEST_ACCOUNT);
                will(returnValue(params[0]));

                oneOf(mapperMock).execute(with(any(UniqueStringRequest.class)));
                will(returnValue(link));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext("http://www.youtube.com/someFile.html",
                principalMock);

        Assert.assertEquals(link, sut.execute(currentContext));

        context.assertIsSatisfied();
    }
}
