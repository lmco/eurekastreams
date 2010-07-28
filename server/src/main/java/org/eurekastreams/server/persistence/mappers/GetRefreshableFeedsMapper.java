/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.persistence.mappers.requests.CurrentDateInMinutesRequest;

/**
 * Gets the feeds that need to be refreshed.
 *
 */
public class GetRefreshableFeedsMapper extends
		ReadMapper<CurrentDateInMinutesRequest, List<Feed>>
{
	/**
	 * Complicated logic here, but doing it all in SQL makes this faster. So,
	 * give me all the feeds where the last time they were updated plus their
	 * update frequency interval is less than now. In other words, if it's 12:30
	 * and a feed has been updated at 12 and has an update interval of 15
	 * minutes, let me have it. If it has an interval of 45 minutes, drop it. If
	 * the interval is blank (the feed does not have it set) then refer to the
	 * plugin's interval (which is required and set up by the installation of
	 * the plugin). Ignore feeds that are pending to avoid race conditions.
	 *
	 * @param inRequest
	 *            the request holding the current time in minutes. This is
	 *            passed in so that when we call the setPending mapper we don't
	 *            have an out of sync issue if the clock has ticked forward a
	 *            minute between the calls.
	 * @return the list of feeds.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Feed> execute(final CurrentDateInMinutesRequest inRequest)
	{
		Query q = getEntityManager()
				.createQuery(
						"SELECT f FROM Feed f WHERE ((f.updateFrequency "
								+ "is not null and f.updated + f.updateFrequency "
								+ "< :currentTimeInMinutes) or (f.updateFrequency "
								+ "is null and f.updated + f.streamPlugin.updateFrequency"
								+ "< :currentTimeInMinutes))")
				.setParameter("currentTimeInMinutes",
						inRequest.getCurrentDateInMinutes());

		return q.getResultList();
	}

}
