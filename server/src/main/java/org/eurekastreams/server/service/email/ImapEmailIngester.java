/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ingests email from a mailbox and processes it.
 */
public class ImapEmailIngester
{
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** For getting a connection to the mail server. */
    private final ImapStoreFactory storeFactory;

    /** Does the actual work on each message. */
    private final MessageProcessor messageProcessor;

    /** Name of folder containing messages to process. */
    private final String inputFolderName;

    /** Name of folder to receive messages that failed processing. */
    private final String errorFolderName;

    /** Name of folder to receive messages that were successfully processed. */
    private final String successFolderName;

    /** Name of folder to receive messages that are discarded (no-reply). */
    private final String discardFolderName;

    /**
     * Constructor.
     *
     * @param inStoreFactory
     *            For getting a connection to the mail server.
     * @param inMessageProcessor
     *            Does the actual work on each message.
     * @param inInputFolderName
     *            Name of folder containing messages to process.
     * @param inErrorFolderName
     *            Name of folder to receive messages that failed processing.
     * @param inSuccessFolderName
     *            Name of folder to receive messages that were successfully processed.
     * @param inDiscardFolderName
     *            Name of folder to receive messages that are discarded (no-reply).
     */
    public ImapEmailIngester(final ImapStoreFactory inStoreFactory, final MessageProcessor inMessageProcessor,
            final String inInputFolderName, final String inErrorFolderName, final String inSuccessFolderName,
            final String inDiscardFolderName)
    {
        storeFactory = inStoreFactory;
        messageProcessor = inMessageProcessor;
        inputFolderName = inInputFolderName;
        errorFolderName = inErrorFolderName;
        successFolderName = inSuccessFolderName;
        discardFolderName = inDiscardFolderName;
    }

    /**
     * Ingests email from a mailbox.
     */
    public void execute()
    {
        // get message store
        Store store;
        try
        {
            long startTime = System.nanoTime();
            store = storeFactory.getStore();
            log.debug("Connected to mail store in {}ns", System.nanoTime() - startTime);
        }
        catch (MessagingException ex)
        {
            log.error("Error getting message store.", ex);
            return;
        }
        try
        {
            // get folders
            Folder inputFolder = store.getFolder(inputFolderName);
            if (!inputFolder.exists())
            {
                log.error("Input folder {} does not exist.", inputFolderName);
                return;
            }
            Folder successFolder = null;
            if (StringUtils.isNotBlank(successFolderName))
            {
                successFolder = store.getFolder(successFolderName);
                if (!successFolder.exists())
                {
                    log.error("Success folder {} does not exist.", successFolderName);
                    return;
                }
            }
            Folder discardFolder = null;
            if (StringUtils.isNotBlank(discardFolderName))
            {
                discardFolder = store.getFolder(discardFolderName);
                if (!discardFolder.exists())
                {
                    log.error("Discard folder {} does not exist.", discardFolderName);
                    return;
                }
            }
            Folder errorFolder = null;
            if (StringUtils.isNotBlank(errorFolderName))
            {
                errorFolder = store.getFolder(errorFolderName);
                if (!errorFolder.exists())
                {
                    log.error("Error folder {} does not exist.", errorFolderName);
                    return;
                }
            }

            inputFolder.open(Folder.READ_WRITE);

            // fetch messages
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            Message[] msgs = inputFolder.getMessages();
            inputFolder.fetch(msgs, fp);

            // process each message
            log.debug("About to process {} messages", msgs.length);
            List<Message> successMessages = new ArrayList<Message>();
            List<Message> errorMessages = new ArrayList<Message>();
            List<Message> discardMessages = new ArrayList<Message>();
            for (int i = 0; i < msgs.length; i++)
            {
                Message msg = msgs[i];

                try
                {
                    boolean processed = messageProcessor.execute(msg);
                    (processed ? successMessages : discardMessages).add(msg);
                }
                catch (Exception ex)
                {
                    log.error("Failed to process email message.", ex);
                    errorMessages.add(msg);
                }
            }

            // move and purge messages
            if (successFolder != null && !successMessages.isEmpty())
            {
                inputFolder.copyMessages(successMessages.toArray(new Message[successMessages.size()]), successFolder);
            }
            if (discardFolder != null && !discardMessages.isEmpty())
            {
                inputFolder.copyMessages(discardMessages.toArray(new Message[discardMessages.size()]), discardFolder);
            }
            if (errorFolder != null && !errorMessages.isEmpty())
            {
                inputFolder.copyMessages(errorMessages.toArray(new Message[errorMessages.size()]), errorFolder);
            }
            for (int i = 0; i < msgs.length; i++)
            {
                msgs[i].setFlag(Flag.DELETED, true);
            }

            // close folder
            inputFolder.close(true);
        }
        catch (MessagingException ex)
        {
            log.error("Error ingesting email.", ex);
        }
        catch (Exception ex)
        {
            log.error("Error ingesting email.", ex);
        }
        finally
        {
            // close store
            try
            {
                store.close();
            }
            catch (MessagingException ex)
            {
                log.error("Error closing message store.", ex);
            }
        }
    }
}
