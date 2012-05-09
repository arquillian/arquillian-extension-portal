/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.pc.arquillian.bootstrap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.portlet.aspects.CCPPInterceptor;
import org.gatein.pc.portlet.aspects.ConsumerCacheInterceptor;
import org.gatein.pc.portlet.aspects.ContextDispatcherInterceptor;
import org.gatein.pc.portlet.aspects.EventPayloadInterceptor;
import org.gatein.pc.portlet.aspects.PortletCustomizationInterceptor;
import org.gatein.pc.portlet.aspects.ProducerCacheInterceptor;
import org.gatein.pc.portlet.aspects.RequestAttributeConversationInterceptor;
import org.gatein.pc.portlet.aspects.SecureTransportInterceptor;
import org.gatein.pc.portlet.aspects.ValveInterceptor;
import org.gatein.pc.portlet.container.ContainerPortletDispatcher;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.impl.deployment.PortletApplicationDeployer;
import org.gatein.pc.portlet.impl.state.StateConverterV0;
import org.gatein.pc.portlet.impl.state.StateManagementPolicyService;
import org.gatein.pc.portlet.impl.state.producer.PortletStatePersistenceManagerService;
import org.gatein.pc.portlet.state.producer.ProducerPortletInvoker;
import org.gatein.wci.ServletContainer;
import org.gatein.wci.ServletContainerFactory;
import org.gatein.wci.impl.DefaultServletContainerFactory;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class PortalContainerInitializer implements ServletContextListener {
    private static final String APPLICATION_DEPLOYER_KEY = "jboss.portal:service=PortletApplicationDeployer";

    public void contextInitialized(ServletContextEvent sce) {
        try {
            boostrap(sce.getServletContext());
        } catch (Exception e) {
            System.err.println("Could not boostrap test server");
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        PortletApplicationDeployer deployer = (PortletApplicationDeployer) sce.getServletContext().getAttribute(
                APPLICATION_DEPLOYER_KEY);
        if (deployer != null)
            deployer.stop();
    }

    public void boostrap(ServletContext ctx) throws Exception {

        PortletApplicationDeployer deployer = new PortletApplicationDeployer();
        ctx.setAttribute(APPLICATION_DEPLOYER_KEY, deployer);

        ServletContainerFactory containerFactory = DefaultServletContainerFactory.getInstance();
        deployer.setServletContainerFactory(containerFactory);
        ctx.setAttribute("jboss.portal:service=ServletContainerFactory", containerFactory);

        ServletContainer container = containerFactory.getServletContainer();
        ctx.setAttribute("jboss.portal:service=ServletContainer", container);

        PortletStatePersistenceManagerService persistence = new PortletStatePersistenceManagerService();
        ctx.setAttribute("jboss.portal:service=ProducerPersistenceManager", persistence);

        StateManagementPolicyService state = new StateManagementPolicyService();
        state.setPersistLocally(true);
        ctx.setAttribute("jboss.portal:service=ProducerStateManagementPolicy", state);

        StateConverterV0 stateConverter = new StateConverterV0();
        ctx.setAttribute("jboss.portal:service=ProducerStateConverter", stateConverter);

        PortletInvokerInterceptor portletIcpt = new PortletInvokerInterceptor();
        ctx.setAttribute("jboss.portal:service=ConsumerPortletInvoker", portletIcpt);
        ctx.setAttribute("ConsumerPortletInvoker", portletIcpt);

        ConsumerCacheInterceptor cacheIcpt = new ConsumerCacheInterceptor();
        portletIcpt.setNext(cacheIcpt);
        ctx.setAttribute("jboss.portal:service=ConsumerCacheInterceptor", cacheIcpt);

        PortletCustomizationInterceptor customizeIcpt = new PortletCustomizationInterceptor();
        cacheIcpt.setNext(customizeIcpt);
        ctx.setAttribute("jboss.portal:service=PortletCustomizationInterceptor", customizeIcpt);

        ProducerPortletInvoker ppi = new ProducerPortletInvoker();
        ppi.setPersistenceManager(persistence);
        ppi.setStateManagementPolicy(state);
        ppi.setStateConverter(stateConverter);
        customizeIcpt.setNext(ppi);
        ctx.setAttribute("jboss.portal:service=ProducerPortletInvoker", ppi);

        ContainerPortletInvoker cpi = new ContainerPortletInvoker();
        ppi.setNext(cpi);
        deployer.setContainerPortletInvoker(cpi);
        ctx.setAttribute("jboss.portal:service=ContainerPortletInvoker", cpi);

        ValveInterceptor vi = new ValveInterceptor();
        vi.setPortletApplicationRegistry(deployer);
        cpi.setNext(vi);
        ctx.setAttribute("jboss.portal:service=ValveInterceptor", vi);

        SecureTransportInterceptor sti = new SecureTransportInterceptor();
        vi.setNext(sti);
        ctx.setAttribute("jboss.portal:service=SecureTransportInterceptor", sti);

        ContextDispatcherInterceptor ci = new ContextDispatcherInterceptor();
        ci.setServletContainerFactory(containerFactory);
        sti.setNext(ci);
        ctx.setAttribute("jboss.portal:service=ContextDispatcherInterceptor", ci);

        ProducerCacheInterceptor pci = new ProducerCacheInterceptor();
        ci.setNext(pci);
        ctx.setAttribute("jboss.portal:service=ProducerCacheInterceptor", pci);

        CCPPInterceptor ccppi = new CCPPInterceptor();
        pci.setNext(ccppi);
        ctx.setAttribute("jboss.portal:service=CCPPInterceptor", ccppi);

        RequestAttributeConversationInterceptor raci = new RequestAttributeConversationInterceptor();
        ccppi.setNext(raci);
        ctx.setAttribute("jboss.portal:service=RequestAttributeConversationInterceptor", raci);

        EventPayloadInterceptor epi = new EventPayloadInterceptor();
        raci.setNext(epi);
        ctx.setAttribute("jboss.portal:service=EventPayloadInterceptor", epi);

        ContainerPortletDispatcher dispatcher = new ContainerPortletDispatcher();
        epi.setNext(dispatcher);
        ctx.setAttribute("jboss.portal:service=PortletContainerDispatcher", dispatcher);

        // don't forget to start the deployer - MC automatically calls start() method, here we do it manually
        deployer.start();
    }
}
