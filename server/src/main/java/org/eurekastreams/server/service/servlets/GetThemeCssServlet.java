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
package org.eurekastreams.server.service.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.http.HttpStatus;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet for serving out theme css.
 * 
 */
public class GetThemeCssServlet extends HttpServlet
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -77088498265276916L;

    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Request uri to theme uuid transformer.
     */
    private Transformer<String, String> requestUriToThemeUuIdTransformer = null;

    /**
     * Mapper to get theme css by uuid.
     */
    private DomainMapper<String, String> getThemeCssByUuidMapper = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
            IOException
    {
        // grab items from spring context if not initialized, 500 error if unable
        try
        {
            initializeSpringObjects();
        }
        catch (Exception e)
        {
            httpError(HttpStatus.SC_INTERNAL_SERVER_ERROR, response);
            return;
        }

        // Convert request to theme uuid, 400 if error.
        String themeUuid = null;
        try
        {
            themeUuid = requestUriToThemeUuIdTransformer.transform(request.getRequestURI());

            // StringUtils.isEmpty checks for null or empty string, String.isEmtpy only checks length
            if (StringUtils.isEmpty(themeUuid))
            {
                httpError(HttpStatus.SC_BAD_REQUEST, response);
                return;
            }
        }
        catch (Exception e)
        {
            httpError(HttpStatus.SC_BAD_REQUEST, response);
            return;
        }

        // get the string css by theme uuid, 404 on error.
        String themeCss = null;
        try
        {
            themeCss = getThemeCssByUuidMapper.execute(themeUuid);
        }
        catch (Exception e)
        {
            httpError(HttpStatus.SC_NOT_FOUND, response);
            return;
        }

        response.setContentType("text/css");
        response.setContentLength(themeCss.getBytes().length);

        PrintWriter out = response.getWriter();
        out.write(themeCss);
        out.flush();
    }

    /**
     * Initialize object from spring context if needed.
     */
    @SuppressWarnings("unchecked")
    private void initializeSpringObjects()
    {
        // grab items from spring context if not initialized, 500 error if unable
        if (requestUriToThemeUuIdTransformer == null || getThemeCssByUuidMapper == null)
        {
            ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

            // Grab needed item from spring context.
            requestUriToThemeUuIdTransformer = (Transformer<String, String>) springContext
                    .getBean("requestUriToThemeUuidTransformer");

            getThemeCssByUuidMapper = (DomainMapper<String, String>) springContext.getBean("getThemeCssByUuid");
        }
    }

    /**
     * Pump out an HTTP error.
     * 
     * @param statusCode
     *            the status code.
     * @param response
     *            the response.
     */
    private void httpError(final int statusCode, final HttpServletResponse response)
    {
        try
        {
            response.setStatus(statusCode);
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.append("<html><body><h1>Error Code: " + statusCode + "</h1><body></html>");
            writer.flush();
        }
        catch (IOException e)
        {
            log.error(e);
        }
    }
}
