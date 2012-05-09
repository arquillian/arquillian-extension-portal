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
package org.gatein.integration.jboss.as7;

import org.gatein.integration.jboss.as7.catalina.CatalinaEventHandler;
import org.jboss.as.web.WebServer;
import org.jboss.as.web.WebSubsystemServices;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class GateInService implements Service<GateInService> {

    static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append("gatein", "testportal");

    private final InjectedValue<WebServer> webServer = new InjectedValue<WebServer>();

    private CatalinaEventHandler handler;

    public static void addService(final ServiceTarget serviceTarget) {
        final GateInService gateInService = new GateInService();

        ServiceBuilder<GateInService> serviceBuilder = serviceTarget.addService(GateInService.SERVICE_NAME, gateInService);
        serviceBuilder.addDependency(WebSubsystemServices.JBOSS_WEB, WebServer.class, gateInService.getWebServer());
        serviceBuilder.install();
    }

    @Override
    public synchronized GateInService getValue() throws IllegalStateException {
        return this;
    }

    @Override
    public synchronized void start(StartContext context) throws StartException {
        handler = new CatalinaEventHandler(webServer.getValue().getServer());
        handler.start();
    }

    @Override
    public synchronized void stop(StopContext context) {
        if (null != handler) {
            handler.stop();
            handler = null;
        }
    }

    public Injector<WebServer> getWebServer() {
        return webServer;
    }

}
