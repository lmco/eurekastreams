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
package org.eurekastreams.web.client.events;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;

/**
 * The Event Bus.
 *
 */
public class EventBus
{
    /**
     * The singleton.
     */
    private static EventBus singleton = null;

    /** Logger. */
    private Logger log;

    /** Tracks ID to use for logging event notifications. */
    private static int lastNotifyRunId;

    /**
     * Last fired events.
     */
    @SuppressWarnings("unchecked")
    private final HashMap<Class, Object> lastFiredEvent = new HashMap<Class, Object>();
    /**
     * Stores the observers.
     */
    @SuppressWarnings("unchecked")
    private HashMap<Class, List<Observer< ? >>> observerHandlers = new HashMap<Class, List<Observer< ? >>>();

    /**
     * Buffered observers.
     */
    @SuppressWarnings("unchecked")
    private HashMap<Class, List<Observer< ? >>> bufferedObserverHandlers = new HashMap<Class, List<Observer< ? >>>();

    /**
     * Gets an instance of the event bus.
     *
     * @return the event bus.
     */
    public static EventBus getInstance()
    {
        if (singleton == null)
        {
            singleton = new EventBus();
        }

        return singleton;
    }

    /**
     * Constructor.
     */
    public EventBus()
    {
        // This setup is to handle unit testing. GWT Logging uses GWT.Create to set up the logging implementation,
        // however GWT.Create is not available when running unit tests. (It returns null if GWTMockUtilities.disarm is
        // called or throws an exception if not. Thus during unit tests calling LogConfiguration.loggingIsEnabled throws
        // a NullPointerException.
        try
        {
            if (LogConfiguration.loggingIsEnabled())
            {
                log = Logger.getLogger("org.eurekastreams.web.client.events.EventBus");
            }
        }
        catch (NullPointerException ex)
        {
            // redundant, but keeps checkstyle happy
            log = null;
        }
    }

    /**
     * Adds an observer.
     *
     * @param event
     *            the event.
     * @param observer
     *            the observer.
     */
    public void addObserver(final Object event, final Observer< ? > observer)
    {
        addObserver(event.getClass(), observer);
    }

    /**
     * Buffer off the current observers.
     */
    public void bufferObservers()
    {
        bufferedObserverHandlers = cloneEventMap(observerHandlers);
    }

    /**
     * Restore the buffered observers.
     */
    public void restoreBufferedObservers()
    {
        observerHandlers = cloneEventMap(bufferedObserverHandlers);
    }

    /**
     * Clones an event map. Cannot use Map.clone() since it is a shallow copy and does not clone the lists, thus using
     * the same list objects in the clone as in the original, resulting in observers added after the buffering to be
     * retained after the restore.
     *
     * @param map
     *            Event map to clone.
     * @return Cloned map.
     */
    @SuppressWarnings("unchecked")
    private HashMap<Class, List<Observer< ? >>> cloneEventMap(final HashMap<Class, List<Observer< ? >>> map)
    {
        HashMap<Class, List<Observer< ? >>> newMap = new HashMap<Class, List<Observer< ? >>>(map.size());

        for (Entry<Class, List<Observer< ? >>> entry : map.entrySet())
        {
            newMap.put(entry.getKey(), new LinkedList<Observer< ? >>(entry.getValue()));
        }

        return newMap;
    }

    /**
     * Adds an observer.
     *
     * @param event
     *            the event class.
     * @param observer
     *            the observer.
     * @param fireLastEvent
     *            fire the last known event of that type.
     */
    public void addObserver(final Class< ? extends Object> event, final Observer< ? > observer,
            final Boolean fireLastEvent)
    {
        addObserver(event, observer);

        if (fireLastEvent)
        {
            fireLastEvent(event, observer);
        }
    }

    /**
     * Fire the last event.
     *
     * @param event
     *            the event type.
     * @param observer
     *            the observer to fire.
     */
    @SuppressWarnings("unchecked")
    private void fireLastEvent(final Class< ? extends Object> event, final Observer observer)
    {
        Object lastEvent = lastFiredEvent.get(event);

        if (lastEvent != null)
        {
            observer.update(lastEvent);
        }
    }

    /**
     * Adds an observer to many events.
     *
     * @param events
     *            the event classes
     * @param observer
     *            the observer.
     */
    public void addObservers(final Observer observer, final Class... events)
    {
        for (Class event : events)
        {
            addObserver(event, observer);
        }
    }

    /**
     * Adds an observer.
     *
     * @param event
     *            the event class.
     * @param observer
     *            the observer.
     */
    public void addObserver(final Class< ? extends Object> event, final Observer< ? > observer)
    {
        if (!observerHandlers.containsKey(event))
        {
            List<Observer< ? >> observers = new LinkedList<Observer< ? >>();
            observerHandlers.put(event, observers);
        }

        observerHandlers.get(event).add(observer);
    }

    /**
     * Removes an observer.
     *
     * @param event
     *            the event for which to remove the observer.
     * @param observer
     *            the observer.
     */
    public void removeObserver(final Object event, final Observer< ? > observer)
    {
        removeObserver(event.getClass(), observer);
    }

    /**
     * Removes an observer.
     *
     * @param event
     *            the event class.
     * @param observer
     *            the observer.
     */
    public void removeObserver(final Class< ? extends Object> event, final Observer< ? > observer)
    {
        final List<Observer< ? >> list = observerHandlers.get(event);
        if (list != null)
        {
            list.remove(observer);
        }
    }

    /**
     * Remove all observers.
     */
    public void clear()
    {
        observerHandlers.clear();
    }

    /**
     * Notifies the observers that subscribe to an event.
     *
     * @param event
     *            the event.
     */
    @SuppressWarnings("unchecked")
    public void notifyObservers(final Object event)
    {
        int thisRunId = ++lastNotifyRunId;
        boolean shouldLog = log != null && log.isLoggable(Level.FINE);

        lastFiredEvent.put(event.getClass(), event);
        List<Observer< ? >> observers = observerHandlers.get(event.getClass());

        if (observers == null || observers.isEmpty())
        {
            if (shouldLog)
            {
                log.fine("[" + thisRunId + "] Notify with event " + event.getClass().getName() + "  " + event
                        + " for no observers.");
            }
        }
        else
        {
            if (shouldLog)
            {
                log.fine("[" + thisRunId + "] Starting notify with event " + event.getClass().getName() + "  " + event
                        + " for " + observers.size() + " observers.");
            }

            for (Observer observer : observers)
            {
                if (shouldLog)
                {
                    log.fine("[" + thisRunId + "] Notifying observer " + observer.getClass().getName() + "  "
                            + observer);
                }

                // TODO: Should catch here and do something with it so that an exception in one observer doesn't cause
                // the others to not run
                observer.update(event);
            }

            if (shouldLog)
            {
                log.fine("[" + thisRunId + "] Finishing notify");
            }
        }
    }

}
