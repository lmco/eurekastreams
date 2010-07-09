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
package org.eurekastreams.server.service.opensocial.spi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.action.request.opensocial.DeleteAppDataRequest;
import org.eurekastreams.server.action.request.opensocial.GetAppDataRequest;
import org.eurekastreams.server.action.request.opensocial.UpdateAppDataRequest;
import org.eurekastreams.server.domain.AppData;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Class that implements the Shindig Application Data Service endpoint.
 * 
 */
public class AppDataServiceImpl implements AppDataService
{

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(AppDataServiceImpl.class);

    /**
     * Instance of the service action on which requests will be made.
     */
    private ServiceAction getAppDataAction;

    /**
     * Local instance of the ServiceActionController to submit service actions through.
     */
    private ServiceActionController serviceActionController;

    /**
     * Local instance of the {@link OpenSocialPrincipalPopulator} for setting up the {@link ServiceActionContext}.
     */
    private OpenSocialPrincipalPopulator openSocialPrincipalPopulator;

    /**
     * Instance of the service action for updating data.
     */
    private ServiceAction updateDataAction;
    
    /**
     * Instance of the service action for deleting data.
     */
    private ServiceAction deleteDataAction;

    /**
     * This is the main constructor for the AppDataServiceImpl class which allows Guice to inject a Spring loaded action
     * to provide the connection to the backend database.
     * 
     * @param inGetAppDataAction
     *            - Action that will perform the database mapping for AppDataServiceImpl. This approach allows the
     *            implementation to maintain the transactional nature of the actions in non-shindig integrated actions.
     * @param inServiceActionController
     *            - instance of the service action controller responsible for loading and executing
     *            {@link ServiceAction}.
     * @param inOpenSocialPrincipalPopulator
     *            - instance of the {@link OpenSocialPrincipalPopulator} for retrieving OpenSocial {@link Principal}
     *            objects.
     * @param inUpdateAction
     *            - Action that will perform the update database mapping.
     * @param inDeleteAction
     *            - Action that will perform the delete database operation.
     */
    @Inject
    public AppDataServiceImpl(@Named("getAppData") final ServiceAction inGetAppDataAction,
            final ServiceActionController inServiceActionController,
            final OpenSocialPrincipalPopulator inOpenSocialPrincipalPopulator,
            @Named("updateAppData") final ServiceAction inUpdateAction, 
            @Named("deleteAppData") final ServiceAction inDeleteAction)
    {
        getAppDataAction = inGetAppDataAction;
        serviceActionController = inServiceActionController;
        openSocialPrincipalPopulator = inOpenSocialPrincipalPopulator;
        updateDataAction = inUpdateAction;
        deleteDataAction = inDeleteAction;
    }

    /**
     * Delete person method.
     * 
     * @param userId
     *            - user id of the person's data to delete.
     * @param groupId
     *            - id of the group that the users belong to - currently unimplemented in OpenSocial js API for Shindig.
     * @param appId
     *            - id of the application requesting the data to be deleted.
     * @param fields
     *            - fields to delete.
     * @param token
     *            - security token for the request.
     * 
     * @return void
     */
    public Future<Void> deletePersonData(final UserId userId, final GroupId groupId, final String appId,
            final Set<String> fields, final SecurityToken token)
    {
        log.debug("Entering delete Person data with userId" + userId.getUserId(token) + ", appId " + appId + ", "
                + fields.size() + ", token appId " + token.getAppId());

        try
        {
        String userOpenSocialId = userId.getUserId(token);

        // create the request
        DeleteAppDataRequest params = new DeleteAppDataRequest(Long.parseLong(appId), userOpenSocialId, fields);

        // create the context
        ServiceActionContext context = new ServiceActionContext(params, openSocialPrincipalPopulator
                .getPrincipal(userOpenSocialId));

        // Execute action via service action controller to perform the update.
        serviceActionController.execute(context, deleteDataAction);
        }
        catch (Exception e)
        {
            log.error("Error occurred deleting OpenSocial Application Data " + e.toString());

            throw new SocialSpiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }
        return ImmediateFuture.newInstance(null);
    }

    /**
     * Get the person data.
     * 
     * @param userIds
     *            - set of user ids to retrieve person data for.
     * @param groupId
     *            - id of the group that the users belong to - currently unimplemented in OpenSocial js API for Shindig.
     * @param appId
     *            - id of the application requesting the data to be deleted.
     * @param fields
     *            - fields to retrieve.
     * @param token
     *            - security token for the request.
     * 
     * @return DataCollection containing the data requested.
     */
    public Future<DataCollection> getPersonData(final Set<UserId> userIds, final GroupId groupId, final String appId,
            final Set<String> fields, final SecurityToken token)
    {
        log.debug("Entering getPerson data with " + userIds.size() + " userIds, appId " + appId + ", " + fields.size()
                + ", token appId " + token.getAppId());

        AppData currentAppData = null;
        GetAppDataRequest currentRequest = new GetAppDataRequest();
        ServiceActionContext currentContext;
        Map<String, Map<String, String>> results = new HashMap<String, Map<String, String>>();
        try
        {
            log.debug("Retrieve userIds");
            List<String> currentUserIds = SPIUtils.getUserList(userIds, token);
            currentRequest.setApplicationId(new Long(appId));

            // TODO add in group implementation when Friends list is available.
            // switch(groupId.getType())
            // {
            // case all:
            // //TODO Assemble specialized parameters for all Group
            // break;
            // case deleted:
            // //TODO Assemble specialized parameters for deleted Group
            // break;
            // case friends:
            // //TODO Assemble specialized parameters for deleted Group
            // break;
            // case groupId:
            // //TODO Assemble specialized parameters for deleted Group
            // break;
            // default:
            // //Self group
            // break;
            // }

            log.debug("Loop through userIds");
            for (String currentUserId : currentUserIds)
            {
                currentRequest.setOpenSocialId(currentUserId);
                currentContext = new ServiceActionContext(currentRequest, openSocialPrincipalPopulator
                        .getPrincipal(currentUserId));
                currentAppData = (AppData) serviceActionController.execute(currentContext, getAppDataAction);
                if (currentAppData != null)
                {
                    results.put(currentAppData.getPerson().getOpenSocialId(), currentAppData.getValues());
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error occurred retrieving appData " + e.toString());

            throw new SocialSpiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }

        DataCollection dc = new DataCollection(results);
        return ImmediateFuture.newInstance(dc);
    }

    /**
     * Update the person data.
     * 
     * @param userId
     *            - user id of the person to update data for.
     * @param groupId
     *            - id of the group that the users belong to - currently unimplemented in OpenSocial js API for Shindig.
     * @param appId
     *            - id of the application requesting the data to be deleted.
     * @param fields
     *            - fields to retrieve.
     * @param values
     *            - values to update the fields with.
     * @param token
     *            - security token for the request.
     * 
     * @return void
     */
    public Future<Void> updatePersonData(final UserId userId, final GroupId groupId, final String appId,
            final Set<String> fields, final Map<String, String> values, final SecurityToken token)
    {
        log.debug("Entering getPerson data with userId " + userId.getUserId(token) + ", appId " + appId + ", "
                + fields.size() + ", Map length " + values.size() + ", token appId " + token.getAppId());
        try
        {
            String userOpenSocialId = userId.getUserId(token);

            // create the request
            UpdateAppDataRequest params = new UpdateAppDataRequest(Long.parseLong(appId), userOpenSocialId,
                    new HashMap<String, String>(values));

            // create the context
            ServiceActionContext context = new ServiceActionContext(params, openSocialPrincipalPopulator
                    .getPrincipal(userOpenSocialId));

            // Execute action via service action controller to perform the update.
            serviceActionController.execute(context, updateDataAction);
        }
        catch (Exception e)
        {
            log.error("Error occurred updating AppData " + e.toString());
            throw new SocialSpiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }

        return ImmediateFuture.newInstance(null);
    }

}
