package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

public class FollowedByPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * Default constructor.
     *
     * @param inPersonMapper
     *            person mapper.
     */
    public FollowedByPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper)
    {
        personMapper = inPersonMapper;
    }


    @Override
    public Long transform(final JSONObject request)
    {
        String accountId = request.getString("followedBy");

        log.info("Looking for cache key for activities followed by " + accountId);

        return personMapper.fetchUniqueResult(accountId).getId();

    }

}
