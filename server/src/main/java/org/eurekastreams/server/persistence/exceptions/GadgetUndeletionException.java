/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.exceptions;

/**
 * Exception thrown when a problem occurs when the user tries to undelete a Gadget.
 * 
 */
public class GadgetUndeletionException extends Exception
{

    /**
     * Serializer version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The the gadget id the user attempted to undelete.
     */
    private long gadgetId;
   
    /**
     * Constructor. 
     * @param message The message.
     * @param inGadgetId The gadget id.
     */
    public GadgetUndeletionException(final String message, final long inGadgetId)
    {
        super(message);
        
        this.gadgetId = inGadgetId;
    }

    /**
     * Get the gadget id the user attempted to undelete.
     * 
     * @return the gadget id the user attempted to undelete.
     */
    public long getGadgetId()
    {
        return gadgetId;
    }
}
