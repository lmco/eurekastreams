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
package org.eurekastreams.server.persistence.mappers.db.notification;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * This mapper returns the details of an existing in-app notification for a
 * recipient, notification type, and URL if one exists.
 */
public class GetExistingInAppNotificationForAggregation extends
		BaseArgDomainMapper<InAppNotificationEntity, InAppNotificationEntity> {

	/**
	 * Queries the database for an existing notification for a user and URL.
	 * 
	 * @param inRequest
	 *            Container for search criteria. Callers should populate the
	 *            recipient, notification type, and url properties
	 * @return An existing unread notification that matches the search criteria.
	 */
	@Override
	public InAppNotificationEntity execute(
			final InAppNotificationEntity inRequest) {
		String q = "from InAppNotification where recipient.id = :userId "
				+ "and url = :url and isRead = false "
				+ "and notificationType = :notificationType";
		Query query = getEntityManager().createQuery(q)
				.setParameter("userId", inRequest.getRecipient().getId())
				.setParameter("url", inRequest.getUrl())
				.setParameter("notificationType", inRequest.getNotificationType());
		List results = query.getResultList();
		if (results.size() < 1) {
			return null;
		}
		return (InAppNotificationEntity) results.get(0);
	}

}
