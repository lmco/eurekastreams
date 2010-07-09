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
package org.eurekastreams.server.action.execution.feed;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.feed.RefreshFeedRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.MemcachedCache;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.ObjectMapper;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.SpecificUrlObjectMapper;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.rome.ActivityStreamsModule;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.rome.ActivityStreamsModuleImpl;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.rome.FeedFactory;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.module.SyModule;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;

/**
 * Test for the RefreshFeedAction.
 * 
 */
public class RefreshFeedExecutionTest
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
     * Minutes in an hour.
     */
    private static final long MININHOUR = 60L;

    /**
     * Minutes in a day.
     */
    private static final long MININDAY = 1440L;

    /**
     * Minutes in a week.
     */
    private static final long MININWEEK = 10080L;

    /**
     * Minutes in a month.
     */
    private static final long MININMONTH = 44640L;

    /**
     * Minutes in a year.
     */
    private static final long MININYEAR = 525600L;

    /**
     * Standard feed mappers.
     */
    private HashMap<BaseObjectType, ObjectMapper> standardFeedMappers = new HashMap<BaseObjectType, ObjectMapper>();

    /**
     * Mappers for specific websites.
     */
    private List<SpecificUrlObjectMapper> specificUrlMappers = new LinkedList<SpecificUrlObjectMapper>();

    /**
     * Bulk insert activity into the DB.
     */
    private InsertMapper<Activity> activityDBInserter = context.mock(InsertMapper.class);

    /**
     * The cache.
     */
    private MemcachedCache cache = context.mock(MemcachedCache.class);

    /**
     * Feed mock.
     */
    private Feed feed = context.mock(Feed.class);
    /**
     * Request.
     */
    private RefreshFeedRequest request = context.mock(RefreshFeedRequest.class);
    /**
     * System under test.
     */
    private RefreshFeedExecution sut;

    /**
     * Context for tests.
     */
    private ActionContext ac = context.mock(ActionContext.class);

    /**
     * Feed factory mock.
     */
    private FeedFactory feedFetcherFactory = context.mock(FeedFactory.class);
    /**
     * Feed fetcher mock.
     */
    private FeedFetcher feedFetcher = context.mock(FeedFetcher.class);
    /**
     * Atom feed mock.
     */
    private SyndFeed atomFeed = context.mock(SyndFeed.class);
    /**
     * Symod mock.
     */
    private SyModuleImpl syMod = context.mock(SyModuleImpl.class);
    /**
     * Flickr mapper mock.
     */
    private SpecificUrlObjectMapper flickrMapper = context.mock(SpecificUrlObjectMapper.class, "flickr");
    /**
     * Youtube mapper mock.
     */
    private SpecificUrlObjectMapper youTubeMapper = context.mock(SpecificUrlObjectMapper.class, "youtube");
    /**
     * Notemapper mock.
     */
    private ObjectMapper noteMapper = context.mock(ObjectMapper.class, "note");
    /**
     * Bookmark mapper mock.
     */
    private ObjectMapper bookmarkMapper = context.mock(ObjectMapper.class, "bookmark");
    /**
     * List of entries.
     */
    private List<SyndEntryImpl> entryList = new LinkedList<SyndEntryImpl>();
    /**
     * List of people owners.
     */
    private List<Person> peopleOwners = new LinkedList<Person>();
    /**
     * List of group owners.
     */
    private List<DomainGroup> groupOwners = new LinkedList<DomainGroup>();
    /**
     * Plugin mock.
     */
    private PluginDefinition plugin = context.mock(PluginDefinition.class);

    /**
     * Find by id person mapper.
     */
    private FindByIdMapper<Person> personMapper = context.mock(FindByIdMapper.class);

    /**
     * Find by id group mapper.
     */
    private FindByIdMapper<DomainGroup> groupMapper = context.mock(FindByIdMapper.class, "gm");

    /**
     * Find by id feed mapper.
     */
    private FindByIdMapper<Feed> feedMapper = context.mock(FindByIdMapper.class, "fm");

    /**
     * Person owner.
     */
    private Person personOwner = context.mock(Person.class);
    /**
     * Group owner.
     */
    private DomainGroup groupOwner = context.mock(DomainGroup.class);

    /**
     * Group owner.
     */
    private UpdateMapper<Feed> updateMapper = context.mock(UpdateMapper.class);

    /**
     * Gadget metadata fetcher.
     */
    private GadgetMetaDataFetcher fetcher = context.mock(GadgetMetaDataFetcher.class);

    /**
     * Prep the system under test for the test suite.
     * 
     * @throws Exception
     *             exception.
     */
    @Before
    public void setUp() throws Exception
    {
        final List<GadgetMetaDataDTO> metaDataList = new ArrayList<GadgetMetaDataDTO>();
        metaDataList.add(new GadgetMetaDataDTO(null));

        FeedSubscriber sub1 = new FeedSubscriber();
        sub1.setEntityId(1L);
        sub1.setEntityType(EntityType.PERSON);
        FeedSubscriber sub2 = new FeedSubscriber();
        sub2.setEntityId(2L);
        sub2.setEntityType(EntityType.GROUP);
        final List<FeedSubscriber> feedSubs = new ArrayList<FeedSubscriber>();
        feedSubs.add(sub1);
        feedSubs.add(sub2);

        specificUrlMappers.add(youTubeMapper);
        specificUrlMappers.add(flickrMapper);

        standardFeedMappers.put(BaseObjectType.NOTE, noteMapper);
        standardFeedMappers.put(BaseObjectType.BOOKMARK, bookmarkMapper);

        sut = new RefreshFeedExecution(standardFeedMappers, specificUrlMappers, activityDBInserter, cache,
                feedFetcherFactory, personMapper, groupMapper, feedMapper, fetcher, updateMapper);
        context.checking(new Expectations()
        {
            {
                allowing(updateMapper).execute(with(any(PersistenceRequest.class)));

                allowing(cache).get(CacheKeys.BUFFERED_ACTIVITIES);
                will(returnValue(1L));

                allowing(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaDataList));

                allowing(feed).getUrl();
                will(returnValue("http://www.flickr.com/rss/feed"));

                allowing(feed).getId();
                will(returnValue(5L));

                oneOf(feedFetcherFactory).getSyndicatedFeed(with(any(URL.class)));
                will(returnValue(atomFeed));

                oneOf(feed).setLastPostDate(with(any(Date.class)));
                oneOf(feed).setLastUpdated(with(any(Long.class)));

                oneOf(feed).setPending(false);

                allowing(activityDBInserter).execute(with(any(PersistenceRequest.class)));
                allowing(cache).addToTopOfList(with(any(String.class)), with(any(ArrayList.class)));

                allowing(feed).getLastPostDate();
                will(returnValue(new Date(2)));

                allowing(feed).getPlugin();
                will(returnValue(plugin));

                allowing(feed).getFeedSubscribers();
                will(returnValue(feedSubs));

                allowing(personMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(personOwner));

                allowing(groupMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(groupOwner));

                allowing(feedMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(feed));

                allowing(plugin).getUrl();

                allowing(plugin).getId();
                will(returnValue(1L));

                allowing(atomFeed).getEntries();
                will(returnValue(entryList));

                allowing(plugin).getObjectType();
                will(returnValue(BaseObjectType.BOOKMARK));

            }
        });

    }

    /**
     * Test with symod saying hourly and an error in the feed.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSymodHourlyAndError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(syMod));

                oneOf(syMod).getUpdateFrequency();
                will(returnValue(1));

                oneOf(syMod).getUpdatePeriod();
                will(returnValue(SyModule.HOURLY));

                oneOf(youTubeMapper).getRegex();
                will(throwException(new Exception()));

                oneOf(feed).setUpdateFrequency(MININHOUR);
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with symod saying daily and an error in the feed.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSymodDailyAndError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(syMod));

                oneOf(syMod).getUpdateFrequency();
                will(returnValue(1));

                oneOf(syMod).getUpdatePeriod();
                will(returnValue(SyModule.DAILY));

                oneOf(youTubeMapper).getRegex();
                will(throwException(new Exception()));

                oneOf(feed).setUpdateFrequency(MININDAY);
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with symod saying weekly and an error in the feed.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSymodWeeklyAndError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(syMod));

                oneOf(syMod).getUpdateFrequency();
                will(returnValue(1));

                oneOf(syMod).getUpdatePeriod();
                will(returnValue(SyModule.WEEKLY));

                oneOf(youTubeMapper).getRegex();
                will(throwException(new Exception()));

                oneOf(feed).setUpdateFrequency(MININWEEK);
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with symod saying monthly and an error in the feed.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSymodMonthlyAndError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(syMod));

                oneOf(syMod).getUpdateFrequency();
                will(returnValue(1));

                oneOf(syMod).getUpdatePeriod();
                will(returnValue(SyModule.MONTHLY));

                oneOf(youTubeMapper).getRegex();
                will(throwException(new Exception()));

                oneOf(feed).setUpdateFrequency(MININMONTH);
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with symod saying yearly and an error in the feed.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSymodYearlyAndError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(syMod));

                oneOf(syMod).getUpdateFrequency();
                will(returnValue(1));

                oneOf(syMod).getUpdatePeriod();
                will(returnValue(SyModule.YEARLY));

                oneOf(youTubeMapper).getRegex();
                will(throwException(new Exception()));

                oneOf(feed).setUpdateFrequency(MININYEAR);
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with no mapper and a specific URL mapper for the feed and 1 entry.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withNoSymodAndSpecificMapper() throws Exception
    {
        final SyndEntryImpl entry1 = context.mock(SyndEntryImpl.class, "e1");
        final ObjectMapper flickrObjectMapper = context.mock(ObjectMapper.class);
        entryList.add(entry1);

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(null));

                oneOf(youTubeMapper).getRegex();
                will(returnValue("dontmatchme"));

                oneOf(flickrMapper).getRegex();
                will(returnValue("(www.)?flickr.com"));

                oneOf(flickrMapper).getObjectMapper();
                will(returnValue(flickrObjectMapper));

                allowing(entry1).getPublishedDate();
                will(returnValue(new Date(3)));

                allowing(entry1).getUpdatedDate();
                will(returnValue(new Date(3)));

                oneOf(flickrObjectMapper).getBaseObjectType();
                oneOf(flickrObjectMapper).getBaseObject(entry1);

                oneOf(feed).setUpdateFrequency(null);

                allowing(personOwner).getAccountId();
                allowing(personOwner).getId();
                allowing(personOwner).getParentOrganization();
                allowing(groupOwner).getParentOrganization();
                allowing(personOwner).getStreamScope();
                allowing(groupOwner).getStreamScope();
                allowing(groupOwner).isPublicGroup();
                allowing(groupOwner).getShortName();
                will(returnValue(false));
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with symod undefined, 1 old entry, 1 activitystreams entry, 1 unsupported activitystreams object, and 1
     * standard note object.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSymodUndefOneActivityStreamsAndOneEntryError() throws Exception
    {
        final ActivityStreamsModuleImpl activityModule = context.mock(ActivityStreamsModuleImpl.class);
        // Feed is too old.
        final SyndEntryImpl entry1 = context.mock(SyndEntryImpl.class, "e1");
        // This feed should error out.
        final SyndEntryImpl entry2 = context.mock(SyndEntryImpl.class, "e2");
        // This feed is an activitystreams that has no object support
        final SyndEntryImpl entry3 = context.mock(SyndEntryImpl.class, "e3");
        // This is a standard entry with a known object
        final SyndEntryImpl entry4 = context.mock(SyndEntryImpl.class, "e4");

        entryList.add(entry1);
        entryList.add(entry2);
        entryList.add(entry3);
        entryList.add(entry4);

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getFeedId();
                will(returnValue(new Long(5)));

                allowing(personOwner).getAccountId();
                allowing(personOwner).getParentOrganization();
                allowing(groupOwner).getParentOrganization();
                allowing(personOwner).getId();
                allowing(personOwner).getStreamScope();
                allowing(groupOwner).getStreamScope();
                allowing(groupOwner).isPublicGroup();
                allowing(groupOwner).getShortName();
                will(returnValue(false));

                oneOf(atomFeed).getModule(SyModule.URI);
                will(returnValue(syMod));

                oneOf(syMod).getUpdateFrequency();
                will(returnValue(1));

                oneOf(syMod).getUpdatePeriod();
                will(returnValue("dontfindme"));

                oneOf(youTubeMapper).getRegex();
                will(returnValue("dontmatchme"));

                oneOf(flickrMapper).getRegex();
                will(returnValue("dontmatchmeeither"));

                // ENTRY 1
                allowing(entry1).getPublishedDate();
                will(returnValue(new Date(1)));

                // ENTRY 2
                allowing(entry2).getPublishedDate();
                will(returnValue(new Date(3)));

                allowing(entry2).getUpdatedDate();
                will(throwException(new Exception()));

                // ENTRY 3
                allowing(entry3).getPublishedDate();
                will(returnValue(new Date(4)));

                allowing(entry3).getUpdatedDate();
                will(returnValue(new Date(4)));

                oneOf(entry3).getModule(ActivityStreamsModule.URI);
                will(returnValue(activityModule));

                oneOf(activityModule).getObjectType();
                will(returnValue("PHOTO"));

                oneOf(activityModule).getAtomEntry();
                will(returnValue(entry3));

                oneOf(noteMapper).getBaseObjectType();
                oneOf(noteMapper).getBaseObject(entry3);

                // ENTRY 4
                allowing(entry4).getPublishedDate();
                will(returnValue(new Date(5)));

                allowing(entry4).getUpdatedDate();
                will(returnValue(new Date(5)));

                oneOf(entry4).getModule(ActivityStreamsModule.URI);
                will(returnValue(null));

                oneOf(bookmarkMapper).getBaseObjectType();
                oneOf(bookmarkMapper).getBaseObject(entry4);

                // Default to hourly when undefined.
                oneOf(feed).setUpdateFrequency(MININHOUR);

            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }
}
