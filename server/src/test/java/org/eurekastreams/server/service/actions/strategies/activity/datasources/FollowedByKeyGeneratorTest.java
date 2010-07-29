package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class FollowedByKeyGeneratorTest
{
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
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * System under test.
     */
    private FollowedByKeyGenerator sut = new FollowedByKeyGenerator(personMapper);

    /**
     * Passing in an empty request triggers a return on everyone list.
     */
    @Test
    public void getKeys()
    {
        JSONObject request = new JSONObject();
        request.put("followedBy", "shawkings");

        final PersonModelView person = context.mock(PersonModelView.class);
        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchUniqueResult("shawkings");
                will(returnValue(person));

                oneOf(person).getId();
                will(returnValue(7L));
            }
        });

        List<String> keys = sut.getKeys(request);
        context.assertIsSatisfied();

        assertEquals(keys.get(0), CacheKeys.ACTIVITIES_BY_FOLLOWING + "7");
    }
}
