/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.InAppNotificationDTO;

/**
 * Extracts the sources from a collection of notifications for use by the notifications dialog.
 */
public class SourceListBuilder
{
    /**
     * Filter to match only notifications from a specific source.
     */
    static class SpecificSourceFilter implements Source.Filter
    {
        /** Type of the source. */
        EntityType sourceType;

        /** Unique ID of the source. */
        String sourceUniqueId;

        /**
         * Constructor.
         *
         * @param inSource
         *            The source.
         */
        public SpecificSourceFilter(final Source inSource)
        {
            sourceType = inSource.getEntityType();
            sourceUniqueId = inSource.getUniqueId();
        }

        /**
         * {@inheritDoc}
         */
        public boolean shouldDisplay(final InAppNotificationDTO inItem)
        {
            return sourceType == inItem.getSourceType() && sourceUniqueId.equals(inItem.getSourceUniqueId());
        }
    }

    /** For sorting source filters. */
    private static final Comparator<Source> SOURCE_SORTER = new Comparator<Source>()
    {
        public int compare(final Source inO1, final Source inO2)
        {
            return inO1.getDisplayName().compareTo(inO2.getDisplayName());
        }
    };

    /** Source representing all notifications. */
    private final Source rootSource;

    /** Index of actual sources. Key is type+uniqueID. */
    private final Map<String, Source> sourceIndex = new HashMap<String, Source>();

    /** List of sources in display order. */
    private final List<Source> sourceList;

    /**
     * Constructor - analyzes and builds lists.
     * 
     * @param list
     *            Notifications to process.
     * @param currentUserAccountId
     *            Current user's account ID.
     */
    public SourceListBuilder(final Collection<InAppNotificationDTO> list, final String currentUserAccountId)
    {
        // -- build index of sources by type with unread counts --

        // create the high-level sources
        rootSource = new Source(null, null, "All", null, new Source.Filter()
        {
            public boolean shouldDisplay(final InAppNotificationDTO inItem)
            {
                return true;
            }
        });
        Source streamSource = new Source(null, null, "Streams", rootSource, new Source.Filter()
        {
            public boolean shouldDisplay(final InAppNotificationDTO inItem)
            {
                return EntityType.PERSON == inItem.getSourceType() || EntityType.GROUP == inItem.getSourceType();
            }
        });
        Source appSource = new Source(null, null, "Apps", rootSource, new Source.Filter()
        {
            public boolean shouldDisplay(final InAppNotificationDTO inItem)
            {
                return EntityType.APPLICATION == inItem.getSourceType();
            }
        });

        // loop through all notifications, building the sources
        List<Source> streamSources = new ArrayList<Source>();
        List<Source> appSources = new ArrayList<Source>();
        int unread = 0;
        for (InAppNotificationDTO item : list)
        {
            String sourceKey = item.getSourceType() + item.getSourceUniqueId();
            Source source = sourceIndex.get(sourceKey);
            if (source == null && item.getSourceType() != null)
            {
                Source parent;
                String name = item.getSourceName();
                List<Source> midSourceList = null;
                switch (item.getSourceType())
                {
                case PERSON:
                    parent = streamSource;
                    midSourceList = streamSources;
                    if (currentUserAccountId.equals(item.getSourceUniqueId()))
                    {
                        name = ""; // sort to beginning
                    }
                    break;
                case GROUP:
                    parent = streamSource;
                    midSourceList = streamSources;
                    break;
                case APPLICATION:
                    parent = appSource;
                    midSourceList = appSources;
                    break;
                default:
                    parent = null;
                    break;
                }
                // if a parent was found, then create the source, else leave the notif for the "all" bin
                if (parent != null)
                {
                    source = new Source(item.getSourceType(), item.getSourceUniqueId(), name, parent, null);
                    source.setFilter(new SourceListBuilder.SpecificSourceFilter(source));
                    sourceIndex.put(sourceKey, source);
                    midSourceList.add(source);
                }
            }

            if (!item.isRead())
            {
                unread++;
                if (source != null)
                {
                    source.incrementUnreadCount();
                    if (source.getParent() != null)
                    {
                        source.getParent().incrementUnreadCount();
                    }
                }
            }
        }
        rootSource.setUnreadCount(unread);

        // -- build list in display order --
        sourceList = new ArrayList<Source>(sourceIndex.size());

        // "all"
        sourceList.add(rootSource);

        // streams
        if (!streamSources.isEmpty())
        {
            // all streams
            sourceList.add(streamSource);

            // prepare list of stream sources: sort, set name on "My Stream"
            Collections.sort(streamSources, SOURCE_SORTER);
            if (streamSources.get(0).getDisplayName().isEmpty())
            {
                streamSources.get(0).setDisplayName("My Stream");
            }

            sourceList.addAll(streamSources);
        }
        // apps
        if (!appSources.isEmpty())
        {
            // all apps
            sourceList.add(appSource);

            // prepare list of sources: sort
            Collections.sort(appSources, SOURCE_SORTER);

            sourceList.addAll(appSources);
        }
    }

    /**
     * @return the rootSource
     */
    public Source getRootSource()
    {
        return rootSource;
    }

    /**
     * @return the sourceIndex
     */
    public Map<String, Source> getSourceIndex()
    {
        return sourceIndex;
    }

    /**
     * @return the sourceList
     */
    public List<Source> getSourceList()
    {
        return sourceList;
    }
}
