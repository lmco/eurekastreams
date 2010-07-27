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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;


/**
 * A form element that takes the name of a javascript command to run to get the value.
 * Genius really, who thinks of this stuff?
 *
 */
public class JSNICommandFormElement implements FormElement
{
	/**
	 * The key.
	 */
	private String key;

	/**
	 * javascript command name.
	 */
	private static String jSNICommand;

	/**
	 * Return the key.
	 * @return the key.
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Get the value.
	 * @return the value.
	 */
	public Serializable getValue()
	{
		return runJSNICommand(jSNICommand);
	}

	/**
	 * The javascript itself.
	 * @param commandName the command name to execute.
	 * @return the value from the command.
	 */
	private static native String runJSNICommand(final String commandName)
    /*-{
         if ($wnd[commandName] != null)
         {
            try
            {
                return $wnd[commandName]();
            }
            catch(ex)
            {
                return "";
            }
         }

     	 return "";
    }-*/;

	/**
	 * Default constructor.
	 * @param inKey the key.
	 * @param inJSNICommand the command.
	 */
	public JSNICommandFormElement(final String inKey, final String inJSNICommand)
	{
		key = inKey;
		jSNICommand = inJSNICommand;
	}

	/**
	 * What COULD I do here?
	 * @param errMessage the message that I'll do nothing with...
	 */
	public void onError(final String errMessage)
	{
		// Nothing cause I'm hidden
	}

	/**
	 * What COULD I do here?
	 */
	public void onSuccess()
	{
		// Nothing cause I'm hidden.
	}

}
