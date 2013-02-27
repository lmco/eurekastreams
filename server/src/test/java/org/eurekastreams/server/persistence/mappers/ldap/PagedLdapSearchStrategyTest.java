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
package org.eurekastreams.server.persistence.mappers.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.eurekastreams.server.domain.Person;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Test suite for the {@link PagedLdapSearchStrategy} class.
 * 
 */
public class PagedLdapSearchStrategyTest
{
    /**
     * System under test.
     */
    private PagedLdapSearchStrategy sut;

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked instance of {@link LdapTemplate} for this test suite.
     */
    private LdapTemplate ldapTemplateMock = context.mock(LdapTemplate.class);

    /**
     * Mocked instance of {@link LdapContextSource} for this test suite.
     */
    private LdapContextSource ldapContextSourceMock = context.mock(LdapContextSource.class);
    
    /**
     * Mocked instnace of {@link SearchControls} for this test suite.
     */
    private SearchControls searchControlsMock = context.mock(SearchControls.class);

    /**
     * Mocked instance of {@link CollectingNameClassPairCallbackHandler} for this test suite.
     */
    private CollectingNameClassPairCallbackHandler handlerMock = context
            .mock(CollectingNameClassPairCallbackHandler.class);

    /**
     * ContextSource urls.
     */
    private String[] urls = new String[] { "url" };
    
    /**
     * Test the sut without specifying the page size which is intended to test the default.
     */
    @Test
    public void testSearchWithDefaultPageSize()
    {
        sut = new PagedLdapSearchStrategy();

        final List<Person> testPersonList = new ArrayList<Person>();
        testPersonList.add(new Person());
        testPersonList.add(new Person());

        context.checking(new Expectations()
        {
            {
            	allowing(ldapTemplateMock).getContextSource();
            	will(returnValue(ldapContextSourceMock));
            	
            	allowing(ldapContextSourceMock).getUrls();
            	will(returnValue(urls));
            	
            	allowing(ldapContextSourceMock).getBaseLdapPathAsString();
            	will(returnValue("dc=blah"));
            	
                oneOf(ldapTemplateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(CollectingNameClassPairCallbackHandler.class)),
                        with(any(PagedResultsDirContextProcessor.class)));

                oneOf(handlerMock).getList();
                will(returnValue(testPersonList));
            }
        });
        sut.searchLdap(ldapTemplateMock, "cn=groupname", searchControlsMock, handlerMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the sut with specifying the page size. This test is a duplicate of the previous test, just using the
     * separate constructor. This test doesn't verify that the paging works because it would need to mock out the ldap
     * searcher, which requires additional effort than can be covered in this defect. Leaving a todo for expanding this
     * test.
     * 
     * TODO:expand this test to mock the ldap searcher for testing the paging.
     */
    @Test
    public void testSearchWithCustomPageSize()
    {
        sut = new PagedLdapSearchStrategy(2);

        final List<Person> testPersonList = new ArrayList<Person>();
        testPersonList.add(new Person());
        testPersonList.add(new Person());

        context.checking(new Expectations()
        {
            {
            	allowing(ldapTemplateMock).getContextSource();
            	will(returnValue(ldapContextSourceMock));
            	
            	allowing(ldapContextSourceMock).getUrls();
            	will(returnValue(urls));
            	
            	allowing(ldapContextSourceMock).getBaseLdapPathAsString();
            	will(returnValue("dc=blah"));
            	
                oneOf(ldapTemplateMock).search(with(any(String.class)), with(any(String.class)),
                        with(any(SearchControls.class)), with(any(CollectingNameClassPairCallbackHandler.class)),
                        with(any(PagedResultsDirContextProcessor.class)));

                oneOf(handlerMock).getList();
                will(returnValue(testPersonList));
            }
        });
        sut.searchLdap(ldapTemplateMock, "cn=groupname", searchControlsMock, handlerMock);

        context.assertIsSatisfied();
    }
}
