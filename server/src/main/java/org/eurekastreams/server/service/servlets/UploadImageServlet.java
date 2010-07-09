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
package org.eurekastreams.server.service.servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.server.NoCurrentUserDetails;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;
import org.eurekastreams.server.service.actions.strategies.HashGeneratorStrategy;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.springframework.context.ApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The abstract servlet class for uploading an image.
 *
 * @param <T>
 *            the type of domain entity this class maps
 */
@SuppressWarnings("serial")
public abstract class UploadImageServlet<T extends DomainEntity> extends HttpServlet implements Servlet
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(UploadImageServlet.class);

    /**
     * The spring factory.
     */
    private ApplicationContext springContext;

    /**
     * Uploads the image.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws ServletException
     *             not expected
     * @throws IOException
     *             not expected
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/plain");

        springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        UserDetails userDetails = getUserDetails();
        T domainEntity = getDomainEntity(userDetails.getUsername(), request);

        HashGeneratorStrategy hasher = new HashGeneratorStrategy();
        String imageId = hasher.hash(String.valueOf(domainEntity.getId()));

        try
        {
            FileItem uploadItem = getFileItem(request);

            if (uploadItem == null)
            {
                response.getWriter().write("fail");
                return;
            }


            // Save the File


            Object actionBean = getAction(request);

            T newDomainEntity = null;

            if (actionBean instanceof ServiceAction || actionBean instanceof TaskHandlerServiceAction)
            {
                SaveImageRequest currentRequest = new SaveImageRequest(uploadItem, domainEntity.getId(), imageId);
                ServiceActionController serviceActionController = (ServiceActionController) springContext
                        .getBean("serviceActionController");
                DefaultPrincipal principal = new DefaultPrincipal(userDetails.getUsername(),
                        ((ExtendedUserDetails) userDetails).getPerson().getOpenSocialId(),
                        ((ExtendedUserDetails) userDetails).getPerson().getId());
                ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

                if (actionBean instanceof ServiceAction)
                {
                    newDomainEntity = (T) serviceActionController.execute(currentContext,
                            (ServiceAction) actionBean);
                }
                else
                {
                    newDomainEntity = (T) serviceActionController.execute(currentContext,
                            (TaskHandlerServiceAction) actionBean);
                }
            }
            else
            {
                throw new Exception("Invalid action call within the UploadImageServlet.");
            }

            // Return the resulting person objects properties to the user.
            response.getWriter().write(imageId + "," + getResponseString(newDomainEntity));

        }
        catch (Exception e)
        {
            log.error("Error running UploadImageServlet.", e);

            response.getWriter().write("fail");
        }
    }

    /**
     * Gets the file item.
     *
     * @param request
     *            the request
     * @return the file item
     */
    private FileItem getFileItem(final HttpServletRequest request)
    {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        // TODO Store this somewhere?
        // Value is in Bytes

        final long maxFileSize = 4000000;
        upload.setFileSizeMax(maxFileSize);
        try
        {
            List items = upload.parseRequest(request);
            Iterator it = items.iterator();

            while (it.hasNext())
            {
                FileItem item = (FileItem) it.next();
                if (!item.isFormField() && item.getFieldName().equals("imageUploadFormElement")
                        && item.getName() != null && !item.getName().isEmpty())
                {
                    return item;
                }
            }
        }
        catch (Exception ex)
        {
            log.error("Error trying to get file item from upload", ex);
        }

        return null;
    }

    /**
     * Gets uesr details.
     *
     * @return the uesr details
     */
    private UserDetails getUserDetails()
    {
        UserDetails user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (null != auth)
        {
            Object obj = auth.getPrincipal();
            if (obj instanceof UserDetails)
            {
                user = (UserDetails) obj;
            }
        }

        if (null == user)
        {
            user = new NoCurrentUserDetails();
        }

        return user;
    }

    /**
     * Gets the domain entity.
     *
     * @param inName
     *            the name used to find the domain entity
     * @param request
     *            the http request object.
     * @return the domain entity
     * @throws ServletException
     *             when anything goes wrong
     */
    protected abstract T getDomainEntity(String inName, HttpServletRequest request) throws ServletException;



    /**
     * Gets the file path string.
     *
     * @param inImageId
     *            the domain entity id
     * @return the file path string
     */
    protected abstract String getFilePath(String inImageId);

    /**
     * Gets the action.
     * @param request the request.
     * @return the action
     */
    protected abstract Object getAction(HttpServletRequest request);

    /**
     * Gets the response string.
     *
     * @param inDomainEntity
     *            the domain entity
     * @return the response string
     */
    protected abstract String getResponseString(T inDomainEntity);

    /**
     * @return the spring factory
     */
    protected ApplicationContext getSpringContext()
    {
        return springContext;
    }
}
