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
package org.eurekastreams.taskqueueprocessor.console;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The MainApp class is the name for the console application that will support the Spring context
 *  that loads up the listener container and the listener beans.
 */
public final class MainApp 
{
	/**
	 *  A private non-default constructor for this utility class.
	 *  Per CheckStyle, a utility class must not have a public or default
	 *   constructor.
	 */
	private MainApp()
	{
		
	}
	
	/**
	 * The console application execution entry point.
	 * 
	 * @param args  Argument array passed in during invocation
	 */
	public static void main(final String[] args) 
	{
		/**
		 * The private log variable
		 */
		Logger logger = Logger.getLogger(MainApp.class);
		
	    logger.debug("MainApp starting...");

		logger.debug("   loading the Spring application context...");
		// Specifies and loads the Spring application context.
		// This will automatically load/start the JMS listener container and all of the
		//  listeners therein.
        @SuppressWarnings("unused")
		final ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("conf/applicationContext.xml");
        
        logger.debug("   loaded the application context.");	    		
	}

}
