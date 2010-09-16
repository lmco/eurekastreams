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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;

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

    /** Test data. */
    private static final String FEED_URL = "http://www.flickr.com/rss/feed";

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



    /** Fixture: feed. */
    private Feed feed = context.mock(Feed.class);

    /** Fixture: stream plugin. */
    private PluginDefinition plugin = context.mock(PluginDefinition.class);

    /** Fixture: atom feed. */
    private SyndFeed atomFeed1 = context.mock(SyndFeed.class, "atomFeed1");

    /** Fixture: atom feed. */
    private SyndFeed atomFeed2 = context.mock(SyndFeed.class, "atomFeed2");


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
        sub1.setRequestor(new Person("user1", "", "", "", ""));
        FeedSubscriber sub2 = new FeedSubscriber();
        sub2.setEntityId(2L);
        sub2.setEntityType(EntityType.GROUP);
        sub2.setRequestor(new Person("user2", "", "", "", ""));
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
                allowing(ac).getParams();
                will(returnValue(new RefreshFeedRequest(5L)));

                // ---- feed ----
                allowing(feed).getUrl();
                will(returnValue(FEED_URL));

                allowing(feed).getLastPostDate();
                will(returnValue(new Date(2)));

                allowing(feed).getPlugin();
                will(returnValue(plugin));

                allowing(feed).getFeedSubscribers();
                will(returnValue(feedSubs));

                allowing(feed).getTitle();
                will(returnValue("This is a feed"));

                // allowing(feed).getId();
                // will(returnValue(5L));

                // ---- plugin ----
                allowing(plugin).getUrl();

                allowing(plugin).getId();
                will(returnValue(1L));

                allowing(plugin).getObjectType();
                will(returnValue(BaseObjectType.BOOKMARK));


                oneOf(youTubeMapper).getRegex();
                will(returnValue("nevermatchme"));


                allowing(updateMapper).execute(with(any(PersistenceRequest.class)));

                allowing(cache).get(CacheKeys.BUFFERED_ACTIVITIES);
                will(returnValue(1L));

                allowing(fetcher).getGadgetsMetaData(with(any(Map.class)));
                will(returnValue(metaDataList));


                allowing(activityDBInserter).execute(with(any(PersistenceRequest.class)));
                allowing(cache).addToTopOfList(with(any(String.class)), with(any(ArrayList.class)));


                allowing(personMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(personOwner));

                allowing(groupMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(groupOwner));

                allowing(feedMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(feed));



                // ---- updates always made ----
                oneOf(feed).setLastPostDate(with(any(Date.class)));
                oneOf(feed).setLastUpdated(with(any(Long.class)));
                oneOf(feed).setPending(false);
            }
        });

    }

    /**
     * Setup expectations for fetching anonymously.
     *
     * @throws Exception
     *             Shouldn't.
     */
    private void setupFetchAnonymous() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(feedFetcherFactory).getSyndicatedFeed(with(equal(FEED_URL)), with(any(List.class)));
                will(returnValue(Collections.singletonMap(null, atomFeed1)));
            }
        });
    }

    /**
     * Setup expectations for fetching on a per-user basis.
     *
     * @throws Exception
     *             Shouldn't.
     */
    private void setupFetchByUser() throws Exception
    {
        final Map<String, SyndFeed> feeds = new TreeMap<String, SyndFeed>(); // use TreeMap to force order
        feeds.put("user1", atomFeed1);
        feeds.put("user2", atomFeed2);

        context.checking(new Expectations()
        {
            {
                allowing(feedFetcherFactory).getSyndicatedFeed(with(equal(FEED_URL)), with(any(List.class)));
                will(returnValue(feeds));
            }
        });
    }

    /**
     * Creates expectations for no SyMod.
     */
    private void setupNoSyMod()
    {
        context.checking(new Expectations()
        {
            {
                allowing(atomFeed1).getModule(SyModule.URI);
                will(returnValue(null));

                allowing(atomFeed2).getModule(SyModule.URI);
                will(returnValue(null));

                oneOf(feed).setUpdateFrequency(null);
            }
        });
    }

    /**
     * Core behavior for update frequency tests.
     *
     * @param period
     *            Time unit of update period.
     * @param frequency
     *            Frequency of update.
     * @param expected
     *            Expected value to be set in the feed.
     * @throws Exception
     *             Shouldn't.
     */
    private void coreUpdateFrequencyTest(final String period, final int frequency, final Long expected)
            throws Exception
    {
        final SyModule syMod = context.mock(SyModule.class);

        setupFetchByUser();
        context.checking(new Expectations()
        {
            {
                allowing(flickrMapper).getRegex();
                will(returnValue("dontmatchthis"));

                allowing(atomFeed1).getModule(SyModule.URI);
                will(returnValue(syMod));

                never(atomFeed2).getModule(with(any(String.class)));

                allowing(syMod).getUpdateFrequency();
                will(returnValue(frequency));

                allowing(syMod).getUpdatePeriod();
                will(returnValue(period));

                allowing(atomFeed1).getEntries();
                will(returnValue(Collections.EMPTY_LIST));

                allowing(atomFeed2).getEntries();
                will(returnValue(Collections.EMPTY_LIST));

                oneOf(feed).setUpdateFrequency(expected);
            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Tests SyMod-specified update frequency.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSymodDefault() throws Exception
    {
        coreUpdateFrequencyTest("Undefined Value", 1, MININHOUR);
    }

    /**
     * Tests SyMod-specified update frequency.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSymodHourly() throws Exception
    {
        coreUpdateFrequencyTest(SyModule.HOURLY, 1, MININHOUR);
    }

    /**
     * Tests SyMod-specified update frequency.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSymodDaily() throws Exception
    {
        coreUpdateFrequencyTest(SyModule.DAILY, 1, MININDAY);
    }

    /**
     * Tests SyMod-specified update frequency.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSymodWeekly() throws Exception
    {
        coreUpdateFrequencyTest(SyModule.WEEKLY, 1, MININWEEK);
    }

    /**
     * Tests SyMod-specified update frequency.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSymodMonthly() throws Exception
    {
        coreUpdateFrequencyTest(SyModule.MONTHLY, 1, MININMONTH);
    }

    /**
     * Tests SyMod-specified update frequency.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testSymodYearly() throws Exception
    {
        coreUpdateFrequencyTest(SyModule.YEARLY, 1, MININYEAR);
    }


    /**
     * Test with no mapper and a specific URL mapper for the feed and 1 entry.
     *
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withSpecificMapper() throws Exception
    {
        final ObjectMapper flickrObjectMapper = context.mock(ObjectMapper.class);
        final SyndEntryImpl entry1 = context.mock(SyndEntryImpl.class, "e1");
        final List<SyndEntryImpl> entryList = Collections.singletonList(entry1);

        setupNoSyMod();
        setupFetchAnonymous();
        context.checking(new Expectations()
        {
            {
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

                allowing(personOwner).getAccountId();
                allowing(personOwner).getId();
                allowing(personOwner).getParentOrganization();
                allowing(groupOwner).getParentOrganization();
                allowing(personOwner).getStreamScope();
                allowing(groupOwner).getStreamScope();
                allowing(groupOwner).isPublicGroup();
                allowing(groupOwner).getShortName();
                will(returnValue(false));

                allowing(atomFeed1).getEntries();
                will(returnValue(entryList));

            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }

    /**
     * Test with 1 old entry, 1 activitystreams entry, 1 unsupported activitystreams object, and 1 standard note object.
     * 
     * @throws Exception
     *             exception feed throws.
     */
    @Test
    public void withOneActivityStreamsAndOneEntryError() throws Exception
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

        final List<SyndEntryImpl> entryList = new LinkedList<SyndEntryImpl>();
        entryList.add(entry1);
        entryList.add(entry2);
        entryList.add(entry3);
        entryList.add(entry4);

        setupNoSyMod();
        setupFetchAnonymous();
        context.checking(new Expectations()
        {
            {
                allowing(personOwner).getAccountId();
                allowing(personOwner).getParentOrganization();
                allowing(groupOwner).getParentOrganization();
                allowing(personOwner).getId();
                allowing(personOwner).getStreamScope();
                allowing(groupOwner).getStreamScope();
                allowing(groupOwner).isPublicGroup();
                allowing(groupOwner).getShortName();
                will(returnValue(false));


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

                allowing(atomFeed1).getEntries();
                will(returnValue(entryList));

                oneOf(bookmarkMapper).getBaseObjectType();
                oneOf(bookmarkMapper).getBaseObject(entry4);

            }
        });

        sut.execute(ac);

        context.assertIsSatisfied();
    }
}
