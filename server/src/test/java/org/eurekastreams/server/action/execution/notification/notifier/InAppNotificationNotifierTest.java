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
package org.eurekastreams.server.action.execution.notification.notifier;

import static org.junit.Assert.assertNull;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.action.execution.notification.NotificationPropertyKeys;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Identifiable;
import org.eurekastreams.server.domain.InAppNotificationEntity;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests InAppNotificationNotifier.
 */
public class InAppNotificationNotifierTest {
	/** Test data. */
	private static final Long RECIPIENT1 = 50L;

	/** Test data. */
	private static final Long RECIPIENT2 = 52L;

	/** Test data. */
	private static final String TEMPLATE = "This is the template";

	/** Test data. */
	private static final String AGGREGATE_TEMPLATE = "This is the aggregate template";

	/** Test data. */
	private static final String RENDERED = "This is the rendered template";

	/** Test data. */
	private static final NotificationType OK_TYPE = NotificationType.POST_TO_PERSONAL_STREAM;

	/** Used for mocking objects. */
	private final JUnit4Mockery context = new JUnit4Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/** Apache Velocity templating engine. */
	private final VelocityEngine velocityEngine = context
			.mock(VelocityEngine.class);

	/**
	 * Global context for Apache Velocity templating engine. (Holds system-wide
	 * properties.)
	 */
	private final Context velocityGlobalContext = context.mock(Context.class);

	/** Mapper to persist the notification. */
	private final DomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> insertMapper = context
			.mock(DomainMapper.class, "insertMapper");

	/** Mapper to update aggregate notifications. */
	private final DomainMapper<PersistenceRequest<InAppNotificationEntity>, Boolean> updateMapper = context
			.mock(DomainMapper.class, "updateMapper");

	/** Mapper to sync unread alert count in cache. */
	private final DomainMapper<Long, UnreadInAppNotificationCountDTO> syncMapper = context
			.mock(DomainMapper.class, "syncMapper");

	/** Provides a dummy person object for persisting the in-app entity. */
	private final DomainMapper<Long, Person> placeholderPersonMapper = context
			.mock(DomainMapper.class, "placeholderPersonMapper");

	/** Looks up existing notifications for aggregation. */
	private final DomainMapper<InAppNotificationEntity, InAppNotificationEntity> existingNotificationMapper = context
			.mock(DomainMapper.class, "existingNotificationMapper");

	/** Dummy person. */
	private final Person person1 = context.mock(Person.class, "person1");

	/** Dummy person. */
	private final Person person2 = context.mock(Person.class, "person2");

	/** SUT. */
	private InAppNotificationNotifier sut;

	/** Templates. */
	private final Map<NotificationType, String> templates = Collections
			.unmodifiableMap(Collections.singletonMap(OK_TYPE, TEMPLATE));

	/** Aggregated Templates. */
	private final Map<NotificationType, String> aggregateTemplates = Collections
			.unmodifiableMap(Collections.singletonMap(
					NotificationType.COMMENT_TO_COMMENTED_POST,
					AGGREGATE_TEMPLATE));

	/** Recipients. */
	private final Collection<Long> recipients = Collections
			.unmodifiableList(Arrays.asList(RECIPIENT1, RECIPIENT2));

	/** Recipient index. */
	private final Map<Long, PersonModelView> recipientIndex;

	/**
	 * One-time setup.
	 */
	public InAppNotificationNotifierTest() {
		Map<Long, PersonModelView> map = new HashMap<Long, PersonModelView>();
		map.put(RECIPIENT1, context.mock(PersonModelView.class, "recipient1"));
		map.put(RECIPIENT2, context.mock(PersonModelView.class, "recipient2"));
		recipientIndex = Collections.unmodifiableMap(map);
	}

	/**
	 * Setup before each test.
	 */
	@Before
	public void setUp() {
		sut = new InAppNotificationNotifier(velocityEngine,
				velocityGlobalContext, templates, aggregateTemplates,
				insertMapper, updateMapper, syncMapper,
				placeholderPersonMapper, existingNotificationMapper);

	}

	/**
	 * Tests notify.
	 * 
	 * @throws Exception
	 *             Won't.
	 */
	@Test
	public void testNotifyUnknownTemplate() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(placeholderPersonMapper).execute(with(equal(RECIPIENT1)));
				will(returnValue(person1));
				oneOf(placeholderPersonMapper).execute(with(equal(RECIPIENT2)));
				will(returnValue(person2));
			}
		});
		Collection<UserActionRequest> result = sut.notify(
				NotificationType.PASS_THROUGH, recipients,
				Collections.EMPTY_MAP, null);

		context.assertIsSatisfied();

		assertNull(result);
	}

	/**
	 * Tests notify.
	 * 
	 * @throws Exception
	 *             Won't.
	 */
	@Test
	public void testNotifyBasic() throws Exception {
		final States state = context.states("main");
		state.startsAs("none");
		context.checking(new Expectations() {
			{
				oneOf(placeholderPersonMapper).execute(RECIPIENT1);
				will(returnValue(person1));
				then(state.is("person1"));

				exactly(recipients.size())
						.of(velocityEngine)
						.evaluate(
								with(any(VelocityContext.class)),
								with(any(StringWriter.class)),
								with(equal("InAppNotification-POST_TO_PERSONAL_STREAM")),
								with(equal(TEMPLATE)));
				will(new AppendRenderedAction());

				oneOf(insertMapper).execute(
						with(new EasyMatcher<PersistenceRequest>() {
							@Override
							protected boolean isMatch(
									final PersistenceRequest testObject) {
								InAppNotificationEntity notif = (InAppNotificationEntity) testObject
										.getDomainEnity();
								return person1 == notif.getRecipient()
										&& RENDERED.equals(notif.getMessage())
										&& OK_TYPE == notif
												.getNotificationType()
										&& notif.getUrl() == null
										&& !notif.isHighPriority()
										&& notif.getSourceType() == EntityType.NOTSET
										&& notif.getSourceUniqueId() == null
										&& notif.getSourceName() == null
										&& notif.getAvatarOwnerType() == EntityType.NOTSET
										&& notif.getAvatarOwnerUniqueId() == null;
							}
						}));
				when(state.is("person1"));

				oneOf(syncMapper).execute(RECIPIENT1);
				when(state.is("person1"));

				oneOf(placeholderPersonMapper).execute(RECIPIENT2);
				will(returnValue(person2));
				then(state.is("person2"));

				oneOf(person1).getId();
				will(returnValue(RECIPIENT1));
				when(state.is("person1"));

				oneOf(person2).getId();
				will(returnValue(RECIPIENT2));
				when(state.is("person2"));

				oneOf(insertMapper).execute(
						with(new EasyMatcher<PersistenceRequest>() {
							@Override
							protected boolean isMatch(
									final PersistenceRequest testObject) {
								InAppNotificationEntity notif = (InAppNotificationEntity) testObject
										.getDomainEnity();
								return person2 == notif.getRecipient()
										&& RENDERED.equals(notif.getMessage())
										&& OK_TYPE == notif
												.getNotificationType()
										&& notif.getUrl() == null
										&& !notif.isHighPriority()
										&& notif.getSourceType() == EntityType.NOTSET
										&& notif.getSourceUniqueId() == null
										&& notif.getSourceName() == null
										&& notif.getAvatarOwnerType() == EntityType.NOTSET
										&& notif.getAvatarOwnerUniqueId() == null;
							}
						}));
				when(state.is("person2"));

				oneOf(syncMapper).execute(RECIPIENT2);
				when(state.is("person2"));
			}
		});

		Collection<UserActionRequest> result = sut.notify(
				NotificationType.POST_TO_PERSONAL_STREAM, recipients,
				Collections.EMPTY_MAP, recipientIndex);

		context.assertIsSatisfied();

		assertNull(result);
	}

	/**
	 * Tests notify.
	 * 
	 * @throws Exception
	 *             Won't.
	 */
	@Test
	public void testNotifyFullFields() throws Exception {
		final String url = "http://www.eurekastreams.org";
		final String sourceName = "Source Name";
		final String sourceUniqueId = "Source Unique ID";
		final EntityType sourceType = EntityType.GROUP;
		final String actorUniqueId = "Actor Unique ID";
		final EntityType actorType = EntityType.PERSON;

		final Identifiable source = context.mock(Identifiable.class, "source");
		final Identifiable actor = context.mock(Identifiable.class, "actor");

		context.checking(new Expectations() {
			{
				oneOf(placeholderPersonMapper).execute(RECIPIENT1);
				will(returnValue(person1));

				oneOf(velocityEngine)
						.evaluate(
								with(any(VelocityContext.class)),
								with(any(StringWriter.class)),
								with(equal("InAppNotification-POST_TO_PERSONAL_STREAM")),
								with(equal(TEMPLATE)));
				will(new AppendRenderedAction());

				oneOf(insertMapper).execute(
						with(new EasyMatcher<PersistenceRequest>() {
							@Override
							protected boolean isMatch(
									final PersistenceRequest testObject) {
								InAppNotificationEntity notif = (InAppNotificationEntity) testObject
										.getDomainEnity();
								return person1 == notif.getRecipient()
										&& RENDERED.equals(notif.getMessage())
										&& OK_TYPE == notif
												.getNotificationType()
										&& url.equals(notif.getUrl())
										&& notif.isHighPriority()
										&& notif.getSourceType() == EntityType.GROUP
										&& sourceUniqueId.equals(notif
												.getSourceUniqueId())
										&& sourceName.equals(notif
												.getSourceName())
										&& notif.getAvatarOwnerType() == EntityType.PERSON
										&& actorUniqueId.equals(notif
												.getAvatarOwnerUniqueId());
							}
						}));

				oneOf(syncMapper).execute(RECIPIENT1);

				allowing(source).getDisplayName();
				will(returnValue(sourceName));
				allowing(source).getUniqueId();
				will(returnValue(sourceUniqueId));
				allowing(source).getEntityType();
				will(returnValue(sourceType));
				allowing(actor).getUniqueId();
				will(returnValue(actorUniqueId));
				allowing(actor).getEntityType();
				will(returnValue(actorType));

				oneOf(person1).getId();
				will(returnValue(RECIPIENT1));
			}
		});

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(NotificationPropertyKeys.URL, url);
		properties.put(NotificationPropertyKeys.HIGH_PRIORITY, Boolean.TRUE);
		properties.put(NotificationPropertyKeys.SOURCE, source);
		properties.put(NotificationPropertyKeys.ACTOR, actor);

		Collection<UserActionRequest> result = sut.notify(
				NotificationType.POST_TO_PERSONAL_STREAM,
				Collections.singletonList(RECIPIENT1), properties,
				recipientIndex);

		context.assertIsSatisfied();

		assertNull(result);
	}

	/**
	 * Tests notify.
	 * 
	 * @throws Exception
	 *             Won't.
	 */
	@Test
	public void testNotifyUnknownRecipient() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(placeholderPersonMapper).execute(RECIPIENT1);
				will(returnValue(null));
			}
		});

		Collection<UserActionRequest> result = sut.notify(
				NotificationType.COMMENT_TO_COMMENTED_POST,
				Collections.singletonList(RECIPIENT1), Collections.EMPTY_MAP,
				recipientIndex);

		context.assertIsSatisfied();

		assertNull(result);
	}

	/**
	 * Tests notification aggregation.
	 * 
	 * @throws Exception
	 *             Won't
	 */
	@Test
	public void testNotifyWithAggregation() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(placeholderPersonMapper).execute(RECIPIENT1);
				will(returnValue(person1));

				oneOf(velocityEngine)
						.evaluate(
								with(any(VelocityContext.class)),
								with(any(StringWriter.class)),
								with(equal("InAppNotification-COMMENT_TO_COMMENTED_POST")),
								with(equal(AGGREGATE_TEMPLATE)));
				will(new AppendRenderedAction());

				InAppNotificationEntity existingNotification = new InAppNotificationEntity();
				existingNotification.setAggregationCount(3);
				oneOf(existingNotificationMapper).execute(
						with(any(InAppNotificationEntity.class)));
				will(returnValue(existingNotification));

				oneOf(updateMapper).execute(
						with(new EasyMatcher<PersistenceRequest>() {
							@Override
							protected boolean isMatch(
									final PersistenceRequest testObject) {
								InAppNotificationEntity notif = (InAppNotificationEntity) testObject
										.getDomainEnity();
								return RENDERED.equals(notif.getMessage())
										&& notif.getAggregationCount() == 4;
							}
						}));

				oneOf(person1).getId();
				will(returnValue(RECIPIENT1));

				oneOf(syncMapper).execute(RECIPIENT1);
			}
		});
		Collection<UserActionRequest> result = sut.notify(
				NotificationType.COMMENT_TO_COMMENTED_POST,
				Collections.singletonList(RECIPIENT1), Collections.EMPTY_MAP,
				recipientIndex);
		context.assertIsSatisfied();
	}

	/** Custom action to simulate side effects of Velocity. */
	private class AppendRenderedAction implements Action {
		@Override
		public Object invoke(final Invocation inv) throws Throwable {
			((StringWriter) inv.getParameter(1)).append(RENDERED);
			return true;
		}

		@Override
		public void describeTo(final Description arg0) {
		}
	}
}
