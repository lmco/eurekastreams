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
package org.eurekastreams.server.persistence.mappers;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.requests.CurrentDateInMinutesRequest;

/**
 * Set all the feeds that will be updated to pending so they aren't updated more
 * than once.
 * 
 */
public class SetRefreshableFeedsAsPending extends
		BaseArgDomainMapper<CurrentDateInMinutesRequest, Boolean> 
{

	/**
	 * Execute. Refer to GetRefreshableFeedsMapper for an explanation of this
	 * WHERE logic.
	 * 
	 * @param inRequest
	 *            request holding the current date in minutes.
	 * @return true.
	 */
	@Override
	public Boolean execute(final CurrentDateInMinutesRequest inRequest) 
	{
		Query q = getEntityManager().createQuery(
				"UPDATE Feed SET pending = true WHERE id in "
						+ "(SELECT id FROM Feed WHERE ((updateFrequency "
						+ "is not null and updated + updateFrequency "
						+ "< :currentTimeInMinutes) or (updateFrequency "
						+ "is null and updated + streamPlugin.updateFrequency"
						+ "< :currentTimeInMinutes)) and pending = false)")
				.setParameter("currentTimeInMinutes",
						inRequest.getCurrentDateInMinutes());

		q.executeUpdate();
		getEntityManager().flush();
		return true;

	}

}
