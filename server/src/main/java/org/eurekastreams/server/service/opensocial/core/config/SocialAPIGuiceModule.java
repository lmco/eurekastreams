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
package org.eurekastreams.server.service.opensocial.core.config;

import org.apache.shindig.social.core.config.SocialApiGuiceModule;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.service.opensocial.oauth.OAuthDataStoreImpl;
import org.eurekastreams.server.service.opensocial.oauth.SocialRealm;
import org.eurekastreams.server.service.opensocial.spi.ActivityServiceImpl;
import org.eurekastreams.server.service.opensocial.spi.AppDataServiceImpl;
import org.eurekastreams.server.service.opensocial.spi.MessageServiceImpl;
import org.eurekastreams.server.service.opensocial.spi.PersonServiceImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.inject.name.Names;
import com.google.inject.spring.SpringIntegration;

/**
 * Guice module to wire up Eureka Streams implementatino of Shindig OpenSocial endpoints.
 * 
 */
public class SocialAPIGuiceModule extends SocialApiGuiceModule
{
    /**
     * Override method to configure the guice injection.
     */
    @Override
    protected void configure()
    {
        super.configure();
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath*:conf/applicationContext-container.xml");
        bind(BeanFactory.class).toInstance(appContext);

        bind(ActivityService.class).to(ActivityServiceImpl.class);
        bind(AppDataService.class).to(AppDataServiceImpl.class);
        bind(PersonService.class).to(PersonServiceImpl.class);
        bind(MessageService.class).to(MessageServiceImpl.class);

        bind(OAuthDataStore.class).to(OAuthDataStoreImpl.class);

        requestStaticInjection(SocialRealm.class);

        bind(ServiceAction.class).annotatedWith(Names.named("getPersonNoContext")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getPersonNoContext"));

        bind(ServiceAction.class).annotatedWith(Names.named("getPeopleByOpenSocialIds")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getPeopleByOpenSocialIds"));
        bind(ServiceAction.class).annotatedWith(Names.named("getAppData")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getAppData"));
        bind(ServiceAction.class).annotatedWith(Names.named("updateAppData")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "updateAppData"));
        bind(ServiceAction.class).annotatedWith(Names.named("deleteAppData")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "deleteAppData"));
        bind(TaskHandlerAction.class).annotatedWith(Names.named("deleteUserActivities")).toProvider(
                SpringIntegration.fromSpring(TaskHandlerAction.class, "deleteUserActivities"));

        // ActivityServiceImpl wirings
        bind(ServiceAction.class).annotatedWith(Names.named("getUserActivities")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getUserActivities"));
        bind(ServiceActionController.class).toProvider(
                SpringIntegration.fromSpring(ServiceActionController.class, "serviceActionController"));
        bind(TaskHandlerServiceAction.class).annotatedWith(Names.named("postPersonActivityServiceActionTaskHandler"))
                .toProvider(
                        SpringIntegration.fromSpring(TaskHandlerServiceAction.class,
                                "postPersonActivityServiceActionTaskHandler"));
        bind(OpenSocialPrincipalPopulator.class).toProvider(
                SpringIntegration.fromSpring(OpenSocialPrincipalPopulator.class, "openSocialPrincipalPopulator"));
    }
}
