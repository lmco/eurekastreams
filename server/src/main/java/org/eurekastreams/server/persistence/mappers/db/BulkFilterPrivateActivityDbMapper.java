package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.persistence.mappers.requests.BulkFilterPrivateActivityMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamsByIds;
import org.eurekastreams.server.search.factories.ActivityDTOFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class BulkFilterPrivateActivityDbMapper extends
        BaseArgDomainMapper<BulkFilterPrivateActivityMapperRequest, List<Long>> implements
        DomainMapper<BulkFilterPrivateActivityMapperRequest, List<Long>>
{
    private GetStreamsByIds streamMapper;

    /**
     * @param inStreamMapper
     *            the streamMapper to set
     */
    public void setStreamMapper(final GetStreamsByIds inStreamMapper)
    {
        streamMapper = inStreamMapper;
    }
    
    
    public List<Long> execute(BulkFilterPrivateActivityMapperRequest inRequest)
    {
        if (inRequest.getActivities().size() == 0)
        {
            return inRequest.getActivities();
        }
        
        Criteria criteria = getHibernateSession().createCriteria(Activity.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(getColumn("verb"));
        fields.add(getColumn("baseObjectType"));
        fields.add(Projections.property("baseObject").as("baseObjectProperties"));
        fields.add(Projections.property("recipientStreamScope.id").as("destinationStreamId"));
        fields.add(Projections.property("recipientParentOrg.id").as("recipientParentOrgId"));
        fields.add(getColumn("isDestinationStreamPublic"));
        fields.add(getColumn("actorType"));
        fields.add(getColumn("originalActorType"));
        fields.add(Projections.property("actorId").as("actorUniqueIdentifier"));
        fields.add(Projections.property("originalActorId").as("originalActorUniqueIdentifier"));
        fields.add(getColumn("postedTime"));
        fields.add(getColumn("mood"));
        fields.add(getColumn("location"));
        fields.add(getColumn("annotation"));
        fields.add(getColumn("appId"));
        fields.add(getColumn("appSource"));
        fields.add(getColumn("appName"));
        criteria.setProjection(fields);
        criteria.add(Restrictions.in("this.id", inRequest.getActivities()));

        ModelViewResultTransformer<ActivityDTO> resultTransformer = new ModelViewResultTransformer<ActivityDTO>(
                new ActivityDTOFactory());
        criteria.setResultTransformer(resultTransformer);
        List<ActivityDTO> results = criteria.list();
        for (ActivityDTO activity : results)
        {
            // fills in data from cached view of stream
            List<Long> streamIds = new ArrayList<Long>();
            streamIds.add(activity.getDestinationStream().getId());
            List<StreamScope> streams = streamMapper.execute(streamIds);
            if (streams.size() > 0)
            {
                activity.getDestinationStream().setDisplayName(streams.get(0).getDisplayName());
                activity.getDestinationStream().setUniqueIdentifier(streams.get(0).getUniqueKey());
                activity.getDestinationStream().setDestinationEntityId(streams.get(0).getDestinationEntityId());

                if (streams.get(0).getScopeType() == ScopeType.PERSON)
                {
                    activity.getDestinationStream().setType(EntityType.PERSON);
                }
                else if (streams.get(0).getScopeType() == ScopeType.GROUP)
                {
                    activity.getDestinationStream().setType(EntityType.GROUP);
                }
            }
        }        
        
        List<Long> publicIds = new ArrayList<Long>();
        
        for (ActivityDTO activity : results)
        {
            boolean isDestiationStreamPublic = activity.getIsDestinationStreamPublic();
            final Long destinationStreamId = activity.getDestinationStream().getDestinationEntityId();

            if (isDestiationStreamPublic)
            {
                publicIds.add(activity.getId());
                break;
            }

            // see if the user has access to view the private group
            if (inRequest.getGroupAccess().contains(destinationStreamId))
            {
                publicIds.add(activity.getId());
            }
        }

        return publicIds;
    }

}
