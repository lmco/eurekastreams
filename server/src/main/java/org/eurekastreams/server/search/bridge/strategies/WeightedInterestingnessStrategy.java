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
package org.eurekastreams.server.search.bridge.strategies;

import java.util.List;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.db.GetCommentorIdsByActivityId;
import org.eurekastreams.server.persistence.mappers.stream.GetOrderedCommentIdsByActivityId;

/**
 * Computes how interesting an activity is, expressed as a Long. Takes different parameters, such as number of comments,
 * unique commentors and time and weights them to express a value.
 */
public class WeightedInterestingnessStrategy implements ComputeInterestingnessOfActivityStrategy
{

    /**
     * Seconds in a day.
     */
    public static final Long SECONDS_IN_DAY = 86400L;

    /**
     * Epoch time at 2010. Used to calculate dates since 2010. No real danger of field rolling over, but saves chars and
     * in theory nobody will use this app before 2010.
     */
    public static final long EPOCH_2010 = 1262304000L;

    /**
     * Weight of a comment.
     */
    private int commentWeight = 0;

    /**
     * Weight of a unique commentor.
     */
    private int uniqueUserCommentWeight = 0;

    /**
     * Weight of time.
     */
    private int timeWeight = 0;

    /**
     * DAO for finding comment ids.
     */
    private GetOrderedCommentIdsByActivityId commentIdsByActivityIdDAO;

    /**
     * DAO for finding unique commentors.
     */
    private static GetCommentorIdsByActivityId commentorIdsByActivityIdDao;

    /**
     * Constructor.
     * 
     * @param inCommentIdsByActivityIdDAO
     *            the DAO for finding comment ids.
     * @param inCommentorIdsByActivityIdDao
     *            the DAO for finding unique commentors.
     * @param inCommentWeight
     *            weight of comments.
     * @param inUniqueUserCommentWeight
     *            weight of unique commentors.
     * @param inTimeWeight
     *            weight of time.
     */
    public WeightedInterestingnessStrategy(final GetOrderedCommentIdsByActivityId inCommentIdsByActivityIdDAO,
            final GetCommentorIdsByActivityId inCommentorIdsByActivityIdDao, final int inCommentWeight,
            final int inUniqueUserCommentWeight, final int inTimeWeight)
    {
        commentIdsByActivityIdDAO = inCommentIdsByActivityIdDAO;
        commentorIdsByActivityIdDao = inCommentorIdsByActivityIdDao;
        commentWeight = inCommentWeight;
        uniqueUserCommentWeight = inUniqueUserCommentWeight;
        timeWeight = inTimeWeight;
    }

    /**
     * Calculate the interestingness. Note, time is only given weight if other factors exist.
     * 
     * @param activity
     *            the activity.
     * @return the interestingness.
     */
    public Long computeInterestingness(final Activity activity)
    {
        Long interestingNess = 0L;

        List<Long> commentList = commentIdsByActivityIdDAO.execute(activity.getId());

        if (commentList != null)
        {
            interestingNess += commentList.size() * commentWeight;
        }

        List<Long> commentorList = commentorIdsByActivityIdDao.execute(activity.getId());

        if (commentorList != null)
        {
            interestingNess += commentorList.size() * uniqueUserCommentWeight;
        }

        // Add Time weight, if interesting.
        if (interestingNess > 0L)
        {
            interestingNess += ((activity.getPostedTime().getTime() - EPOCH_2010) / SECONDS_IN_DAY) * timeWeight;
        }

        return interestingNess;
    }

}
