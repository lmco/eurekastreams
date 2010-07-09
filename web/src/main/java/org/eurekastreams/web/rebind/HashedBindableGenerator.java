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
package org.eurekastreams.web.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates the property hash map.
 */
public class HashedBindableGenerator extends Generator
{
    /**
     * The fields in the object.
     */
    private JField[] fields;

    /**
     * The package name the object is in.
     */
    private String packageName;

    /**
     * The class name of the object.
     */
    private String simpleClassName = null;

    /**
     * The proxy object class name.
     */
    private String proxyClassName;

    /**
     * Generates the code.
     * 
     * @param logger
     *            the logger to use.
     * @param context
     *            context for generation.
     * @param typeName
     *            the object type.
     * @throws UnableToCompleteException
     *             thrown on generation failure.
     * 
     * @return the code as a string.
     */
    public String generate(final TreeLogger logger,
            final GeneratorContext context, final String typeName)
            throws UnableToCompleteException
    {
        try
        {
            TypeOracle typeOracle = context.getTypeOracle();
            JClassType requestedClass = typeOracle.getType(typeName);

            simpleClassName = requestedClass.getSimpleSourceName();
            packageName = requestedClass.getPackage().getName();
            proxyClassName = simpleClassName + "HashedProxy";
            fields = requestedClass.getFields();

            String qualifiedProxyClassName = packageName + "." + proxyClassName;

            SourceWriter writer = null;

            PrintWriter printWriter = context.tryCreate(logger, packageName,
                    proxyClassName);

            if (printWriter != null)
            {
                ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
                        packageName, proxyClassName);

                composerFactory.addImport("java.util.HashMap");
                composerFactory.addImport("java.util.Set");

                composerFactory.addImport("org.eurekastreams.web.client.ui.Bindable");

                composerFactory
                        .addImport("org.eurekastreams.web.client.ui.HashedBindable");
                composerFactory.setSuperclass("HashedBindable");

                writer = composerFactory.createSourceWriter(context,
                        printWriter);
            }

            // If the SourceWriter object is null, then the new class that we
            // are trying
            // to create already exists, therefore we just need to return back
            // the name of the new class
            if (writer == null)
            {
                return qualifiedProxyClassName;
            } 
            else
            {

                writeConstructor(logger, simpleClassName, writer);
                writer.commit(logger);
                return qualifiedProxyClassName;
            }
        } 
        catch (NotFoundException e)
        {
            throw new UnableToCompleteException();
        }
    }

    /**
     * Writes the constructor source code.
     * 
     * @param logger
     *            the logger to use.
     * @param className
     *            the name of the class.
     * @param writer
     *            the writter for the code.
     */
    public void writeConstructor(final TreeLogger logger, final String className,
            final SourceWriter writer)
    {
        writer
                .println("private HashMap<String,Object> map = new HashMap<String,Object>();");
        writer.println("private Bindable bindable;");
        writer.println("public " + proxyClassName + "()");
        writer.println("{");
        writer.println("}");
        writer.println("public void populateHash(Bindable unHashedObject)");
        writer.println("{");
        writer.println("bindable = unHashedObject;");
        for (JField field : fields)
        {
            if (!field.isPrivate())
            {
                writer.println("map.put(\"" + field.getName() + "\", (("
                        + simpleClassName + ")unHashedObject)."
                        + field.getName() + ");");
            }
        }
        writer.println("}");
        writer.println("public Object get(String key)");
        writer.println("{");
        writer.println("return map.get(key);");
        writer.println("}");
        writer.println("public Set<String> getFields()");
        writer.println("{");
        writer.println("return map.keySet();");
        writer.println("}");
        writer.println("public void set(String key, Object w)");
        writer.println("{");
        for (JField field : fields)
        {
            if (!field.isPrivate())
            {
                writer.println("if(key == \"" + field.getName() + "\")");
                writer.println("{");
                writer.println("((" + simpleClassName + ")bindable)."
                        + field.getName() + " = ("
                        + field.getType().getQualifiedSourceName() + ")w;");
                writer.println("}");
            }
        }

        writer.println("}");

    }

}
