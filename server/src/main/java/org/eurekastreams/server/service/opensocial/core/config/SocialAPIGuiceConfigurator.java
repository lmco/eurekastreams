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

import java.util.List;
import java.util.Set;

import org.apache.shindig.auth.AnonymousAuthenticationHandler;
import org.apache.shindig.auth.AuthenticationHandler;
import org.apache.shindig.common.servlet.ParameterFetcher;
import org.apache.shindig.protocol.DataServiceServletFetcher;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.protocol.conversion.BeanXStreamConverter;
import org.apache.shindig.protocol.conversion.xstream.XStreamConfiguration;
import org.apache.shindig.social.core.oauth.AuthenticationHandlerProvider;
import org.apache.shindig.social.core.util.BeanXStreamAtomConverter;
import org.apache.shindig.social.core.util.xstream.XStream081Configuration;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.service.ActivityHandler;
import org.apache.shindig.social.opensocial.service.AppDataHandler;
import org.apache.shindig.social.opensocial.service.MessageHandler;
import org.apache.shindig.social.opensocial.service.PersonHandler;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.eurekastreams.server.service.opensocial.oauth.OAuthDataStoreImpl;
import org.eurekastreams.server.service.opensocial.oauth.SocialRealm;
import org.eurekastreams.server.service.opensocial.spi.ActivityServiceImpl;
import org.eurekastreams.server.service.opensocial.spi.AppDataServiceImpl;
import org.eurekastreams.server.service.opensocial.spi.MessageServiceImpl;
import org.eurekastreams.server.service.opensocial.spi.PersonServiceImpl;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.spring.SpringIntegration;

/**
 * Wire up Eureka Streams implementation of Shindig OpenSocial endpoints in Guice.
 *
 */
public class SocialAPIGuiceConfigurator implements SpringGuiceConfigurator
{
    /**
     * Configuration value for the Service Expiration.
     */
    private static final Long SERVICE_EXPIRATION_IN_MINS = 60L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(final Binder inBinder, final ApplicationContext inAppContext)
    {
        inBinder.bind(ParameterFetcher.class).annotatedWith(Names.named("DataServiceServlet")).to(
                DataServiceServletFetcher.class);

        inBinder.bind(Boolean.class).annotatedWith(Names.named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
                .toInstance(Boolean.TRUE);
        inBinder.bind(XStreamConfiguration.class).to(XStream081Configuration.class);
        inBinder.bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.xml")).to(
                BeanXStreamConverter.class);
        inBinder.bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.json")).to(
                BeanJsonConverter.class);
        inBinder.bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.atom")).to(
                BeanXStreamAtomConverter.class);

        inBinder.bind(new TypeLiteral<List<AuthenticationHandler>>()
        {
        }).toProvider(AuthenticationHandlerProvider.class);

        Multibinder<Object> handlerBinder =
                Multibinder.newSetBinder(inBinder, Object.class, Names.named("org.apache.shindig.handlers"));
        for (Class handler : getHandlers())
        {
            handlerBinder.addBinding().toInstance(handler);
        }
        inBinder.bind(Long.class).annotatedWith(Names.named("org.apache.shindig.serviceExpirationDurationMinutes"))
                .toInstance(SERVICE_EXPIRATION_IN_MINS);

        inBinder.bind(ActivityService.class).to(ActivityServiceImpl.class);
        inBinder.bind(AppDataService.class).to(AppDataServiceImpl.class);
        inBinder.bind(PersonService.class).to(PersonServiceImpl.class);
        inBinder.bind(MessageService.class).to(MessageServiceImpl.class);

        inBinder.bind(OAuthDataStore.class).to(OAuthDataStoreImpl.class);

        inBinder.requestStaticInjection(SocialRealm.class);

        inBinder.bind(ServiceAction.class).annotatedWith(Names.named("getPeopleByOpenSocialIds")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getPeopleByOpenSocialIds"));
        inBinder.bind(ServiceAction.class).annotatedWith(Names.named("getFollowing")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getFollowing"));
        inBinder.bind(ServiceAction.class).annotatedWith(Names.named("getAppData")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getAppData"));
        inBinder.bind(ServiceAction.class).annotatedWith(Names.named("updateAppData")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "updateAppData"));
        inBinder.bind(ServiceAction.class).annotatedWith(Names.named("deleteAppData")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "deleteAppData"));
        inBinder.bind(TaskHandlerAction.class).annotatedWith(Names.named("deleteUserActivities")).toProvider(
                SpringIntegration.fromSpring(TaskHandlerAction.class, "deleteUserActivities"));

        // ActivityServiceImpl wirings
        inBinder.bind(ServiceAction.class).annotatedWith(Names.named("getUserActivities")).toProvider(
                SpringIntegration.fromSpring(ServiceAction.class, "getUserActivities"));
        inBinder.bind(ActionController.class).toProvider(
                SpringIntegration.fromSpring(ServiceActionController.class, "serviceActionController"));
        inBinder.bind(TaskHandlerServiceAction.class).annotatedWith(
                Names.named("postPersonActivityServiceActionTaskHandler")).toProvider(
                SpringIntegration.fromSpring(TaskHandlerServiceAction.class,
                        "postPersonActivityServiceActionTaskHandler"));
        inBinder.bind(OpenSocialPrincipalPopulator.class).toProvider(
                SpringIntegration.fromSpring(OpenSocialPrincipalPopulator.class, "openSocialPrincipalPopulator"));
        inBinder.bind(PrincipalPopulatorTransWrapper.class).toProvider(
                SpringIntegration.fromSpring(PrincipalPopulatorTransWrapper.class,
                        "openSocialPrincipalPopulatorTransWrapper"));
        inBinder.bind(GadgetDefinitionMapper.class).annotatedWith(Names.named("jpaGadgetDefinitionMapper"))
                .toProvider(SpringIntegration.fromSpring(GadgetDefinitionMapper.class, "jpaGadgetDefinitionMapper"));
        inBinder.bind(GadgetMetaDataFetcher.class).toProvider(
                SpringIntegration.fromSpring(GadgetMetaDataFetcher.class, "gadgetMetaDataHttpFetcher"));
    }

    /**
     * Hook to provide a Set of request handlers. Subclasses may override to add or replace additional handlers.
     *
     * @return Set of Handlers.
     */
    protected Set<Class<?>> getHandlers()
    {
        return ImmutableSet.<Class<?>> of(ActivityHandler.class, AppDataHandler.class, PersonHandler.class,
                MessageHandler.class);
    }
}
