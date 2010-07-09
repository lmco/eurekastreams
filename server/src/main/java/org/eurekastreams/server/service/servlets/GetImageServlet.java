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

import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Get an Image via a Servlet.
 * 
 */
public class GetImageServlet extends HttpServlet
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetImageServlet.class);
    /**
     * The spring factory.
     */
    private ApplicationContext springContext;

    /**
     * Serial.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 404 is a magic number, he lived by the sea.
     */
    private static final int NOTFOUND = 404;

    /**
     * 500 is even more special. Treat him right.
     */
    private static final int ERROR = 500;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetImageServlet()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
            IOException
    {
        springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        ImageWriter imageWriter = (ImageWriter) springContext.getBean("imageWriter");
        RenderedImage image = imageWriter.read(request.getParameter("img"));

        // show a 404 page
        if (image == null)
        {
            httpError(NOTFOUND, response);
        }
        else
        {
            BufferedOutputStream bos = null;
            ByteArrayOutputStream baos = null;

            try
            {
                baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);

                bos = new BufferedOutputStream(response.getOutputStream());

                response.setContentType("image/png");
                response.setContentLength(baos.toByteArray().length);

                bos.write(baos.toByteArray());

            }
            catch (Exception e)
            {
                // Tell the user there was some internal server error.\
                // 500 - Internal server error.
                httpError(ERROR, response);
            }
            finally
            {
                if (baos != null)
                {
                    try
                    {
                        baos.close();
                    }
                    catch (IOException e)
                    {
                        log.error(e);
                        // To late to do anything about it now, we may have already sent some data to user.
                    }
                }
                if (bos != null)
                {
                    try
                    {
                        bos.close();
                    }
                    catch (IOException e)
                    {
                        log.error(e);
                        // To late to do anything about it now, we may have already sent some data to user.
                    }
                }
            }
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        doGet(request, response);
    }
}
