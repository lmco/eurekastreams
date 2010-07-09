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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for FindSystemSettingsMapper class.
 *
 */
public class FindSystemSettingsMapperUnitTest
{
   /**
    * mock context.
    */
   private final Mockery context = new JUnit4Mockery()
   {
       {
           setImposteriser(ClassImposteriser.INSTANCE);
       }
   };

   /**
    * Test execute method.
    */
   @SuppressWarnings("unchecked")
   @Test
   public void testExecute()
   {
       final EntityManager entityManager = context.mock(EntityManager.class);
       final QueryOptimizer queryOptimizer = context.mock(QueryOptimizer.class);
       final MapperRequest req = context.mock(MapperRequest.class);
       final Query query = context.mock(Query.class);
       final SystemSettings systemSettings = context.mock(SystemSettings.class);
       final String displayName = "display name";
       final String shortName = "shortName";

       FindSystemSettings sut = new FindSystemSettings();
       sut.setEntityManager(entityManager);
       sut.setQueryOptimizer(queryOptimizer);

       context.checking(new Expectations()
       {
           {
               oneOf(entityManager).createQuery(with(any(String.class)));
               will(returnValue(query));

               oneOf(query).getSingleResult();
               will(returnValue(systemSettings));
               
               oneOf(systemSettings).getSupportStreamGroupShortName();
               will(returnValue(shortName));

               oneOf(entityManager).createQuery(with(any(String.class)));
               will(returnValue(query));
               
               oneOf(query).setParameter(shortName, shortName);
               will(returnValue(query));

               oneOf(query).getSingleResult();
               will(returnValue(displayName));
               
               oneOf(systemSettings).setSupportStreamGroupDisplayName(displayName);
          }
       });

       assertTrue(sut.execute(req) == systemSettings);
       context.assertIsSatisfied();
   }

}
