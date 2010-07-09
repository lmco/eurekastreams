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
package org.eurekastreams.server.service.actions.strategies;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Image;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.db.DeleteImage;
import org.eurekastreams.server.persistence.mappers.db.GetImageByIdentifier;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Writes and reads (misnomer?) to the disk for images. Could be interfaced and replaced with something that reads and
 * writes to a DB.
 *
 */
public class ImageWriter
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(ImageWriter.class);

    /**
     * Insert Mapper.
     */
    private InsertMapper<Image> insertMapper;
    /**
     * Update Mapper.
     */
    private UpdateMapper<Image> updateMapper;
    /**
     * Delete Mapper.
     */
    private DeleteImage deleteMapper;
    /**
     * Get Mapper.
     */
    private GetImageByIdentifier getMapper;

    /**
     * Constructor.
     * @param inInsertMapper insert mapper.
     * @param inUpdateMapper update mapper.
     * @param inDeleteMapper delete mapper.
     * @param inGetMapper get mapper.
     */
    public ImageWriter(final InsertMapper<Image> inInsertMapper, final UpdateMapper<Image> inUpdateMapper,
            final DeleteImage inDeleteMapper, final GetImageByIdentifier inGetMapper)
    {
        insertMapper = inInsertMapper;
        updateMapper = inUpdateMapper;
        deleteMapper = inDeleteMapper;
        getMapper = inGetMapper;
    }

    /**
     * Writers a rendered image to the disk.
     *
     * @param image
     *            The image to write.
     * @param identifier
     *            The path to write it too.
     * @throws Exception
     *             in case something evil happens.
     */
    public void write(final RenderedImage image, final String identifier) throws Exception
    {
        try
        {
            Image imageInDb = getMapper.execute(identifier);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            if (imageInDb == null)
            {
                insertMapper.execute(new PersistenceRequest<Image>(new Image(identifier, baos.toByteArray())));
            }
            else
            {
                imageInDb.setImageBlob(baos.toByteArray());
                updateMapper.execute(new PersistenceRequest<Image>(imageInDb));
            }
        }
        catch (Exception ex)
        {
            log.error("Error writing file to database: " + identifier, ex);
        }
    }

    /**
     * Writes an uploaded file to the disk.
     *
     * @param fileItem
     *            The file to write.
     * @param identifier
     *            The path to write it too.
     * @throws Exception
     *             again, the evil.
     */
    public void write(final FileItem fileItem, final String identifier) throws Exception
    {
        Image imageInDb = getMapper.execute(identifier);

        if (imageInDb == null)
        {
            insertMapper.execute(new PersistenceRequest<Image>(new Image(identifier, fileItem.get())));
        }
        else
        {
            imageInDb.setImageBlob(fileItem.get());
            updateMapper.execute(new PersistenceRequest<Image>(imageInDb));
        }

    }

    /**
     * Deletes a file at a resource.
     *
     * @param identifier
     *            the path to delete the file from.
     */
    public void delete(final String identifier)
    {
        deleteMapper.execute(identifier);
    }

    /**
     * Renames a file.
     *
     * @param orig
     *            the path to rename from.
     * @param newIdentifier
     *            the path to rename to.
     */
    public void rename(final String orig, final String newIdentifier)
    {
        Image image = getMapper.execute(orig);
        image.setImageIdentifier(newIdentifier);
        updateMapper.execute(new PersistenceRequest<Image>(image));
    }

    /**
     * Read a file into a rendered image.
     *
     * @param identifier
     *            the path to read from.
     * @return the rendered image.
     * @throws IOException
     *             evil IO error.
     */
    public RenderedImage read(final String identifier) throws IOException
    {
        try
        {
            Image image = getMapper.execute(identifier);
            ByteArrayInputStream baos = new ByteArrayInputStream(image.getImageBlob());
            return ImageIO.read(baos);
        }
        catch (Exception ex)
        {
            log.error("Error reading file from disk: " + identifier, ex);
            return null;
        }
    }

    /**
     * Get an Image from a File.
     * @param file the file.
     * @return the image.
     * @throws IOException an IO exception.
     */
    public BufferedImage getImageFromFile(final FileItem file) throws IOException
    {
        ByteArrayInputStream baos = new ByteArrayInputStream(file.get());
        return ImageIO.read(baos);
    }
}
