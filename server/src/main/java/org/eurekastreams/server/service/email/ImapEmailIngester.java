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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
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

    /** For sending response emails. */
    private final EmailerFactory emailerFactory;

    /**
     * Constructor.
     *
     * @param inStoreFactory
     *            For getting a connection to the mail server.
     * @param inMessageProcessor
     *            Does the actual work on each message.
     * @param inEmailerFactory
     *            For sending response emails.
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
            final EmailerFactory inEmailerFactory,
            final String inInputFolderName, final String inErrorFolderName, final String inSuccessFolderName,
            final String inDiscardFolderName)
    {
        storeFactory = inStoreFactory;
        messageProcessor = inMessageProcessor;
        emailerFactory = inEmailerFactory;
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
            // Note: Not preloading CONTENT_INFO. For some reason, preloading the content info (IMAP BODYSTRUCTURE)
            // causes the call to getContent to return empty. (As if there was a bug where getContent saw the cached
            // body structure and thought that the content itself was cached, but I'd think a bug like that would have
            // been found by many people and fixed long ago, so I'm assuming it's something else.)
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            Message[] msgs = inputFolder.getMessages();
            inputFolder.fetch(msgs, fp);

            // process each message
            log.debug("About to process {} messages", msgs.length);
            List<Message> successMessages = new ArrayList<Message>();
            List<Message> errorMessages = new ArrayList<Message>();
            List<Message> discardMessages = new ArrayList<Message>();
            List<Message> responseMessages = new ArrayList<Message>();
            for (int i = 0; i < msgs.length; i++)
            {
                Message message = msgs[i];

                // TEMPORARY DEBUG ONLY
                dumpMessage(message, i);

                try
                {
                    boolean processed = messageProcessor.execute(message, responseMessages);
                    (processed ? successMessages : discardMessages).add(message);
                }
                catch (Exception ex)
                {
                    log.error("Failed to process email message.", ex);
                    errorMessages.add(message);
                }
            }

            // send response messages
            for (Message responseMessage : responseMessages)
            {
                emailerFactory.sendMail(responseMessage);
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

    /**
     * TEMPORARY debug code.
     * 
     * @param message
     *            The message.
     * @param index
     *            Index.
     */
    protected void dumpMessage(final Message message, final int index)
    {
        System.out.printf("--------------------------%nMESSAGE, i=%d%n", index);
        try
        {
            dumpEnvelope(message);
            dumpParts(message, 0);
            dumpPartContent(message, "0");
        }
        catch (Exception ex)
        {
            System.out.printf("Error dumping message: " + ex);
        }
        System.out.printf("--------------------------%n");
    }

    /**
     * TEMPORARY debug code.
     * 
     * @param m
     *            The message.
     * @throws Exception
     *             Perhaps.
     */
    private static void dumpEnvelope(final Message m) throws Exception
    {
        dumpAddresses("FROM", m.getFrom());
        dumpAddresses("TO", m.getRecipients(Message.RecipientType.TO));
        dumpAddresses("CC", m.getRecipients(Message.RecipientType.CC));
        dumpAddresses("BCC", m.getRecipients(Message.RecipientType.BCC));
        dumpAddresses("REPLY-TO", m.getReplyTo());

        // SUBJECT
        p("SUBJECT: " + m.getSubject());

        // DATE
        Date d = m.getSentDate();
        p("SendDate: " + (d != null ? d.toString() : "UNKNOWN"));
    }

    /**
     * TEMPORARY debug code.
     * 
     * @param part
     *            Part to dump.
     * @param level
     *            Indent level.
     * @throws MessagingException
     *             Perhaps.
     * @throws IOException
     *             Perhaps.
     */
    private static void dumpParts(final Part part, final int level) throws MessagingException, IOException
    {
        ContentType contentType;
        String baseContentType;
        try
        {
            contentType = new ContentType(part.getContentType());
            baseContentType = contentType.getBaseType();
        }
        catch (Exception ex)
        {
            contentType = null;
            baseContentType = null;
        }
        p(level, "Content type: %s,  Disposition: %s,  Size: %d,  Lines:  %d", baseContentType, part.getDisposition(),
                part.getSize(), part.getLineCount());
        p(level, "Description: %s, Full content type:  '%s'", part.getDescription(), part.getContentType());

        if (contentType != null)
        {
            if ("multipart".equals(contentType.getPrimaryType()))
            {
                Object content = part.getContent();
                if (content instanceof Multipart)
                {
                    Multipart mp = (Multipart) content;

                    if (!part.getContentType().equals(mp.getContentType()))
                    {
                        p(level, "WARNING:  Content type of Multipart object does not match parent part:  '%s'",
                                mp.getContentType());
                    }

                    int count = mp.getCount();
                    p(level, "Parts:  %d", count);
                    for (int i = 0; i < count; i++)
                    {
                        p(level + 1, "Part %d", i);
                        dumpParts(mp.getBodyPart(i), level + 2);
                    }
                }
                else
                {
                    p(level, "WARNING: multipart content has an object of type %s", content.getClass().getName());
                }
            }
            else if ("message/rfc822".equals(baseContentType))
            {
                Object content = part.getContent();
                p(level, "Headers:");
                for (Enumeration e = part.getAllHeaders(); e.hasMoreElements();)
                {
                    Header h = (Header) e.nextElement();
                    p(level + 1, "%s=%s", h.getName(), h.getValue());
                }
            }
        }
    }

    /**
     * TEMPORARY debug code.
     *
     * @param part
     *            Part to dump.
     * @param prefix
     *            Printing prefix.
     * @throws MessagingException
     *             Perhaps.
     * @throws IOException
     *             Perhaps.
     */
    private static void dumpPartContent(final Part part, final String prefix) throws MessagingException, IOException
    {
        ContentType contentType;
        String contentTypeS;
        try
        {
            contentType = new ContentType(part.getContentType());
            contentTypeS = contentType.getBaseType();
        }
        catch (Exception ex)
        {
            contentType = null;
            contentTypeS = null;
        }
        if (contentType != null && "multipart".equals(contentType.getPrimaryType()))
        {
            Object content = part.getContent();
            if (content instanceof Multipart)
            {
                Multipart mp = (Multipart) content;
                int count = mp.getCount();
                for (int j = 0; j < count; j++)
                {
                    BodyPart bp = mp.getBodyPart(j);
                    String newPrefix = prefix + "." + j;
                    dumpPartContent(bp, newPrefix);
                }
            }
        }
        else
        {
            if ("text".equals(contentType.getPrimaryType()))
            {
                System.out.printf("%nPart %s, %s, %d bytes, %d lines:%n", prefix, contentTypeS, part.getSize(),
                        part.getLineCount());
                String partContent = part.getContent().toString();
                System.out.printf("content (%d chars)>>%n%s%n---%n", partContent.length(), partContent);
                // System.out.printf("data handler>>%n");
                // part.getDataHandler().writeTo(System.out);
                // System.out.printf("---%n");
            }
        }
    }

    /**
     * TEMPORARY debug code.
     * 
     * @param label
     *            label.
     * @param addresses
     *            addresses.
     */
    private static void dumpAddresses(final String label, final Address[] addresses)
    {
        if (addresses != null)
        {
            for (int j = 0; j < addresses.length; j++)
            {
                p(label + ": " + dumpAddress(addresses[j]));
            }
        }
    }

    /**
     * TEMPORARY debug code.
     * 
     * @param a
     *            Address.
     * @return String.
     */
    private static String dumpAddress(final Address a)
    {
        if (a instanceof InternetAddress)
        {
            InternetAddress ia = (InternetAddress) a;
            return String.format("'%s' -> '%s' '%s'", ia, ia.getAddress(), ia.getPersonal());
        }
        return String.format("'%s' (type %s)", a, a.getClass().getName());
    }

    /**
     * TEMPORARY debug code.
     * 
     * @param level
     *            Indent level.
     * @param fmt
     *            Format.
     * @param args
     *            Arguments.
     */
    private static void p(final int level, final String fmt, final Object... args)
    {
        System.out.print("                                               ".substring(0, level * 4));
        System.out.printf(fmt, args);
        System.out.println();
    }

    /**
     * TEMPORARY debug code.
     * 
     * @param fmt
     *            Format.
     * @param args
     *            Arguments.
     */
    private static void p(final String fmt, final Object... args)
    {
        System.out.printf(fmt, args);
        System.out.println();
    }

}
